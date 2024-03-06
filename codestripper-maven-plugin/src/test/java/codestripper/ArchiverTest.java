/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package codestripper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tets the archiver.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class ArchiverTest {

    Path outDir = Path.of( "puk" );

    //@Disabled("think TDD")
    @Test @DisplayName( "test add file" )
    public void testAddConcreteFile() throws Exception {
        Log log = new SystemStreamLog();
        try (
                Archiver archiver = new Archiver( outDir.toString(), log ); ) {
            Path p = Path.of( "src", "test",
                    "java",
                    "codestripper",
                    "PathFinder.java" );
            archiver.addConcreteFile( p );
            assertThat( Path.of( "puk", "assignment", p.toString() ) ).exists();
        }

//        fail( "method AddFile reached end. You know what to do." );
    }

    @Test @DisplayName( "test add to zip dir" )
    public void testAddToZipDir() throws IOException, Exception {
        Log log = new SystemStreamLog();
        Path file = Path.of( "src", "test",
                "java", "codestripper",
                "ArchiverTest.java"
        );
        try (
                Archiver archiver = new Archiver( outDir.toString(), log ); ) {
            archiver.addFile( file );
        }
        assertThat( Path.of( "puk", "assignment", file.toString() ) )
                .exists();

//        fail( "method AddFile reached end. You know what to do." );
    }

    @AfterEach
    public void cleanup() throws IOException {

        cleanupStatic( outDir );
    }

    static void cleanupStatic(Path outDir) throws IOException {
        List<Path> collect = Files.walk( outDir, Integer.MAX_VALUE )
                .sorted( ( f1, f2 ) -> f2.compareTo( f1 ) )
                .collect( toList() );
//        List<Path> reversed = collect.reversed();
        collect.stream()
                .peek( System.out::println )
                .forEach( f -> f.toFile().delete() );

        assertThat( outDir ).doesNotExist();
    }
}
