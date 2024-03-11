/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codestripper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.Path.of;
import static java.lang.System.getProperty;

/**
 * Compute the required Paths.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public record PathLocations(String work, String out, String assignmentName,
        String projectName) {

    public PathLocations(String work, String out) {
        this( work, out, "assignment",
                of( getProperty( "user.dir" ) ).getFileName()
                        .toString() );
    }

    public PathLocations(String out) {
        this( getProperty( "user.dir" ), out, "assignment",
                of( getProperty( "user.dir" ) ).getFileName()
                        .toString() );
    }

    public Path pwd() {
        return of( getProperty( "user.dir" ) );
    }

    public Path workRelative(Path other) throws IOException {
        return relTo( work, other );
    }

    Path relTo(String x, Path other) throws IOException {
        if ( !other.isAbsolute() ) {
            other = other.toAbsolutePath();
        }

        return toExistingPath( x ).toAbsolutePath().relativize( other );
    }

    public Path outRelative(Path other) throws IOException {
        return relTo( work, other );
    }

    /**
     * Ensure that filePath.getParent() in out exists.
     *
     * @param filePath the path of a file
     * @return the path of the file with the parent existing
     * @throws IOException should not occur.
     */
    public Path inOutFile(String filePath) throws IOException {
        return toExistingPath( out ).resolve( filePath );
    }

    /**
     * Ensure that filePath.getParent() in work exists.
     *
     * @param filePath the path of a file
     * @return the path of the file with the parent existing
     * @throws IOException should not occur.
     */
    public Path inWorkFile(String filePath) throws IOException {
        return toExistingPath( work ).resolve( filePath );
    }

    public Path realWork() throws IOException {
        Path result = toExistingPath( work );
        return result;
    }

    public Path toExistingPath(String dir) throws IOException {
        var result = of( dir ).toAbsolutePath();
        if ( !result.toFile().exists() ) {
            Files.createDirectories( result );
        }
        return result;
    }

    public Path realOut() throws IOException {
        Path result = toExistingPath( out );
        return result;
    }
}
