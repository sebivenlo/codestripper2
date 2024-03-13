package codestripper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static java.util.stream.Collectors.toList;
import mytinylogger.DefaultLogger;
import loggerwrapper.Logger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import org.junit.jupiter.api.AfterEach;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class StripperTestBase {

    Path tempDir;
//    Path pwd = Path.of( System.getProperty( "user.dir" ) );
    final Logger log = new DefaultLogger();
    //    String projectName = pwd.getFileName().toString();
    //    Path expandedArchive;

    PathLocations locations;

    public StripperTestBase() {
        try {
            Path sampleproject = Path.of( "..",
                    "sampleproject", "example" ).toAbsolutePath();
            assumeThat( sampleproject ).exists();
            tempDir = Files.createTempDirectory( "codestripper-" + this
                    .getClass().getSimpleName() + "-" );
            locations = new PathLocations( log, sampleproject, tempDir );
        } catch ( IOException ex ) {
            log.error( () -> ex.getMessage() );
            throw new IllegalArgumentException( ex );
        }
    }

    @AfterEach
    public void cleanup() throws IOException {
        cleanupStatic( tempDir );
    }

    static void cleanupStatic(Path dirToCleanAndRemove) throws IOException {
        if ( !dirToCleanAndRemove.toFile().exists() ) {
            return;
        }
        Path tempDir = Path.of( System.getProperty( "java.io.tmpdir" ) );
        // refuse to clean anything but temp.
        if ( !dirToCleanAndRemove.startsWith( tempDir ) ) {
            return;
        }
        List<Path> collect = Files
                .walk( dirToCleanAndRemove, Integer.MAX_VALUE )
                .sorted( ( f1, f2 ) -> f2.compareTo( f1 ) )
                .collect( toList() );
        collect.stream()
                .forEach( f -> f.toFile().delete() );

        assertThat( dirToCleanAndRemove.toFile() ).doesNotExist();
    }

}
