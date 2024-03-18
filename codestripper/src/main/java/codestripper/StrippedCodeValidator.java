/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codestripper;

import io.github.sebivenlo.dependencyfinder.DependencyFinder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;
import java.util.stream.Stream;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import loggerwrapper.Logger;
import static loggerwrapper.LoggerLevel.ERROR;
//import org.apache.maven.plugin.logging.Log;

/**
 * Validates that the stripped code is compilable. It does this by running 'mvn
 * test-compile' on the target/out directory.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class StrippedCodeValidator {

    final Pattern problematicFile = Pattern.compile(
            "(?<file>.*):\\d+: error:.*" );

    final PathLocations locations;
    final Logger log;
    final Path expandedProject;

    /**
     * Configure the validator with logger and locations.
     *
     * @param log to use
     * @param locations where is the work?
     */
    public StrippedCodeValidator(Logger log, PathLocations locations) {
        this.locations = locations;
        this.log = log;
        expandedProject = this.locations.strippedProject();

    }

    /**
     * Do the work.
     *
     * @throws CodeStripperValidationException when the compiler is not silent.
     */
    public void validate() throws CodeStripperValidationException {
        String strippedPrefix = locations.strippedProject()
                .toAbsolutePath()
                .toString() + pathSep;
        try {
            Path compilerOutDir = makeOutDir();
            Path srcDir = locations.strippedProject()
                    .resolve( "src" );
            if ( srcDir.startsWith( locations.work() ) ) {
                log.info( () -> "srcDir = " + locations.workRelative( srcDir ) );
            } else {
                log.info( () -> "srcDir = " + srcDir );
            }
            String[] args = makeCompilerArguments( srcDir, compilerOutDir );
            String[] compilerOptions = compilerOptions( compilerOutDir );
            String[] sourceFiles = getSourceFiles( srcDir );
            List<String> compilerOutput = new ArrayList<>();
            final Set<Path> problematicFiles = new HashSet<>();
            log.info(
                    () -> "validating " + validatedClassCount + " stripped classes" );
            int exitCode = runCompiler( compilerOptions, sourceFiles,
                    problematicFiles, compilerOutput );
            if ( compilerOutput.isEmpty() ) {
                log.info( () -> "all stripped files passed compiler test" );
            } else {
                log.info( ()
                        -> "\033[31;1mCompiling the stipped files causes some compiler errors\033[m" );
                Arrays.stream( sourceFiles )
                        .map( this::relFile )
                        .forEach( l -> {
                            if ( problematicFiles.contains( l ) ) {
                                log.error( () -> "\033[1;31m" + l + "\033[m" );
                            } else {
                                log.info( () -> "\033[32m" + l + "\033[m" );
                            }
                        } );
                compilerOutput.replaceAll(
                        l -> l.startsWith( strippedPrefix ) ? l.substring(
                        strippedPrefix.length() ) : l );
                throw new CodeStripperValidationException(
                        compilerOutput.stream()
                                .collect( joining( "\n" ) ),
                        "The validator found compilation errors" );
            }

            log.info(
                    () -> "exited validate-stripped-code with exit code " + exitCode );
        } catch ( IOException | InterruptedException ex ) {
            log.error( () -> ex.getMessage() );
        }
    }

    int runCompiler(String[] options, String[] sourceFiled,
            final Set<Path> problematicFiles,
            List<String> compilerOutput) throws InterruptedException, IOException {
        String[] args = concat( new String[]{ "java" }, concat( options,
                sourceFiles ) );
        ProcessBuilder pb = new ProcessBuilder( args );
        Process process = pb.start();
        BufferedReader reader
                = new BufferedReader( new InputStreamReader(
                        process.getErrorStream() )
                );
        String line;
        while ( ( line = reader.readLine() ) != null ) {
            Matcher matcher = problematicFile.matcher( line );
            if ( matcher.matches() ) {
                problematicFiles.add( relFile( matcher.group( "file" ) ) );
            }
            compilerOutput.add( line );
        }
        int exitCode = process.waitFor();
        return exitCode;
    }

    int runCompilerAlt(String[] options, String[] souceFiles,
            final Set<Path> problematicFiles,
            List<String> compilerOutput) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(
                null, null, null );
        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();

        JavaCompiler.CompilationTask task = compiler.getTask(
                null, //errowriter null =stderr
                null, // filemanager null is standard
                collector, // diagnostics
                List.of( options ),
                null,
                fileManager.getJavaFileObjectsFromStrings( List
                        .of( sourceFiles ) )
        );
        Boolean success = task.call();
        for ( Diagnostic<? extends JavaFileObject> diagnostic : collector
                .getDiagnostics() ) {
            log.info( () -> "diagnostic = " + diagnostic );
            Diagnostic.Kind kind = diagnostic.getKind();
            String file = diagnostic.getSource()
                    .toString();
            if ( kind == Diagnostic.Kind.ERROR ) {
                compilerOutput.add( diagnostic.toString() );
                problematicFiles.add( Path.of( file ) );
            }
        }

        return success ? 0 : 1;
    }

    private Path relFile(String l) {
        return expandedProject.relativize( Path.of( l )
                .toAbsolutePath() );

    }

    static Path makeOutDir() throws IOException {
        Path result = Files.createTempDirectory( "cs-StrippedCodeValidator-" );
        result.toFile()
                .deleteOnExit();
        return result;
    }

    static final String pathSep = System.getProperty( "path.separator" );

    String[] makeCompilerArguments(Path sourceDir, Path outDir) {
        String[] sources = getSourceFiles( sourceDir );
        validatedClassCount = sources.length;
        String[] opts = compilerOptions( outDir );
        return concat( opts, sources );
    }

    static String[] concat(String[] opts, String[] sources) {
        String[] allOpts = Arrays.copyOf( opts, opts.length + sources.length );
        System.arraycopy( sources, 0, allOpts, opts.length, sources.length );
        return allOpts;
    }

    String[] compilerOptions(Path outDir) {
        String compileClassPath = getSneakyClassPath();
        String[] opts = {
            "-p", compileClassPath,
            "-sourcepath", "src/main/java" + pathSep + "src/test/java",
            "-cp", compileClassPath,
            "-d", outDir.toString() };
        return opts;
    }

    private int validatedClassCount = 0;

    boolean isJavaFile(Path p) {
        return p.getFileName()
                .toString()
                .endsWith(
                        ".java" );
    }

    private String[] sourceFiles = null;

    String[] getSourceFiles(Path startDir) {
        if ( null == sourceFiles ) {
            String[] result = {};
            try ( Stream<Path> stream = Files.walk( startDir,
                    Integer.MAX_VALUE ) ) {
                result = stream
                        .filter( path -> !Files.isDirectory( path ) )
                        .filter( this::isJavaFile )
                        .map( Path::toString )
                        .toArray( String[]::new );
            } catch ( IOException ignored ) {
            }
            sourceFiles = result;
        }
        return sourceFiles;
    }

    // cache
    private String sneakyClassPath;

    String getCachedClassPath(Path f) throws IOException {
        return Files.lines( f )
                .collect( joining( pathSep ) );
    }

    String getSneakyClassPath() {
        sneakyClassPath = DependencyFinder.testCompileclassPath();
//        return sneakyClassPath;

        if ( null == sneakyClassPath ) {
            String result = "";
            try {
                Path classPathCache = locations.expandedArchive()
                        .resolve(
                                "classpath-cache.txt" );
                if ( Files.exists( classPathCache ) ) {
                    return getCachedClassPath( classPathCache );
                }

                // if not, get and fill cache.
                String pom = locations.work()
                        .resolve( "pom.xml" )
                        .toAbsolutePath()
                        .toString();
                ProcessBuilder pb = new ProcessBuilder( "mvn",
                        "-f",
                        pom,
                        "dependency:build-classpath" );
                Process process = pb.start();
                BufferedReader reader
                        = new BufferedReader(
                                new InputStreamReader( process.getInputStream() ) );

                String line;
                while ( ( line = reader.readLine() ) != null ) {
                    if ( !line.startsWith( "[INFO]" ) ) {
                        result += line;
                    }
                }
                int exitCode = process.waitFor();
                Files.write( classPathCache, List.of( result ) );
            } catch ( IOException | InterruptedException ex ) {
                log.error( () -> ex.getMessage() );
            }
            sneakyClassPath = result;
        }
        return sneakyClassPath;
    }

}
