package io.github.sebivenlo.codestripperplugin;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
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

    //@Disabled("think TDD")
    @Test @DisplayName( "Get source files" )
    public void testGetSourceFiles() {
        Path sourceDir = Path.of( "src" );
        String[] sourceFiles = this.getSourceFiles( sourceDir );
//        System.out.println( "sourceFiles = " + Arrays.toString( sourceFiles ) );
        assertThat( sourceFiles ).contains(
                "src/main/java/io/github/sebivenlo/codestripperplugin/StrippedCodeValidator.java",
                "src/test/java/io/github/sebivenlo/codestripperplugin/StrippedCodeValidatorTest.java"
        );
//        fail( "method SourceFiles reached end. You know what to do." );
    }

    //@Disabled("think TDD")
    @Test @DisplayName( "compiler args " )
    public void testCompilerArgs() throws DependencyResolutionRequiredException, IOException {
        Path sourceDir = Path.of( "src" );
        String[] args = this.makeCompilerArguments( sourceDir, makeOutDir() );
        // cleanup
        this.outDir.toFile().delete();
        System.out.println( Arrays.stream( args ).collect( joining( " " ) ) );
        assertThat( args ).isNotEmpty();
//        fail( "method CompilerArgs reached end. You know what to do." );
    }

    //@Disabled("think TDD")
    @Test @DisplayName( "run the compiler" )
    public void testCompilerRun() throws IOException {

        codestripper.CodeStripper.main( new String[]{} );
        ThrowableAssert.ThrowingCallable code = () -> {
            this.execute();
        };

        assertThatCode( code ).doesNotThrowAnyException();
//        fail( "method CompilerRun reached end. You know what to do." );
    }
}
