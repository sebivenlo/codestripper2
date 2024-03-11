/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package codestripper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assumptions.assumeThat;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class PathFinderTest extends StripperTestBase {

    public PathFinderTest() {
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

    //@Disabled("think TDD")
    @Test @DisplayName( "non existing file" )
    public void testNewFile() throws IOException {
        var resource = locations.out().resolve( "puk.zip" );

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

    //@Disabled("think TDD")
    @Test @DisplayName( "how can you resolv a sibling" )
    public void testFindPWDSibling() {
        Path pwd = Path.of( System.getProperty( "user.dir" ) );
        Path images = pwd.resolve( "../images" ).toAbsolutePath();
        assertThat( images ).exists();
//        fail( "method FindPWDSibling reached end. You know what to do." );
    }

    //@Disabled("think TDD")
    @Test @DisplayName( "some story line" )
    public void testFindRelativePath() {
        Path p = Path.of( "..", "README.md" );
        assumeThat( p ).exists();
        Path relativize = locations.work().relativize( p.toAbsolutePath() );
        System.out.println( "relativize = " + relativize );
        Path inAssesment = locations.out()
                .resolve( "assignment" ).resolve( "assignment" ).resolve( p )
                .normalize()
                .toAbsolutePath();
        System.out.println( "inAssesment = " + inAssesment.toString() );
//        fail( "method FindRelativePath reached end. You know what to do." );
    }

}
