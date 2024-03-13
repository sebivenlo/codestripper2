/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package codestripper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

/**
 * Tests the Archiver.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
@TestMethodOrder( OrderAnnotation.class )
public class ArchiverTest extends StripperTestBase {

    Archiver newArchiver() throws IOException {
        return new Archiver( log, locations );
    }

//    @Disabled( "think TDD" )
    @Order( 2 )
    @Test
    //    @DisplayName( "test add assignment file" )
    public void testAssignmentFile(TestInfo info) throws Exception {
        try (
                Archiver archiver = newArchiver(); ) {
            Path source = locations.projectFile( Path.of( "..", "README.md" ) );
            assertThat( source ).exists();
            archiver.addFile( source );
            assertThat(
                    locations.expandedArchive().resolve( "assignment" )
                            .resolve( "README.md" ).toAbsolutePath()
            )
                    .exists();
        }
//        fail( "method AddFile reached end. You know what to do." );
    }

//    @Disabled( "think TDD" )
    @Order( 3 )
    @Test @DisplayName( "test add to zip dir" )
    public void testAddToZipDir() throws IOException, Exception {
        Path file = Path.of( "src", "test",
                "java", "greeter",
                "GreeterTest.java"
        );
        assumeThat( locations.work().resolve( file ) ).exists();
        try (
                Archiver archiver = newArchiver(); ) {
            archiver.addFile( file );
            Path expandedArchive = locations.expandedArchive();
            var expected = expandedArchive
                    .resolve( "assignment" )
                    .resolve( locations.projectName() )
                    .resolve( file );
            assertThat( expected ).exists();
        }

//        fail( "method AddFile reached end. You know what to do." );
    }

    @Order( 4 )
    //    @Disabled( "think TDD" )
    @Test @DisplayName( "add file using archiver.addFile" )
    public void testAddFile(TestInfo info) throws Exception {

        var fName = Path.of( "..", "README.md" );
        assumeThat( fName ).exists();
        try (
                Archiver archiver = newArchiver(); ) {
            archiver.addFile( fName );
            Path expandedArchive = locations.expandedArchive();
            Path expected = expandedArchive
                    .resolve( "assignment" )
                    .resolve( "README.md" )
                    .toAbsolutePath();
            assertThat( expected ).exists();
        }

//        fail( "method AddExtras reached end. You know what to do." );
    }

    @Order( 5 )
    //    @Disabled( "think TDD" )
    @Test @DisplayName( "add extras" )
    public void testAddExtras() throws Exception {
        List<String> extras = List.of( "../README.md", "../images" );
        for ( String extra : extras ) {
            assumeThat( locations.work().resolve( extra ) ).exists();
        }
        SoftAssertions.assertSoftly( softly -> {
            try (
                    Archiver archiver = newArchiver(); ) {
                archiver.addExtras( extras );
                for ( String extra : extras ) {
                    Path expandedArchive = locations.expandedArchive();
                    Path expectedPath = expandedArchive
                            .resolve( "assignment" )
                            .resolve( archiver.projectName() )
                            .resolve( extra ).normalize();
                    softly.assertThat( expectedPath.toAbsolutePath() ).exists();
                }
            } catch ( Exception ex ) {
                java.util.logging.Logger
                        .getLogger( ArchiverTest.class.getName() )
                        .log( Level.SEVERE, null, ex );
            }
        } );

//        fail( "method AddExtras reached end. You know what to do." );
    }

    @BeforeEach
    public void setup(TestInfo info) {
        System.out.println(
                "start  test ======= " + info.getDisplayName() + " ========" );
    }

    @AfterEach
    public void pullDown(TestInfo info) {
        System.out.println(
                "finish test ======= " + info.getDisplayName() + " ========" );
    }

}
