/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codestripper;

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

    protected static final Set<String> textExtensions = Set.of( "java", "sql",
            "txt", "sh", "yaml", "yml" );

    protected final Path dotgit = Path.of( ".git" );
    protected final Path expandedArchive;
    protected final Log log;
    protected final Path outDir;
    protected final Path pwd = Path.of( System.getProperty( "user.dir" ) )
            .toAbsolutePath();
    protected final Path target = pwd.resolve( "target" ).toAbsolutePath();
    protected LoggerLevel logLevel = LoggerLevel.FINE;

    /**
     *
     * @return
     */
    protected Path expandedArchive() {
        return expandedArchive;
    }

    protected ChippenDale(Log log, Path outDir) {
        this.log = log;
        this.outDir = outDir.toAbsolutePath();
        this.expandedArchive = outDir.resolve( "assignment" );
    }

    protected boolean isText(Path file) {
        String fileNameString = file.getFileName().toString();
        String[] split = fileNameString.split( "\\.", 2 );
        if ( split.length < 2 ) {
            // no extension
            return false;
        }
        String extension = split[ 1 ];
        return Archiver.textExtensions.contains( extension );
    }

    public boolean acceptablePath(Path p) {
        if ( p.toString().startsWith( ".git" ) ) {
            return false;
        }
        Path absPath = p.toAbsolutePath();
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

    public Path outDir() {
        return outDir;
    }

}
