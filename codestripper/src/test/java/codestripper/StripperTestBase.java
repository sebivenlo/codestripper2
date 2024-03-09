package codestripper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import org.junit.jupiter.api.AfterEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class StripperTestBase {

    Path outDir;
    Path pwd = Path.of( System.getProperty( "user.dir" ) );
    Logger log = LoggerFactory.getLogger( StripperTestBase.class );
    String projectName = pwd.getFileName().toString();
    public StripperTestBase() {
        Path tmpDir = Path.of( "/", "tmp", "codestripper-" + getClass()
                .getSimpleName() + "-" + LocalDateTime.now().toString()
                        .replaceAll( "[:T]", "-" ) );
        try {
            outDir = Files.createDirectory( tmpDir );
        } catch ( IOException ex ) {

            log.error( ex.getMessage() );
        }
        assumeThat( outDir ).exists();
    }

    @AfterEach
    public void cleanup() throws IOException {
//        cleanupStatic( outDir );
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
