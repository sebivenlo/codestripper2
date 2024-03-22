package codestripper;

import static codestripper.Zipper.lineSep;
import io.github.sebivenlo.dependencyfinder.DependencyFinder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import static java.util.stream.Collectors.joining;
import java.util.stream.Stream;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import loggerwrapper.Logger;
//import org.apache.maven.plugin.logging.Log;

/**
 * Validates that the stripped code is compilable. It does this by running 'mvn
 * test-compile' on the target/out directory.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class StrippedCodeValidator {

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

    final ConcurrentMap<Path, Diagnostic> problematicFiles = new ConcurrentHashMap<>();

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
            if (srcDir.startsWith( locations.work() )) {
                log.info( () -> "srcDir = " + locations.workRelative( srcDir ) );
            } else {
                log.info( () -> "srcDir = " + srcDir );
            }
            var compilerOptions = compilerOptions( srcDir, compilerOutDir );
            var sourceFiles = getSourceFiles( srcDir );
            List<String> compilerOutput = new ArrayList<>();
            log.info(
                    () -> "validating " + validatedClassCount + " stripped classes" );
            int exitCode = runCompilerAlt( compilerOptions, sourceFiles,
                    problematicFiles, compilerOutput );
            if (problematicFiles.isEmpty()) {
                log.info( () -> "all stripped files passed compiler test" );
            } else {
                log.info( ()
                        -> "\033[31;1mCompiling the stipped files causes some compiler errors\033[m" );
                sourceFiles.stream()
                        .map( this::relFile )
                        .forEach( this::logDiagnostic );
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
        } catch (IOException ex) {
            log.error( () -> ex.getMessage() );
        }
    }

    void logDiagnostic(Path l) {
        if (problematicFiles.containsKey( l )) {
            log
                    .error( () -> "\033[1;31m" + l + "\033[m" );
            Diagnostic diagnostic = problematicFiles
                    .get( l );
            log.error( () -> diagnostic.getMessage(
                    Locale
                            .getDefault() ) );
            log
                    .error( () -> Objects.toString(
                    diagnostic
                            .getCode() ) );
            log.error(
                    () -> "at line " + diagnostic
                            .getLineNumber()
                    + ", column " + diagnostic
                            .getColumnNumber() );
            List<String> problematicSource = getProblematicSource(
                    diagnostic );
            for (String string : problematicSource) {
                log.error( () -> string );
            }
        } else {
            log.info(
                    () -> "Okay: \033[32;2m" + l + "\033[m" );
        }
    }

    List<String> getProblematicSource(Diagnostic<? extends JavaFileObject> d) {
        String pathStart = locations.strippedProject()
                .toString() + fileSep;
        int len = pathStart.length();

        return Stream.of( d.toString()
                .split( lineSep ) )
                .map( s -> {
                    if (s.startsWith( pathStart )) {
                        return s.substring( len );
                    } else {
                        return "\033[33m" + s + "\033[m";
                    }
                } )
                .toList();
    }

    int runCompilerAlt(List<String> options, List<String> souceFiles,
            final Map<Path, Diagnostic> problematicFiles,
            List<String> compilerOutput) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(
                null, null, null );
        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
        JavaCompiler.CompilationTask task = compiler.getTask(
                null, //errowriter null =stderr
                null, // filemanager null is standard
                collector, // diagnostics
                options,
                null, // agent classes
                fileManager.getJavaFileObjectsFromStrings( sourceFiles ) );
        Boolean success = task.call();
        for (Diagnostic<? extends JavaFileObject> diagnostic : collector
                .getDiagnostics()) {
            Diagnostic.Kind kind = diagnostic.getKind();
            var p = diagnostic.getSource();
            var q = p.toUri()
                    .getPath();
            if (fileSep.equals( "\\" )) {
                // assume windows, then first character is misplaced
                q = q.substring( 1 );
            }
            var file = Path.of( q )
                    .toAbsolutePath();
            final var fileR = locations
                    .strippedProject()
                    .relativize( file );
            compilerOutput.add( diagnostic.getMessage( Locale.getDefault() ) );
            problematicFiles.put( fileR, diagnostic );
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
    static final String fileSep = System.getProperty( "file.separator" );

    List<String> compilerOptions(Path projectDir, Path outDir) {
        String compileClassPath = getClassPath();
        String sourcePath = sourcePath( projectDir );
        var opts = List.of(
                "-p", compileClassPath,
                "-sourcepath", sourcePath,
                "-cp", compileClassPath,
                "-d", outDir.toString() );
        return opts;
    }

    String sourcePath(Path projectDir) {
        String sourcePath = projectDir.resolve( "src/main/java" ) + pathSep
                + projectDir.resolve( "src/test/java" );
        return sourcePath;
    }

    private int validatedClassCount = 0;

    boolean isJavaFile(Path p) {
        return p.getFileName()
                .toString()
                .endsWith(
                        ".java" );
    }

    private List<String> sourceFiles = null;

    List<String> getSourceFiles(Path startDir) {
        if (null == sourceFiles) {
            List<String> result = List.of();
            try (Stream<Path> stream = Files.walk( startDir,
                    Integer.MAX_VALUE )) {
                result = stream
                        .filter( path -> !Files.isDirectory( path ) )
                        .filter( this::isJavaFile )
                        .map( Path::toString )
                        .toList();
            } catch (IOException ignored) {
            }
            sourceFiles = result;
            validatedClassCount = result.size();
        }
        return sourceFiles;
    }

    // cache
    private String classPath = null;

    String getClassPath() {
        if (null == classPath) {
            classPath = DependencyFinder.testCompileclassPath();
        }
        return classPath;
    }
}
