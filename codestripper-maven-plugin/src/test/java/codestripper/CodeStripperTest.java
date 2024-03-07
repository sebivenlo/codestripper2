/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package codestripper;

import java.io.IOException;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.*;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
//@Disabled
public class CodeStripperTest extends StripperTestBase {

    //@Disabled("think TDD")
    @Test @DisplayName( "test the whole codestripper" )
    public void testTestStripper() throws IOException {
        System.out.println( "codestripper result in " + outDir.toString() );
        var stripper = new CodeStripper( log, outDir ).extraResources( List.of(
                "../README.md", "../images" ) );
        stripper.strip( pwd );

        assertThat( outDir.toFile() ).exists();
        assertThat( outDir.resolve( "assignment" ).toFile() ).exists();

//        fail( "method TestStripper reached end. You know what to do." );
    }

    @AfterEach
    public void cleanup() {
    }
}
