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
    PathLocations locations;

    public CodeStripperTest() {
        Path sampleProject = Path.of( "..", "sampleproject", "example" )
                .toAbsolutePath().normalize();
        System.out.println( "sampleProject = " + sampleProject );
        assumeThat( sampleProject ).exists();
        locations = new PathLocations( log, sampleProject, tempDir );
        stripper = new CodeStripper.Builder()
                .logger( log )
                .pathLocations( locations )
                .extraResources( List.of(
                        "../README.md", "../images" ) )
                .build();
    }

    Path roadKill = Path.of( "src",
            "main",
            "java",
            "greeter",
            "BrokenOnPurpose.java" );

    @Order( 1 )
    //@Disabled("think TDD")
    @Test @DisplayName( "check that the files land in the proper archive" )
    public void testProperOutput() throws IOException {
        System.out.println( "outDir = " + locations.out() );
        System.out.println( "road kill src = " + roadKill );
//        Path source = pwd.resolve( src );
        assumeThat( locations.work().resolve( roadKill ) ).exists();
        stripper.logLevel = LoggerLevel.MUTE;
        Path output = stripper.strip();
        System.out.println( "stripper output = " + output );
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
//    @Test //@DisplayName( "stripped files are smaller" )
    public void testTestThatStripperStrips() throws IOException {
        Path workRelative = locations.workRelative( roadKill );
        System.out.println( "workRelative = " + workRelative );
        assumeThat( workRelative ).exists();
        long size1 = Files.size( locations.workRelative( roadKill ) );
        stripper.logLevel = LoggerLevel.FINE;
        stripper.strip();
        Path expandedArchive = locations.out().resolve( "expandedArchive" );
        Path inArchive = Path.of( locations.assignmentName(), locations
                .projectName() );
        System.out.println( "expandedArchive  = " + expandedArchive );
        Path strippedRoadKill = expandedArchive.resolve( inArchive ).resolve(
                roadKill );
        System.out.println( "strippedRoadKill = " + strippedRoadKill );
        assumeThat( strippedRoadKill ).exists();
        long size2 = Files.size( strippedRoadKill );
        assertThat( size2 ).isLessThan( size1 );

//        fail( "method TestThatStripperStrips reached end. You know what to do." );
    }

    //@Disabled("think TDD")
    @Test @DisplayName( "test that files land in proper place" )
    public void testFilesLandAtProperPlace() throws IOException {
        assumeThat( locations.work().resolve( "pom.xml" ) ).exists();
        var stripper = new CodeStripper.Builder()
                .logger( log )
                .pathLocations( locations )
                .build();
        stripper.logLevel = LoggerLevel.FINE;
        Path output = stripper.strip();
        System.out.println( "outDir = " + output );
        assertThat( locations.expandedArchive().resolve( locations
                .assignmentName() )
                .resolve( locations.projectName() ).resolve( "pom.xml" ) )
                .exists();

//        fail( "method FilesLandAtProperPlace reached end. You know what to do." );
    }

    //@Disabled("think TDD")
    @Test @DisplayName( "are assignment files such as pom.xml added properly" )
    public void testAddAssignmentFiles() throws IOException {
        assumeThat( Path.of( "pom.xml" ) ).exists();
        stripper.logLevel = LoggerLevel.FINE;
        Path output = stripper.strip();
        System.out.println( "output = " + output );
        assumeThat( output ).exists();

        assertThat( output.resolve( "pom.xml" ) ).exists();

//        fail( "method AddAssignmentFiles reached end. You know what to do." );
    }

}
