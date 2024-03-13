package streamprocessor;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class TransformerTest extends ProcessorTestBase {

    //@Disabled("think TDD")
    @Test @DisplayName( "Test that remove works" )
    public void testRemove() {
        String in = "some text;//cs" + ":remove";
        ProcessorFactory fac = newProcessorFactory();
        Matcher m = fac.matcherFor( in );
        assertThat( m ).matches();
        String ins = m.group( "instruction" );
        assertThat( ins ).isEqualTo( "remove" );
        Processor processorFor = fac.processorFor( in );
        var apply = processorFor.apply( processorFor );
        assertThat( apply ).isEmpty();
        System.out.println( "ins = " + ins );

//        fail( "method Remove reached end. You know what to do." );
    }

    List<String> input
            = revealTags(
                    """
            SOME BIG STORY//CS:lower
            example text;//CS:replace://TODO
            example2 text;//CS:nop:
            //CS:remove:start
            example3 the solution
            //CS:remove:end
            More text
            //CS:include:humpty.txt
            fin//CS:UPPER
            """
                            .lines()
            )
                    .toList();

    List<String> expected = """
            some big story
            //TODO
            example2 text;
            More text
            Humpty Dumpty sat on a wall.
            Humpty Dumpty had a great fall.
            All the king's horses and all the king's men
            Couldn't put Humpty together again.
            FIN
            """
            .lines().toList();

    //@Disabled("think TDD")
    @Test @DisplayName( "Complete example" )
    public void testComleteExample() {
        var fac = newProcessorFactory();
        var result = input.stream()
                .map( fac::apply )
                .flatMap( x -> x )
                .toList();

        assertThat( result ).isEqualTo( expected );

//        fail( "method ComleteExample reached end. You know what to do." );
    }

    //@Disabled("think TDD")
    @Test @DisplayName( "some story line" )
    public void testLeaveQuotesAlone() {
        var input = Arrays.asList(
                new String[]{ "pay me,true, 6,remove,start,true",
                    "pay me,true, 6,replace,start,true",
                    "pay me,true, 6,remove,null,true",
                    ",true, 6,remove,end,false"
                } );

        var fac = newProcessorFactory();
        var result = input.stream()
                .map( fac::apply )
                .flatMap( x -> x )
                .toList();
        assertThat( result ).isEqualTo( input );
//        fail( "method LeaveQuotesAlone reached end. You know what to do." );
    }

    public static Stream<String> revealTags(Stream<String> in) {
        return in.map( l -> l.replaceFirst( "CS:", "cs" + ":" ) );
    }
}
