/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

package codestripper;

import java.io.IOException;
import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class PathLocationsTest {

    //@Disabled("think TDD")
    @Test @DisplayName( "test that empty strings can produce real paths" )
    public void testWorkDir() throws IOException {
        var x = new PathLocations( "", "" );

        assertThat( x.realOut() ).exists();
        assertThat( x.realWork() ).exists();
        assertThat( x.inWorkFile( "pom.xml" ) ).exists();

        fail( "method WorkDir reached end. You know what to do." );
    }
}