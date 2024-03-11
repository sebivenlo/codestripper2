/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package codestripper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assumptions.assumeThat;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
//@Disabled
@TestMethodOrder( OrderAnnotation.class )
public class CodeStripperTest extends StripperTestBase {

    final CodeStripper stripper;

    public CodeStripperTest() {
        stripper = new CodeStripper.Builder()
                .logger( log )
                .pathLocations( locations )
                .extraResources( List.of(
                        "../README.md", "../images" ) )
                .build();
    }

    @Order( 1 )
    //@Disabled("think TDD")
    @Test @DisplayName( "check that the files land in the proper archive" )
    public void testProperOutput() throws IOException {
        System.out.println( "outDir = " + locations.out() );
        Path roadKill = Path.of( "src",
                "test",
                "java",
                "codestripper",
                "StripperRoadKill.java" );
        System.out.println( "src = " + roadKill );
//        Path source = pwd.resolve( src );
        assumeThat( roadKill ).exists();
        stripper.logLevel = LoggerLevel.MUTE;
        Path output = stripper.strip( locations.work() );
        assertThat( output ).exists();
        System.out.println( "expandedArchive = " + output );
        Path stripped = output.resolve( roadKill );
        System.out.println( "stripped = " + stripped );
        assertThat( stripped ).as( " expecting dir with src to exists " )
                .exists();
//        fail( "method ProperOutput reached end. You know what to do." );
    }

    @Order( 2 )
    //@Disabled("think TDD")
    @Test @DisplayName( "stripped files are smaller" )
    public void testTestThatStripperStrips() throws IOException {
        Path roadKill = Path.of( "src",
                "test",
                "java",
                "codestripper",
                "StripperRoadKill.java" );
        long size1 = Files.size( roadKill );
        stripper.logLevel = LoggerLevel.FINE;
        stripper.strip( locations.work() );
        Path expandedArchive = locations.out().resolve( "expandedArchive" );
        System.out.println( "expandedArchive = " + expandedArchive );
        Path strippedRoadKill = expandedArchive.resolve( roadKill );
        assumeThat( strippedRoadKill ).exists();
        long size2 = Files.size( strippedRoadKill );
        assertThat( size2 ).isLessThan( size1 );

//        fail( "method TestThatStripperStrips reached end. You know what to do." );
    }

    //@Disabled("think TDD")
    @Test @DisplayName( "test that files land in proper place" )
    public void testFilesLandAtProperPlace() throws IOException {
        assumeThat( Path.of( "pom.xml" ) ).exists();
        var stripper = new CodeStripper.Builder()
                .logger( log )
                .pathLocations( locations )
                .build();
        stripper.logLevel = LoggerLevel.FINE;
        Path output = stripper.strip( locations.work() );
        System.out.println( "outDir = " + output );
        assertThat( output.resolve( "pom.xml" ) ).exists();

//        fail( "method FilesLandAtProperPlace reached end. You know what to do." );
    }

    //@Disabled("think TDD")
    @Test @DisplayName( "are assignment files such as pom.xml adde properly" )
    public void testAddAssignmentFiles() throws IOException {
        assumeThat( Path.of( "pom.xml" ) ).exists();
        stripper.logLevel = LoggerLevel.FINE;
        Path output = stripper.strip( locations.work() );
        System.out.println( "output = " + output );
        assumeThat( output ).exists();

        assertThat( output.resolve( "pom.xml" ) ).exists();

        fail( "method AddAssignmentFiles reached end. You know what to do." );
    }

    @Override
    public void cleanup() throws IOException {
//        super.cleanup(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }

}
