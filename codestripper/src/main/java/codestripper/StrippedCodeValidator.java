/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codestripper;

import codestripper.PathLocations;
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
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;
import java.util.stream.Stream;
import loggerwrapper.Logger;
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

    PathLocations locations;
    Logger log;

    public StrippedCodeValidator(Logger log, PathLocations locations) {
        this.locations = locations;
        this.log = log;
    }

    public void validate() throws CodeStripperValidationException {
        try {
            Path compilerOutDir = makeOutDir();
            Path srcDir = locations.strippedProject().resolve( "src" );
            if ( srcDir.startsWith( locations.work() ) ) {
                log.info( () -> "srcDir = " + locations.workRelative( srcDir ) );
            } else {
                log.info( () -> "srcDir = " + srcDir );
            }
            String[] args = makeCompilerArguments( srcDir, compilerOutDir );
            ProcessBuilder pb = new ProcessBuilder( args );
            log.info(
                    () -> "validating " + validatedClassCount + " stripped classes" );
            Process process = pb.start();
            BufferedReader reader
                    = new BufferedReader( new InputStreamReader(
                            process.getErrorStream() )
                    );
            String line;
            List<String> compilerOutput = new ArrayList<>();
            final Set<String> problematicFiles = new HashSet<>();
            while ( ( line = reader.readLine() ) != null ) {
                Matcher matcher = problematicFile.matcher( line );
                if ( matcher.matches() ) {
                    problematicFiles.add( matcher.group( "file" ) );
                }
                compilerOutput.add( line );
            }

            int exitCode = process.waitFor();
            if ( compilerOutput.isEmpty() ) {
                log.info( () -> "all stripped files passed compiler test" );
            } else {
                log.info( ()
                        -> "\033[31;1mCompiling the stipped files causes some compiler errors\033[m" );
                Arrays.stream( sourceFiles )
                        .forEach( l -> {
                            if ( problematicFiles.contains( l ) ) {
                                log.error( () -> "\033[1;31m" + l + "\033[m" );
                            } else {
                                log.info( () -> "\033[1m" + l + "\033[m" );
                            }
                        } );

                for ( String s : compilerOutput ) {
                    log.error( () -> s );
                }
                throw new CodeStripperValidationException( compilerOutput
                        .stream()
                        .collect( joining( "\n" ) ),
                        "The validator found compilation errors" );
            }

            log.info(
                    () -> "exited validate-stripped-code with exit code " + exitCode );
        } catch ( IOException | InterruptedException ex ) {
            log.error( () -> ex.getMessage() );
        }
    }

    static Path makeOutDir() throws IOException {
        Path result = Files.createTempDirectory( "cs-StrippedCodeValidator-" );
        result.toFile().deleteOnExit();
        return result;
    }

    static final String pathSep = System.getProperty( "path.separator" );

    String[] makeCompilerArguments(Path sourceDir, Path outDir) {
        String[] sources = getSourceFiles( sourceDir );
        validatedClassCount = sources.length;
        String compileClassPath = getSneakyClassPath();

        String[] opts = {
            "javac",
            "-p", compileClassPath,
            "-sourcepath", "src/main/java" + pathSep + "src/test/java",
            "-cp", compileClassPath,
            "-d", outDir.toString() };
        String[] allOpts = Arrays.copyOf( opts, opts.length + sources.length );
        System.arraycopy( sources, 0, allOpts, opts.length, sources.length );
        return allOpts;
    }

    private int validatedClassCount = 0;

    boolean isJavaFile(Path p) {
        return p.getFileName().toString().endsWith(
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
                        .peek( f -> log.info(
                        () -> "validating \033[34m" + locations
                                .workRelative( f ).toString() + "\033[m" ) )
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
        return Files.lines( f ).collect( joining( pathSep ) );
    }

    String getSneakyClassPath() {

        if ( null == sneakyClassPath ) {
            String result = "";
            try {
                Path classPathCache = locations.expandedArchive().resolve(
                        "classpath-cache.txt" );
                if ( Files.exists( classPathCache ) ) {
                    return getCachedClassPath( classPathCache );
                }

                // if not, get and fill cache.
                String pom = locations.work().resolve( "pom.xml" )
                        .toAbsolutePath().toString();
                ProcessBuilder pb = new ProcessBuilder( "mvn",
                        "-f",
                        pom,
                        "dependency:build-classpath" );
                Process process = pb.start();
                BufferedReader reader
                        = new BufferedReader(
                                new InputStreamReader( process.getInputStream() ) );

                String line = "";
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
