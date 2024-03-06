/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package codestripper;

import static codestripper.ArchiverTest.cleanupStatic;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class ZipperTest {
//@Disabled("think TDD")

    @Test @DisplayName( "some story line" )
    public void testAddLines() throws IOException, Exception {
        Path pukPath = Path.of( "puk", "puk.zip" );
        Path pwd = Path.of( System.getProperty( "user.dir" ) );
        var pukZip = pwd.resolve( pukPath );
        try (
                Zipper zipper = new Zipper( pukZip ); ) {
            Path pom = Path.of( "pom.xml" );
            assertThat( pom ).isNotNull();
            zipper.add( pom, List.of( "<project>"
                    + "Hello", "world", "</project>" ) );
            assertThat( pukZip ).exists();
        }
//        fail( "method AddFile reached end. You know what to do." );
    }

    Path outDir = Path.of( "puk" );

    @Test @DisplayName( "some story line" )
    public void testAddFile() throws Exception {
        Path pokZip = outDir.resolve( Path.of( "pok.zip" ) );

        try (
                Zipper zipper = new Zipper( pokZip ); ) {
            Path pom = Path.of( "pom.xml" );
            assertThat( pom ).isNotNull();
            zipper.add( Path.of( "far", "far", "away" ).resolve( pom ), pom );
        }
        assertThat( pokZip ).exists();
        ZipFile z = new ZipFile( pokZip.toFile() );
        Enumeration<? extends ZipEntry> entries = z.entries();

        long count = Collections.list( entries ).stream().count();
        assertThat( count ).isEqualTo( 1 );

//        fail( "method AddFile reached end. You know what to do." );
    }

    @AfterEach
    public void cleanup() throws IOException {

        cleanupStatic( outDir );
    }
}
