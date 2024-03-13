package streamprocessor;

import codestripper.LoggerLevel;
import static codestripper.LoggerLevel.DEBUG;
import static codestripper.LoggerLevel.FINE;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import static java.util.Map.entry;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import static java.util.stream.Stream.of;
import org.apache.maven.plugin.logging.Log;

/**
 * Creator of Processor boxes based on the content os strings and previous
 * Strings.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class ProcessorFactory implements Function<String, Stream<String>> {

    /**
     * what we consider java files.
     */
    public static final Path JAVA_PATH = Path.of( ".java" );
    /**
     * The secret sauce.
     */
    private final String myPreciousRegex;
    private final Pattern pattern;

    /**
     * Create a factory for the type of file specified by this path.
     *
     * @param filePath to use
     */
    public ProcessorFactory(Path filePath, Log log) {
        this( filePath, "cs", log );
    }
    private final Log logger;

    public ProcessorFactory(Log logger) {
        this( JAVA_PATH, "cs", logger );
    }

    /**
     * Create a factory for the given file and specify the tag
     *
     * @param filePath to use
     * @param tag to use
     */
    public ProcessorFactory(Path filePath, String tag, Log log) {
        commentToken = commentTokenFor( filePath );
        myPreciousRegex
                = "(?<indent>\\s*)" //optional indentation
                + "(?<text>\\S.*)?" // anything other starting with non space
                + "(?<commentToken>" + commentToken + ")" // mandatory comment token
                + tag // required tag, split over two lines to self-protect against stripping
                + ":"
                + "(?<instruction>\\w+)" // required instruction group
                + "(:(?<startEnd>(start|end)))?" // optional start end group
                + ":?(?<payLoad>(.*$))?" // optional  payLoad
                ;
        pattern = Pattern.compile( myPreciousRegex );
        this.transforms = new HashMap<>( defaultTransforms );
        this.logger = log;
    }

    /**
     * Get the used regular expression for testing.
     *
     * @return the pattern
     */
    Pattern getPattern() {
        return pattern;
    }

    final Map<String, Function<Processor, Stream<String>>> transforms;

    Matcher matcherFor(String line) {
        Matcher m = pattern.matcher( line );
        return m;
    }

    /**
     * For testing
     *
     * @return the known set of instructions.
     *
     */
    String[] getInstructions() {
        return transforms.keySet().stream().sorted().toArray( String[]::new );
    }

    /**
     * Create a processor box for the given line.
     *
     * Processor.apply will produce a {@code Stream<String>} with 0 1 or more
     * lines.
     *
     * @param line for the processor.
     * @return a processor for the line.
     */
    public Processor processorFor(String line) {
        if ( activeTransformation == ignore ) {
            return deathTrap;
        }
        Matcher m = pattern.matcher( line );
        if ( m.matches() ) {
            String instruction = m.group( "instruction" ).trim();
            var startEndText = m.group( "startEnd" );
            // avoid NPE on lines without startEnd
            var startEnd = null == startEndText ? "" : startEndText;
            var payLoad = m.group( "payLoad" );
            var text = m.group( "text" );
            var indent = m.group( "indent" );
            var transformation = transformFor( instruction );
            if ( "ignore".equals( instruction ) ) {
                // snap the trap.
                activeTransformation = ignore;
                return deathTrap;
            }
            if ( "start".equals( startEnd ) ) {
                // pickup the transformation
                activeTransformation = transformation;
                logDebug( () -> "start " + instruction + " at line "
                        + lineNumber + ": " + line );
                // but remove current line
                logDebug( () -> "start remove at " + ( lineNumber + 1 ) );
                transformation = remove;
            }
            if ( "end".equals( startEnd ) ) {
                activeTransformation = nop;
                logDebug( () -> "end " + instruction + " at line "
                        + lineNumber + ": " + line );
                // but remove current line
                transformation = remove;
                logDebug( () -> "stop remove at " + ( lineNumber + 1 ) );
                if ( openStart.peek().instruction().equals( instruction ) ) {
                    openStart.pop();
                }
            }
            var result = new Processor( line, payLoad, transformation, instruction,
                    ++lineNumber, text, indent, startEnd );

            if ( "start".equals( startEnd ) ) {
                openStart.push( result );
            }
            logFine(
                    () -> "execute " + result.instruction() + " at line " + result
                    .lineNumber() + ": " + result + line );
            return result;
        }
        // lines without instructions are subject to activeTansformation
        return newProcessor( line );
    }

    // shorthand for non tagged lines factories
    private Processor newProcessor(String line) {
        return new Processor( line, "", activeTransformation, "", ++lineNumber,
                line, "",
                "" );
    }

    /**
     * Apply the creation of a processor and applying its effect on the input
     * string. Collapses two mappings into 1.
     *
     * @param line to process, remove or replace.
     * @return The result of the processor box applied
     */
    @Override
    public Stream<String> apply(String line) {
        var x = processorFor( line );
        var applied = x.apply( x );
        return applied;
    }

    int lineNumber = 0;

    Stream<String> include(Processor proc) {
        try {
            return Files.lines( Path.of( proc.payLoad().trim() ) ).map(
                    l -> proc.indent() + l );
        } catch ( IOException ex ) {

            logger.error( ex.getMessage() );
            return Stream.empty();
        }
    }
    final Function<Processor, Stream<String>> ignore = p -> Stream.empty();
    final Function<Processor, Stream<String>> replace = p -> of( p.indent() + p
            .payLoad() );
    final Function<Processor, Stream<String>> add = p -> of( p.indent() + p
            .payLoad() );
    final Function<Processor, Stream<String>> uncomment
            = p -> of( p.indent() + p.text().replaceFirst( "//", "" ) );
    final Function<Processor, Stream<String>> comment
            = p -> of( p.indent() + "//" + p.text() );
    final Function<Processor, Stream<String>> nop
            = p -> of( p.indent() + p.text() );
    final Function<Processor, Stream<String>> remove
            = p -> {
                if ( !p.payLoad().isBlank() ) {
                    logFine( () -> "replaced line '" + p.lineNumber()
                            + ":'" + p.line() + "'" );
                    return Stream.of( p.indent() + p.payLoad() );
        }
        logFine( () -> "dropped line '" + p.lineNumber()
                + ":'" + p.line() + "'" );
        return Stream.empty();
            };

    final Function<Processor, Stream<String>> include
            = this::include;
    final Function<Processor, Stream<String>> UPPER
            = p -> Stream.of( p.text().toUpperCase() );
    final Function<Processor, Stream<String>> lower
            = p -> Stream.of( p.text().toLowerCase() );
    static final Function<Processor, Stream<String>> replaceFirst
            = ( Processor p ) -> {
                String separator = p.payLoad().substring( 0, 1 );
                String[] split = p.payLoad().substring( 1 ).split( separator );
                String result = p.text().replaceFirst( split[ 0 ], split[ 1 ] );
                return of( result );
            };
    static final Function<Processor, Stream<String>> replaceAll
            = ( Processor p ) -> {
                String separator = p.payLoad().substring( 0, 1 );
                String[] split = p.payLoad().split( separator, 2 );
                String result = p.text().replaceAll( split[ 0 ], split[ 1 ] );
                return of( result );
            };

    Function<Processor, Stream<String>> activeTransformation = nop;
    final Processor deathTrap = new Processor( "", "", ignore,
            "ignore", 0, "//cs:ignore", "", "start" );

    // lookup
    final Map<String, Function<Processor, Stream<String>>> defaultTransforms = Map
            .ofEntries(
                    entry( "add", add ),
                    entry( "comment", comment ),
                    entry( "replace", replace ),
                    entry( "nop", nop ),
                    entry( "uncomment", uncomment ),
                    entry( "remove", remove ),
                    entry( "UPPER", UPPER ),
                    entry( "lower", lower ),
                    entry( "include", include ),
                    entry( "ignore", ignore ),
                    entry( "replaceFirst", replaceFirst ),
                    entry( "replaceAll", replaceAll )
            );

    final Function<Processor, Stream<String>> transformFor(
            String line) {
        return transforms.getOrDefault( line, nop );
    }

    /**
     * Keep use of Processor Factory simple for default case
     */
