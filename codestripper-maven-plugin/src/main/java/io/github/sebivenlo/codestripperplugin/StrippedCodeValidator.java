/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.github.sebivenlo.codestripperplugin;

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
import java.util.stream.Stream;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Validates that the stripped code is compilable. It does this by running 'mvn
 * test-compile' on the target/out directory.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
@Mojo( name = "validate-stripped-code",
        defaultPhase = LifecyclePhase.NONE )
public class StrippedCodeValidator extends AbstractMojo {

    @Parameter( defaultValue = "${project}", required = true, readonly = true )
    MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Log log = getLog();

        log.info( "start stripped code validation" );

        /*
         make compile outdir in temp
         javac -d outdir file -p classpath -cp classpath filestocompile
         for test compile,
         */
        try {
            validate( log );
        } catch ( IOException | InterruptedException | DependencyResolutionRequiredException ex ) {
            getLog().error( ex.getMessage() );
        }
    }

    Pattern problematicFile = Pattern.compile( "(?<file>.*):\\d+: error:.*" );

    void validate(Log log) throws InterruptedException, DependencyResolutionRequiredException, IOException {
        getDependencies();
        outDir = makeOutDir();
        Path srcDir = Path.of( "target", "stripper-out", "src" );
        String[] args = makeCompilerArguments( srcDir, outDir );
        ProcessBuilder pb = new ProcessBuilder( args );
        getLog()
                .info( "validating " + validatedClassCount + " stripped classes" );
        Process process = pb.start();
        BufferedReader reader
                = new BufferedReader( new InputStreamReader(
                        process.getErrorStream() )
                //                        process                        .getInputStream() )
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
            getLog().info( "all stripped files passed compiler test" );
        } else {
            getLog().info(
                    "\033[31;1mCompiling the stipped files causes some compiler errors\033[m" );
            Arrays.stream( sourceFiles )
                    .forEach( l -> {
                        if ( problematicFiles.contains( l ) ) {
                            log.error( "\033[1;31m" + l + "\033[m" );
                        } else {
                            log.info( "\033[1m" + l + "\033[m" );
                        }
                    } );

            for ( String s : compilerOutput ) {
                log.error( s );
            }
        }

        log.info( "exited validate-stripped-code with exit code " + exitCode );
    }

    Path outDir;

    Path makeOutDir() throws IOException {
        outDir = Files.createTempDirectory( "cs-target" );
        outDir.toFile().deleteOnExit();
        return outDir;
    }

    static final String pathSep = System.getProperty( "path.separator" );

    String[] makeCompilerArguments(Path sourceDir, Path outDir
    ) throws
            DependencyResolutionRequiredException {
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
                        .peek( f -> System.out.println( "source " + f ) )
                        .map( Path::toString )
                        .toArray( String[]::new );
            } catch ( IOException ignored ) {
            }
            sourceFiles = result;
        }
        return sourceFiles;
    }

    /**
     * Used in tests to avoid reflection.
     *
     * @param p project (or mock) to insert
     */
    void setProject(MavenProject p) {
        this.project = p;
    }

    // cache
    private String sneakyClassPath;

    String getSneakyClassPath() {

        if ( null == sneakyClassPath ) {
            String result = "";
            try {
                ProcessBuilder pb = new ProcessBuilder( "mvn",
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
            } catch ( IOException | InterruptedException ex ) {
                getLog().error( ex );
            }
            sneakyClassPath = result;
        }
        return sneakyClassPath;
    }

    /**
     * Used by the maven framework.
     */
    public StrippedCodeValidator() {
    }

    void getDependencies() throws DependencyResolutionRequiredException {
        Log log = getLog();
        log.info( "===== depedencies ======" );
        if ( null == project ) {
            return;
        }
        project.getTestClasspathElements().stream()
                //                .map( Dependency::toString )
                .forEach( s -> log.info( "class path element " + s ) );
    }
}
