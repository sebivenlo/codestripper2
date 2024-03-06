/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package codestripper;

import static codestripper.ArchiverTest.cleanupStatic;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assumptions.assumeThat;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class PathFinder {

    public PathFinder() {
    }

    //@Disabled("think TDD")
    @Test @DisplayName( "play with paths" )
    public void testFindResources() throws IOException {
        var resource = Path.of( "../", "images", "lineprocessor-classdiagram.svg" );
        assumeThat( resource ).exists();
        var parent = Path.of( System.getProperty( "user.dir" ) ).getParent();
        var resourceReal = resource.toRealPath();
        var resourceInzip = parent.relativize( resourceReal );

        System.out.println( "resourceInzip = " + resourceInzip );
        assertThat( resourceInzip.toString() ).isEqualTo(
                "images/lineprocessor-classdiagram.svg" );

//        fail( "method FindResources reached end. You know what to do." );
    }

    //@Disabled("think TDD")
    @Test @DisplayName( "some story line" )
    public void testOpenFile() throws IOException {
        var resource = Path.of( "../", "images", "lineprocessor-classdiagram.svg" );

        ThrowingCallable code = () -> {
            Stream<String> lines = Files.lines( resource );
            assertThat( lines ).isNotEmpty();

        };
        assertThatCode( code ).doesNotThrowAnyException();
//        fail( "method OpenFile reached end. You know what to do." );
    }

    Path outDir = Path.of( System.getProperty( "user.dir" ) ).resolve( Path.of(
            "puk" ) );

    //@Disabled("think TDD")
    @Test @DisplayName( "non existing file" )
    public void testNewFile() throws IOException {
        var resource = Path.of( "puk", "puk.zip" );

        Files.createDirectories( resource.getParent() );
        ThrowingCallable code = () -> {
            File zipFile = resource.toFile();
            try ( var fos = new FileOutputStream( zipFile ); ) {
                //assertThat( lines ).isNotEmpty();
            }
            assertThat( resource ).exists();

        };
        assertThatCode( code ).doesNotThrowAnyException();
//        fail( "method OpenFile reached end. You know what to do." );
    }

    @AfterEach
    public void cleanup() throws IOException {

        cleanupStatic( outDir );
    }
}
