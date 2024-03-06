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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

/**
 * Tets the archiver.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
@TestMethodOrder( OrderAnnotation.class )
public class ArchiverTest extends StripperTestBase {


    Path pwd = Path.of( System.getProperty( "user.dir" ) );
    Log log = new SystemStreamLog();

//    @Disabled( "think TDD" )
    @Order( 1 )
    @Test @DisplayName( "test add assignment file" )
    public void testAssignmentFile(TestInfo info) throws Exception {
        System.out.println(
                "test info ===== " + info.getDisplayName() + " ========" );
        System.out.println( "outDir = " + outDir );
        try (
                Archiver archiver = new Archiver( outDir.toString(), log ); ) {
            Path source = Path.of( "..", "README.md" );
            archiver.addAssignmentFile( source, source );
            assertThat(
                    archiver.expandedArchive()
                            //                            .resolve( "assignment" )
                            .resolve( "README.md" )
                            .toAbsolutePath() )
                    .exists();
        }

//        fail( "method AddFile reached end. You know what to do." );
    }

//    @Disabled( "think TDD" )
    @Order( 2 )
    @Test @DisplayName( "test add to zip dir" )
    public void testAddToZipDir() throws IOException, Exception {
        Path file = Path.of( "src", "test",
                "java", "codestripper",
                "ArchiverTest.java"
        );
        try (
                Archiver archiver = new Archiver( outDir.toString(), log ); ) {
            archiver.addFile( file );
            assertThat(
                    archiver.expandedArchive()
                            .resolve( "assignment" )
                            .resolve( file )
            ).exists();
        }

//        fail( "method AddFile reached end. You know what to do." );
    }

    @Order( 0 )
    //@Disabled("think TDD")
    @Test @DisplayName( "test path in archive" )
    public void testPathInArchive() throws Exception {
        Path readme = Path.of( "..", "README.md" );
        try ( Archiver archiver = new Archiver( outDir.toString(), log ); ) {
            Path pathInArchive = archiver.pathInArchive( "puk", readme );
            assertThat( pathInArchive )
                    .isEqualTo( Path.of( "puk", "README.md" ) );
        }
//        fail( "method PathInArchive reached end. You know what to do." );
    }

    @Order( 3 )
    //    @Disabled( "think TDD" )
    @Test @DisplayName( "add file using archiver.addFile" )
    public void testAddFile(TestInfo info) throws Exception {
//        cleanup();

        var fName = Path.of( "..", "README.md" );
        assumeThat( fName ).exists();
        System.out.println(
                "test info ======= " + info.getDisplayName() + " ========" );
        try (
                Archiver archiver = new Archiver( outDir.toString(), log ); ) {
            archiver.addFile( fName );
            Path expected = archiver.expandedArchive()
                    //                    .resolve( "assignment" )
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
        List<String> extras = List.of( "../README.md", "../images" );
        for ( String extra : extras ) {
            assumeThat( pwd.resolve( extra ) ).exists();
        }
        try (
                Archiver archiver = new Archiver( outDir.toString(), log ); ) {
            archiver.addExtras( extras );
            Path expectedPath = archiver.expandedArchive()
                    .resolve( "assignment" )
                    .resolve( "../README.md" ).normalize();
            System.out.println( "expected Path " + expectedPath.toString() );
            assertThat( expectedPath.toAbsolutePath() ).exists();
        }

//        fail( "method AddExtras reached end. You know what to do." );
    }

}
