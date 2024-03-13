/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package codestripper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assumptions.assumeThat;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class PathLocationsTest extends StripperTestBase {

    //@Disabled("think TDD")
    @Test @DisplayName( "test that empty strings can produce real paths" )
    public void testWorkDir() throws IOException {
        assertThat( locations.out() ).exists();
        assertThat( locations.work() ).exists();
        assertThat( locations.work().resolve( "pom.xml" ) ).exists();

//        fail( "method WorkDir reached end. You know what to do." );
    }

    //@Disabled("think TDD")
    @Test @DisplayName( "test that stuff in out dir is reliabily created" )
    public void testCreating() throws IOException {
        assertThat( locations.out().toString() )
                .contains( this.getClass().getSimpleName() );
//        fail( "method Creating reached end. You know what to do." );
    }

    //@Disabled("think TDD")
    @DisplayName( "ensure that illagal params cause trouble" )
    @ParameterizedTest
    @CsvSource( {
        // work,     out,  assiName, project,  expected       ,  words in msg
        "none,         none,      a,       p,  IllegalArgument, work|should|already|exists",
        "existing,     none,      a,       p,  IllegalArgument, out|should|already|exists",
        "existing, readonly,      a,       p,  IllegalArgument, not|writable",
        "writable, writable,      a,       p,  IllegalArgument, out|work|should|be|different",
        "writable,   parent,      a,       p,  IllegalArgument, work|should|not|be|a|child|of|out" //
    } )
    public void testVerifyParams(String workPath, String outPath,
            String assignmentName, String projectName,
            String except,
            String words
    ) {
        Path tmp = Path.of( System.getProperty( "java.io.tmpdir" ) );
        assumeThat( tempDir ).startsWith( tmp );
        String exception = except.trim();
        String[] requiredWords = words.trim().split( "\\|" );
        final Path none = Path.of( "nix" );
        Path pwd = Path.of( System.getProperty( "user.dir" ) );
        Map<String, Path> selectedPath = Map.of(
                "existing", pwd,
                "writable", tempDir,
                "readonly", Path.of( "/" ),
                "none", Path.of( "nix" ),
                "parent", tmp
        );

        ThrowingCallable code = () -> {
            Path workp = selectedPath.get( workPath );
            Path outp = selectedPath.get( outPath );
            new PathLocations( log, workp, outp );
        };

        if ( exception.isEmpty() ) {
            assertThatCode( code ).doesNotThrowAnyException();
        } else {
            assertThatThrownBy( code )
                    .isExactlyInstanceOf( IllegalArgumentException.class )
                    .hasMessageContainingAll( requiredWords );
        }
//        fail( "method VerifyParams reached end. You know what to do." );
    }
}
