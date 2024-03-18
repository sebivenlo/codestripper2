package codestripper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import static java.util.stream.Collectors.joining;
import mytinylogger.DefaultLogger;
import loggerwrapper.Logger;
import static loggerwrapper.LoggerLevel.INFO;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assumptions.assumeThat;
import org.assertj.core.api.ThrowableAssert;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.*;
import codestripper.CodeStripperValidationException;
/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class StrippedCodeValidatorTest {

    static Path pwd = Path.of( System.getProperty( "user.dir" ) );
    static Logger log = new DefaultLogger().level( INFO );
    Path tempDir;
    final PathLocations locations;
    final StrippedCodeValidator validator;
    final CodeStripper stripper;

    public StrippedCodeValidatorTest() {

        Logger log = new DefaultLogger();
        try {
            Path sampleproject = Path.of( "..",
                    "sampleproject", "example" ).toAbsolutePath();
            assumeThat( sampleproject ).exists();
            tempDir = Files.createTempDirectory( "codestripper-" + this
                    .getClass().getSimpleName() + "-" );
            locations = new PathLocations( log, sampleproject, tempDir );
            stripper = new CodeStripper.Builder()
                    .logger( log )
                    .pathLocations( locations )
                    .dryRun( false )
                    .extraResources( List.of( "../README.md", "../images" ) )
                    .build();
            stripper.strip();
            validator = new StrippedCodeValidator( log, locations );
        } catch ( IOException ex ) {
            log.error( () -> ex.getMessage() );
            throw new IllegalArgumentException( ex );
        }
    }

    //@Disabled("think TDD")
    @Test
    @DisplayName( "Get source files" )
    public void testGetSourceFiles() {
        Path sourceDir = locations.strippedProject().resolve( "src" );
        String[] sourceFiles = validator.getSourceFiles( sourceDir );

        // massage the input paths
        var actual = Arrays.asList( sourceFiles )
                .stream().map( l -> locations.strippedProject().relativize( Path
                .of( l ) ) )
                .toList();
        var expected = List.of(
                Path.of(
                        "src/main/java/greeter/BrokenOnPurpose.java" ),
                Path.of(
                        "src/test/java/greeter/GreeterTest.java" )
        );
        assertThat( actual ).containsAll( expected );
//        fail( "method SourceFiles reached end. You know what to do." );
    }

//    @Disabled( "think TDD" )
    @Test
    @DisplayName( "compiler args " )
    public void testCompilerArgs() throws IOException {
        String[] args = validator.makeCompilerArguments( locations
                .strippedProject().resolve( "src" ), validator.makeOutDir() );
        // cleanup
//        this.outDir.toFile().delete();
        System.out.println( Arrays.stream( args ).collect( joining( " " ) ) );
        assertThat( args ).isNotEmpty();
//        fail( "method CompilerArgs reached end. You know what to do." );
    }

    //@Disabled("think TDD")
    @Test @DisplayName( "some story line" )
    public void testGetClassPath() {
        assumeThat( locations.strippedProject() ).exists();
        ThrowingCallable code = () -> {
            String sneakyClassPath = validator.getSneakyClassPath();
            System.out.println( "sneakyClassPath = " + sneakyClassPath );
        };

        assertThatCode( code ).doesNotThrowAnyException();
        //fail( "method GetClassPath reached end. You know what to do." );
    }

//    @Disabled( "think TDD" )
    @Test
    @DisplayName( "run the compiler" )
    public void testCompilerRun() throws IOException {
        Path strippedProject = locations.strippedProject();
        System.out.println( "strippedProject = " + strippedProject );
        assumeThat( strippedProject.resolve( "src" ) ).exists();
        System.out.println( "strippedProject = " + strippedProject.toString() );

        ThrowableAssert.ThrowingCallable code = () -> {
            validator.validate();
        };

        assertThatThrownBy( code ).isExactlyInstanceOf(
                CodeStripperValidationException.class );
//        fail( "method CompilerRun reached end. You know what to do." );
    }
}
