/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codestripper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import org.apache.maven.plugin.logging.Log;

/**
 * Parent of classes in this package that should have the same view on things.
 * Name is a pun on a well known artist type. DragQueen would also be
 * appropriate.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 *
 * @param C subclass
 */
sealed abstract class ChippenDale<C extends ChippenDale<C>>
        permits Archiver, CodeStripper {

    /**
     * known text formats
     */
    protected static final Set<String> textExtensions = Set.of( "java", "sql",
            "txt", "sh", "yaml", "yml", "properties" );

    /**
     * Local path of .git.
     */
    protected final Path dotgit = Path.of( ".git" );
    /**
     * The location of the 'unzipped' archive.
     */
    protected final Path expandedArchive;
    public Path getExpandedArchive() {
        return expandedArchive;
    }
    /**
     *
     */
    final Log log;
    private final Path outDir;
    final Path pwd = Path.of( System.getProperty( "user.dir" ) )
            .toAbsolutePath();
    final Path target = pwd.resolve( "target" ).toAbsolutePath();
    LoggerLevel logLevel = LoggerLevel.FINE;

    final String projectName = pwd.getParent().getFileName().toString();

    public String projectName() {
        return projectName;
    }

    /**
     *
     * @return
     */
    Path expandedArchive() {
        return expandedArchive;
    }

    ChippenDale(Log log, Path outDir) throws IOException {
        this.log = log;
        System.out.println( "outDir = " + outDir );
        this.outDir = outDir.toRealPath().toAbsolutePath();
        this.expandedArchive = outDir().resolve( "assignment" );
    }

    boolean isText(Path file) {
        String fileNameString = file.getFileName().toString();
        String[] split = fileNameString.split( "\\.", 2 );
        if ( split.length < 2 ) {
            // no extension
            return false;
        }
        String extension = split[ 1 ];
        return Archiver.textExtensions.contains( extension );
    }

    /**
     * Determine if a path is acceptable as location for resources. Used to test
     * directories and files.
     *
     * @param p path to test
     * @return true if acceptable false otherwise.
     */
    boolean acceptablePath(Path p) {
        if ( p.toString().startsWith( ".git" ) ) {
            return false;
        }
        if ( p.getFileName().toString().startsWith( ".git" ) ) {
            return false;
        }
        Path absPath = p.toAbsolutePath();
        var itr = absPath.iterator();
        // no .git in dir name
        while ( itr.hasNext() ) {
            if ( itr.next().getFileName().toString().equals( ".git" ) ) {
                return false;
            }
        }
        if ( absPath.startsWith( outDir ) ) {
            return false;
        }
        if ( absPath.startsWith( target ) ) {
            return false;
        }
        if ( Files.isDirectory( absPath ) ) {
            return false;
        }
        if ( absPath.getFileName().toString().endsWith( "~" ) ) {
            return false;
        }
        return true;
    }

    Path outDir() {
        return outDir;
    }

}
