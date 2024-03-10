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

    @Order( 4 )
    //    @Disabled( "think TDD" )
    @Test @DisplayName( "test the whole codestripper" )
    public void testTestStripper() throws IOException {
        System.out.println( "codestripper result in " + outDir.toString() );
        var stripper = new CodeStripper( log, outDir ).extraResources( List.of(
                "../README.md", "../images" ) );
        stripper.strip( pwd );

        assertThat( expandedArchive.toFile() ).exists();
        assertThat( expandedArchive.resolve( "assignment" ).toFile() ).exists();

//        fail( "method TestStripper reached end. You know what to do." );
    }

    @Order( 1 )
    //@Disabled("think TDD")
    @Test @DisplayName( "check that the files land in the proper archive" )
    public void testProperOutput() throws IOException {
        System.out.println( "outDir = " + outDir );
        Path roadKill = Path.of( "src",
                "test",
                "java",
                "codestripper",
                "StripperRoadKill.java" );
        System.out.println( "src = " + roadKill );
//        Path source = pwd.resolve( src );
        assumeThat( roadKill ).exists();
        var stripper = new CodeStripper( log, outDir ).extraResources( List.of(
                "../README.md", "../images" ) );
        stripper.logLevel = LoggerLevel.MUTE;
        Path output = stripper.strip( pwd );
        assertThat( output ).exists();
        System.out.println( "expandedArchive = " + output );
        Path stripped = output.resolve( "assignment" ).resolve(
                projectName ).resolve( roadKill );
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
        var stripper = new CodeStripper( log, outDir ).extraResources( List.of(
                "../README.md", "../images" ) );
        stripper.logLevel = LoggerLevel.FINE;
        stripper.strip( pwd );
        Path expandedArchive = stripper.expandedArchive();
        Path strippedRoadKill = expandedArchive.resolve( "assignment" ).resolve(
                projectName ).resolve( roadKill );
        assumeThat( strippedRoadKill ).exists();
        long size2 = Files.size( strippedRoadKill );
        assertThat( size2 ).isLessThan( size1 );

//        fail( "method TestThatStripperStrips reached end. You know what to do." );
    }

    //@Disabled("think TDD")
    @Test @DisplayName( "test that files land in proper place" )
    public void testFilesLandAtProperPlace() throws IOException {
        assumeThat( Path.of( "pom.xml" ) ).exists();
        var stripper = new CodeStripper( log, outDir );
        stripper.logLevel = LoggerLevel.FINE;
        stripper.strip( pwd );

        assertThat( expandedArchive.resolve( "assignment" )
                .resolve( projectName )
                .resolve( "pom.xml" ) ).exists();

//        fail( "method FilesLandAtProperPlace reached end. You know what to do." );
    }
}
