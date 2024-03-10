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
public class ArchiverTest extends StripperTestBase {//    String projectName = pwd.getFileName().toString();
//    Path expandedArchive;
    //    String projectName = pwd.getFileName().toString();
//    Path expandedArchive;

    Path tempDir;
    public ArchiverTest() {
        Path tmpDir = Path.of( "/", "tmp", "codestripper-" + getClass()
                .getSimpleName() + "-" + LocalDateTime.now().toString()
                        .replaceAll( "[:T]", "-" ) );
        try {
            tempDir = Files.createDirectories( tmpDir );
//            expandedArchive = outDir.resolve( "expandedArchive" );
        } catch ( IOException ex ) {

            log.error( ex.getMessage() );
        }
    }

//    Path pwd = Path.of( System.getProperty( "user.dir" ) );
//    Log log = new SystemStreamLog();

    Archiver newArchiver() throws IOException {
        return new Archiver( log, tempDir, "assignment", pwd.getFileName()
                .toString() );
    }

    @Order( 0 )
    //    @Disabled( "think TDD" )
    @Test @DisplayName( "test dot dot file in archive" )
    public void testPathInArchive(TestInfo info) throws Exception {
        Path readme = Path.of( "..", "README.md" );
        try ( Archiver archiver = newArchiver(); ) {
            Path pathInArchive = archiver
                    .relPathInArchive( "assignment", readme );
            assertThat( pathInArchive.toString().startsWith( "/" ) ).isFalse();
            assertThat( pathInArchive.toString() )
                    .isEqualTo(
                            Path.of( "assignment" )
                                    .resolve( "README.md" )
                                    .toString() );
        }

//        fail( "method PathInArchive reached end. You know what to do." );
    }

    @Order( 1 )
    //@Disabled("think TDD")
    @Test @DisplayName( "test java path in archive" )
    public void testPathInArchive2(TestInfo info) throws Exception {
        Path javaFile = Path
                .of( "src/main/java/codestripper/CodeStripper.java" );
        assertThat( javaFile ).exists();
        try ( Archiver archiver = newArchiver(); ) {
            Path actual = archiver.relPathInArchive( "solution", javaFile );

            assertThat( actual.toString().startsWith( "/" ) ).isFalse();
            var expected = Path.of( "solution" )
                    .resolve( archiver.projectName() )
                    .resolve( javaFile );
            assertThat( actual )
                    .isEqualTo( expected );
        }
//        fail( "method PathInArchive reached end. You know what to do." );
    }

//    @Disabled( "think TDD" )
    @Order( 2 )
    @Test
    //    @DisplayName( "test add assignment file" )
    public void testAssignmentFile(TestInfo info) throws Exception {
        try (
                Archiver archiver = newArchiver(); ) {
            Path source = Path.of( "..", "README.md" );
            Path relPathInArchive = archiver.relPathInArchive( "assignment",
                    source );
            archiver.addAssignmentFile( relPathInArchive, source );
            assertThat(
                    archiver.expandedArchive().resolve( "assignment" )
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
                "java", "codestripper",
                "ArchiverTest.java"
        );
        assumeThat( file ).exists();
        try (
                Archiver archiver = newArchiver(); ) {
            archiver.addFile( file );
            Path expandedArchive = archiver.expandedArchive();
            var expected = expandedArchive
                    .resolve( "assignment" )
                    .resolve( archiver.projectName() )
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
            Path expandedArchive = archiver.expandedArchive();
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
        List<String> extras = List.of( "../LICENSE", "../images" );
        for ( String extra : extras ) {
            assumeThat( pwd.resolve( extra ) ).exists();
        }
        SoftAssertions.assertSoftly( softly -> {
            try (
                    Archiver archiver = newArchiver(); ) {
                archiver.addExtras( extras );
                for ( String extra : extras ) {
                    Path expandedArchive = archiver.expandedArchive();
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

    @Order( 4 )
    //    @Disabled( "think TDD" )
    @Test @DisplayName( "test the whole codestripper" )
    public void testTestArchivePaths() throws IOException {
        System.out.println( "codestripper result in " + outDir.toString() );
        final var archiver = new Archiver( log, outDir, "zip1", "projectName" );

        archiver.addAssignmentFiles( pwd );

        assertThat( archiver.expandedArchive() )
                .exists();
        assertThat( archiver.expandedArchive()
                .resolve( "zip1" ) )
                .exists();
        assertThat( archiver.expandedArchive()
                .resolve( "zip1" )
                .resolve( "projectName" ) )
                .exists();

//        fail( "method TestStripper reached end. You know what to do." );
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