//    public static class Factory {
    final String commentToken;

    static String commentTokenFor(Path p) {
        var filename = p.getFileName().toString();
        int lastIndex = filename.lastIndexOf( "." );
        if ( lastIndex < 0 ) {
            return "#";
        }
        String extension = filename.substring( lastIndex + 1 );

        return switch ( extension ) {
            case "java" ->
                "//";
            case "sql" ->
                "--";
            case "bat", "cmd" ->
                "@REM";
            case "py", "sh" ->
                "#";
            default ->
                "#";
        };
    }

    private Stack<Processor> openStart = new Stack();

    /**
     * Are the warning during stripping.
     *
     * @return true if warnings were generated.
     */
    public boolean hasDanglingTag() {
        return !openStart.isEmpty();
    }

    /**
     * Return dangling tag info, that is the last tag not closed.
     *
     * @return the dangling tag info.
     */
    public String danglingTags() {
        if ( openStart.isEmpty() ) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        while ( !openStart.isEmpty() ) {
            Processor top = openStart.pop();
            sb.append( "\n\t" ).append( top.lineNumber() )
                    .append( " :'" )
                    .append( top.line() )
                    .append( "´" );
        }
        return sb.toString();
    }

    private LoggerLevel logLevel = FINE;

    /**
     * Set the logging level.
     *
     * @param level for the logging.
     * @return this.
     */
    public ProcessorFactory logLevel(LoggerLevel level) {
        this.logLevel = level;
        return this;
    }

    void logFine(Supplier<String> msg) {
        if ( this.logLevel.compareTo( FINE ) >= 0 ) {
            logger.info( msg.get() );
        }
    }

    void logDebug(Supplier<String> msg) {
        if ( this.logLevel.compareTo( DEBUG ) >= 0 ) {
            logger.info( msg.get() );
        }
    }

}
