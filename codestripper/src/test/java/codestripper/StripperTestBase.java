package codestripper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toList;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class StripperTestBase {

    Path tempDir;
//    Path pwd = Path.of( System.getProperty( "user.dir" ) );
    final Log log = new SystemStreamLog();
    //    String projectName = pwd.getFileName().toString();
    //    Path expandedArchive;

    PathLocations locations;

    public StripperTestBase() {
        try {
            Path sampleproject = Path.of( "..",
                    "sampleproject" ).toAbsolutePath();
            tempDir = Files.createTempDirectory( "codestripper-" + this
                    .getClass().getSimpleName() + "-" );
            locations = new PathLocations( log, sampleproject, tempDir );
        } catch ( IOException ex ) {
            log.error( ex.getMessage() );
            throw new IllegalArgumentException( ex );
        }
    }

    @AfterEach
    public void cleanup() throws IOException {
        cleanupStatic( tempDir );
    }

    static void cleanupStatic(Path outDir) throws IOException {
        if ( !outDir.toFile().exists() ) {
            return;
        }
        List<Path> collect = Files.walk( outDir, Integer.MAX_VALUE )
                .sorted( ( f1, f2 ) -> f2.compareTo( f1 ) )
                .collect( toList() );
        collect.stream()
                //                .peek( System.out::println )
                .forEach( f -> f.toFile().delete() );

        assertThat( outDir.toFile() ).doesNotExist();
    }

}
