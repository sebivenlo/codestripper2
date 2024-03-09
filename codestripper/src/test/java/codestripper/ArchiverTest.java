/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package codestripper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.assertj.core.api.Assumptions.assumeThatCode;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests the Archiver.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
@TestMethodOrder( OrderAnnotation.class )
public class ArchiverTest extends StripperTestBase {

    Path pwd = Path.of( System.getProperty( "user.dir" ) );
    Logger log = LoggerFactory.getLogger( ArchiverTest.class );

    @Order( 0 )
    //    @Disabled( "think TDD" )
    @Test @DisplayName( "test dot dot file in archive" )
    public void testPathInArchive(TestInfo info) throws Exception {
        Path readme = Path.of( "..", "README.md" );
        try ( Archiver archiver = new Archiver( outDir, log ); ) {
            Path pathInArchive = archiver
                    .relPathInArchive( "assignment", readme );
            assertThat( pathInArchive.toString().startsWith( "/" ) ).isFalse();
            System.out.println( "dot dot pathInArchive = " + pathInArchive );
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
        try ( Archiver archiver = new Archiver( outDir, log ); ) {
            Path actual = archiver.relPathInArchive( "solution", javaFile );

            assertThat( actual.toString().startsWith( "/" ) ).isFalse();
            System.out.println( "java pathInArchive = " + actual );
            var expected = Path.of( "solution" )
                    .resolve( archiver.projectName() )
                    .resolve( javaFile );
            System.out.println( "expected = " + expected );
            System.out.println( "actual = " + actual );
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
        System.out.println(
                "test info ===== " + info.getDisplayName() + " ========" );
        System.out.println( "outDir = " + outDir );
        try (
                Archiver archiver = new Archiver( outDir, log ); ) {
            Path source = Path.of( "..", "README.md" );
            Path relPathInArchive = archiver.relPathInArchive( "assignment",
                    source );
            archiver.addAssignmentFile( relPathInArchive, source );
            System.out.println( "archiver.expandedArchive() = " + archiver
                    .expandedArchive() );
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
                Archiver archiver = new Archiver( outDir, log ); ) {
            archiver.addFile( file );
            var expected = expandedArchive
                    .resolve( "assignment" )
                    .resolve( archiver.projectName() )
                    .resolve( file );
            System.out.println( "expected = " + expected );
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
        System.out.println(
                "test info ======= " + info.getDisplayName() + " ========" );
        try (
                Archiver archiver = new Archiver( outDir, log ); ) {
            archiver.addFile( fName );
            Path expected = expandedArchive
                    .resolve( "assignment" )
                    .resolve( "README.md" )
                    .toAbsolutePath();
            System.out.println( "expectedPath = '" + expected + "'" );
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
                    Archiver archiver = new Archiver( outDir, log ); ) {
                archiver.addExtras( extras );
                for ( String extra : extras ) {

                    Path expectedPath = expandedArchive
                            .resolve( "assignment" )
                            .resolve( archiver.projectName() )
                            .resolve( extra ).normalize();
                    System.out.println( "expected Path " + expectedPath
                            .toString() );
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

    public ArchiverTest() {
        super();
        ThrowingCallable code = () -> {
            try (
                    Archiver archiver = new Archiver( outDir, log ); ) {
            } catch ( Throwable ex ) {
                ex.printStackTrace();
                throw ex;
            }
        };

        assumeThatCode( code ).doesNotThrowAnyException();
    }

}
