/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package codestripper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class PathLocationsTest extends StripperTestBase {

    final Log logger;

    public PathLocationsTest() {
        logger = new SystemStreamLog();
    }

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
    @DisplayName( "test that illagal params cause trouble" )
    @ParameterizedTest
    @CsvSource( {
        "none,         none,  IllegalArgument, work|should|already|exists",
        "existing,     none,  IllegalArgument, out|should|already|exists",
        "existing, readonly,  IllegalArgument, writable",
        "existing, writable,   ''            ,''"
    } )
    public void testVerifyParams(String workPath, String outPath,
            String except,
            String words
    ) {
        String exception = except.trim();
        String[] requiredWords = words.split( "\\|" );
        final Path none = Path.of( "nix" );
        Path pwd = Path.of( System.getProperty( "user.dir" ) );
        Map<String, Path> selectedPath = Map.of(
                "existing", pwd,
                "writable", tempDir,
                "readonly", Path.of( "/" ),
                "none", Path.of( "nix" )
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
