/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codestripper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.Path.of;
import static java.lang.System.getProperty;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.maven.plugin.logging.Log;

/**
 * Compute the required Paths.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
record PathLocations(Log logger, Path work, Path out,
        String assignmentName,
        String projectName) {

    public PathLocations     {
        if ( !work.toFile().exists() ) {
            throw new IllegalArgumentException( "work dir Path " + work
                    .toString()
                    + " should already exists!" );
        }
        if ( !out.toFile().exists() ) {
            throw new IllegalArgumentException( "out dir Path " + out.toString()
                    + " should already exists!" );
        }
        if ( !Files.isWritable( out ) ) {
            throw new IllegalArgumentException( "Path " + out.toString()
                    + " is not writable!" );
        }
    }

    public PathLocations(Log logger, Path work, Path out) {
        this( logger, work, out, "assignment",
                of( getProperty( "user.dir" ) ).getFileName()
                        .toString() );
    }

    public PathLocations(Log logger, Path out) {
        this( logger, Path.of( getProperty( "user.dir" ) ), out, "assignment",
                of( getProperty( "user.dir" ) ).getFileName()
                        .toString() );
    }

    public void cleanup() {
        if ( out.startsWith( "/tmp/codestripper-" ) ) {

        }
    }

    public Path pwd() {
        return of( getProperty( "user.dir" ) );
    }

    public Path workRelative(Path other) {
        return relTo( work, other );
    }

    Path relTo(final Path x, Path other) {
        if ( !other.isAbsolute() ) {
            other = other.toAbsolutePath();
        }
        return x.toAbsolutePath().relativize( other );
    }

    public Path outRelative(Path other) {
        return relTo( work, other );
    }

    /**
     * Ensure that filePath.getParent() in out exists.
     *
     * @param filePath the path of a file
     * @return the path of the file with the parent existing
     * @throws IOException should not occur.
     */
    public Path inOutFile(String filePath) {
        return out.resolve( filePath );
    }

    /**
     * Ensure that filePath.getParent() in work exists.
     *
     * @param filePath the path of a file
     * @return the path of the file with the parent existing
     * @throws IOException should not occur.
     */
    public Path inWorkFile(String filePath) {
        return work.resolve( filePath );
    }

    public static Path createTempOut(String prefix) throws IOException {
        return Files.createTempDirectory( prefix );
    }

}
