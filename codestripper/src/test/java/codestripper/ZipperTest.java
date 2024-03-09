/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package codestripper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assumptions.assumeThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class ZipperTest extends StripperTestBase {
//@Disabled("think TDD")

    @Test @DisplayName( "some story line" )
    public void testAddLines() throws IOException, Exception {
        Path pukZip = outDir.resolve( "puk.zip" );
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

    @Test @DisplayName( "adding entry with path to zip" )
    public void testAddFile() throws Exception {
        Path pokZip = outDir.resolve( "pok.zip" );
        Path pom = Path.of( "pom.xml" );
        assumeThat( pom ).exists();
        var pathInZip = Path.of( "far", "far", "away" ).resolve( pom );

        try (
                Zipper zipper = new Zipper( pokZip ); ) {
            zipper.add( pathInZip, pom );
        }
        assertThat( pokZip ).exists();
        long count = countZipEntries( pokZip );
        assertThat( count ).isEqualTo( 1 );
        assertThat( hasAllEntries( pokZip, pathInZip.toString() ) ).isTrue();
//        fail( "method AddFile reached end. You know what to do." );
    }

    static long countZipEntries(Path pokZip) throws IOException {
        ZipFile z = new ZipFile( pokZip.toFile() );
        Enumeration<? extends ZipEntry> entries = z.entries();
        long count = Collections.list( entries ).stream().count();
        return count;
    }

    static boolean hasAllEntries(Path pokZip, String... entryNames) throws IOException {
        ZipFile z = new ZipFile( pokZip.toFile() );

        final Set set = new HashSet( List.of( entryNames ) );
        Enumeration<? extends ZipEntry> entries = z.entries();

        var list = Collections.list( entries );
        for ( ZipEntry zipEntry : list ) {
            set.remove( zipEntry.toString() );
        }

        return set.isEmpty();
    }

}
