package io.github.sebivenlo.codestripperplugin;

import codestripper.CodeStripper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import static java.util.stream.Collectors.joining;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assumptions.assumeThat;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.*;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class StrippedCodeValidatorTest extends StrippedCodeValidator {

    static Path pwd = Path.of( System.getProperty( "user.dir" ) );
    static Log log = new SystemStreamLog();

    public StrippedCodeValidatorTest() {
        super( Path.of( "target", "stripper-out", "assignment", pwd
                .getFileName().toString() ), log );
        try {
            outDir = Files.createTempDirectory( "codestripper-" + getClass()
                    .getSimpleName() + "-tests-" );
        } catch ( IOException ex ) {
            log.error( ex.getMessage() );
        }
    }

    //@Disabled("think TDD")
    @Test
    @DisplayName( "Get source files" )
    public void testGetSourceFiles() {
        Path sourceDir = pwd.resolve( Path.of( "src" ) );
        String[] sourceFiles = this.getSourceFiles( sourceDir );

        // massage the inpo paths
        var actual = Arrays.asList( sourceFiles )
                .stream().map( l -> pwd.relativize( Path.of( l ) ) )
                .toList();
        var expected = List.of(
                Path.of(
                        "src/main/java/io/github/sebivenlo/codestripperplugin/StrippedCodeValidator.java" ),
                Path.of(
                        "src/test/java/io/github/sebivenlo/codestripperplugin/StrippedCodeValidatorTest.java" )
        );
//        System.out.println( "sourceFiles = " + Arrays.toString( sourceFiles ) );
        assertThat( actual ).containsAll( expected );
//        fail( "method SourceFiles reached end. You know what to do." );
    }

//    @Disabled( "think TDD" )
    @Test
    @DisplayName( "compiler args " )
    public void testCompilerArgs() throws DependencyResolutionRequiredException, IOException {
        Path sourceDir = Path.of( "src" );
        String[] args = this.makeCompilerArguments( sourceDir, makeOutDir() );
        // cleanup
        this.outDir.toFile().delete();
        System.out.println( Arrays.stream( args ).collect( joining( " " ) ) );
        assertThat( args ).isNotEmpty();
//        fail( "method CompilerArgs reached end. You know what to do." );
    }

//    @Disabled( "think TDD" )
    @Test
    @DisplayName( "run the compiler" )
    public void testCompilerRun() throws IOException {

        CodeStripper stripper = new CodeStripper( new SystemStreamLog(), outDir );
        stripper = stripper.extraResources( List
                .of( "../README.md", "../images" ) );
        var strippedProject = stripper.strip( pwd );
        System.out.println( "strippedProject = " + strippedProject );
        assumeThat( strippedProject.resolve( "src" ) ).exists();
        System.out.println( "strippedProject = " + strippedProject.toString() );
        ThrowableAssert.ThrowingCallable code = () -> {
            this.validate();
        };

        assertThatCode( code ).doesNotThrowAnyException();
//        fail( "method CompilerRun reached end. You know what to do." );
    }
}
