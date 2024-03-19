package codestripper;

import java.nio.file.Path;
import java.util.Set;

/**
 * Parent of classes in this package that should have the same view on things.
 * Name is a pun on a well known artist type. DragQueen would also be
 * appropriate.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 *
 */
public class ChippenDale {

    private ChippenDale(){}
    /**
     * known text formats.
     */
    public static final Set<String> textExtensions = Set.of( "java", "sql",
            "txt", "sh", "yaml", "yml", "properties" );

    /**
     * Local path of .git.
     */
    public static final Path dotgit = Path.of( ".git" );

    /**
     * The location of the 'unzipped' archive.
     * @param file to test
     * @return true if file classifies as text.
     */
    public static boolean isText(Path file) {
        String fileNameString = file.getFileName().toString();
        String[] split = fileNameString.split( "\\.", 2 );
        if ( split.length < 2 ) {
            // no extension
            return false;
        }
        String extension = split[ 1 ];
        return textExtensions.contains( extension );
    }

    /**
     * Default stripper out dir.
     */
    public static final Path DEFAULT_STRIPPER_OUTDIR = Path.of( "target",
            "stripper-out" );

}
