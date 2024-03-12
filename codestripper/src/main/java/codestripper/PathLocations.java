package codestripper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.Path.of;
import static java.lang.System.getProperty;
import java.util.Objects;
import org.apache.maven.plugin.logging.Log;

/**
 * Set of locations.
 *
 * Computes the required Paths. Based on two paths, work and out, compute the
 * paths to be used.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public record PathLocations(Log logger, Path work, Path out,
        String assignmentName,
        String projectName) {

    /**
     * Verifies inputs.
     *
     * @param logger not null
     * @param work path that should exist
     * @param out exists and writable and should not be a parent to work
     * @param assignmentName not null not blank
     * @param projectName not null not blank
     */
    public PathLocations     {
        Objects.requireNonNull( logger );
        Objects.requireNonNull( assignmentName );
        Objects.requireNonNull( projectName );
        assert !assignmentName.isBlank();
        assert !projectName.isBlank();
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
        if ( out.equals( work ) ) {
            throw new IllegalArgumentException(
                    "out and work Paths should be different, now both are " + work
                            .toString() );
        }
        if ( work.toAbsolutePath().startsWith( out.toAbsolutePath() ) ) {
            throw new IllegalArgumentException(
                    "work dir" + work.toString()
                    + " should not be a child of the out " + out.toString()
                    + " dir to prevent accidental overwriting " );
        }
    }

    /**
     * Create locations with default assignment name.
     *
     * @param logger to use
     * @param work readable dir
     * @param out writable dir
     */
    public PathLocations(Log logger, Path work, Path out) {
        this( logger, work, out, "assignment",
                work.toAbsolutePath().getFileName().toString() );
    }

    /**
     * Create locations with default assignment name and default work dir pwd.
     *
     * @param logger to use
     * @param out wriable dir.
     */
    public PathLocations(Log logger, Path out) {
        this( logger, Path.of( getProperty( "user.dir" ) ), out, "assignment",
                of( getProperty( "user.dir" ) ).getFileName()
                        .toString() );
    }

    public Path pwd() {
        return of( getProperty( "user.dir" ) );
    }

    public Path workRelative(Path other) {
        return relTo( work, other );
    }

    public Path workRelative(String other) {
        return relTo( work, Path.of( other ) );
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
