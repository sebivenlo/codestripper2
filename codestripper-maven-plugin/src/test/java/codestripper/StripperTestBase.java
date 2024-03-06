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

    Path outDir;
    Path pwd = Path.of( System.getProperty( "user.dir" ) );
    Log log = new SystemStreamLog();

    public StripperTestBase() {
        try {
            outDir = Files.createTempDirectory( "codestripper-" + getClass()
                    .getSimpleName() + "-tests-" );
        } catch ( IOException ex ) {
            Logger.getLogger( ArchiverTest.class.getName() )
                    .log( Level.SEVERE, null, ex );
        }
    }

    @AfterEach
    public void cleanup() throws IOException {

        cleanupStatic( outDir );
    }

    static void cleanupStatic(Path outDir) throws IOException {
        if ( !outDir.toFile().exists() ) {
            return;
        }
        List<Path> collect = Files.walk( outDir, Integer.MAX_VALUE )
                .sorted( ( f1, f2 ) -> f2.compareTo( f1 ) )
                .collect( toList() );
        collect.stream()
                .peek( System.out::println )
                .forEach( f -> f.toFile().delete() );

        assertThat( outDir.toFile() ).doesNotExist();
    }

}
