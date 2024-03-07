package io.github.sebivenlo.codestripperplugin;

import codestripper.CodeStripper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import static java.util.stream.Collectors.joining;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import static org.assertj.core.api.Assertions.*;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.*;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class StrippedCodeValidatorTest extends StrippedCodeValidator {

    Path pwd = Path.of( System.getProperty( "user.dir" ) );

    public StrippedCodeValidatorTest() {

        try {
            outDir = Files.createTempDirectory( "codestripper-" + getClass()
                    .getSimpleName() + "-tests-" );
        } catch ( IOException ex ) {
            getLog().error( ex.getMessage() );
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

        CodeStripper stripper = new CodeStripper( getLog(), outDir )
                .extraResources( List.of( "..README.md", "../images" ) );
        stripper.strip( pwd );
        ThrowableAssert.ThrowingCallable code = () -> {
            this.execute();
        };

        assertThatCode( code ).doesNotThrowAnyException();
//        fail( "method CompilerRun reached end. You know what to do." );
    }
}
