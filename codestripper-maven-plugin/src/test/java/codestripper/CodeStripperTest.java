/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package codestripper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.*;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
@Disabled
public class CodeStripperTest {

    Path pwd = Path.of( System.getProperty( "user.dir" ) );
    Path outDir = pwd.resolve( "target" ).resolve( "naked" );
    Log log = new SystemStreamLog();

    //@Disabled("think TDD")
    @Test @DisplayName( "some story line" )
    public void testTestStripper() throws IOException {

        var stripper = new CodeStripper( log, outDir ).extraResources( List.of(
                "../README.md", "../images" ) );
        stripper.strip( pwd );

        assertThat( outDir.toFile() ).exists();
        assertThat( outDir.resolve( "assessment" ).toFile() ).exists();

        fail( "method TestStripper reached end. You know what to do." );
    }

}
