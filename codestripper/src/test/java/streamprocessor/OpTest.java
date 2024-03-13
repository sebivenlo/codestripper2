/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package streamprocessor;

import java.util.List;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.*;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class OpTest extends ProcessorTestBase {

    //@Disabled("think TDD")
    @Test
    @DisplayName( "test remove" )
    public void testRemove() {
        List<String> input = revealTags(
                """
                import java.util.List;
                import com.example.package;//CS:remove
                import java.util.Map;
                """
                        .lines() ).toList();
        List<String> expected = """
                              import java.util.List;
                              import java.util.Map;
                              """
                .lines().toList();
        ProcessorFactory factory = newProcessorFactory();
        assertThat( input.stream()
                .map( factory::apply ) // wrap in recipe
                .flatMap( x -> x ) // flatten the result
                //                .peek(l-> out.println("out "+ l))
                .toList() ).isEqualTo( expected );
    }

    @Test
    @DisplayName( "test remove range" )
    public void testRemoveRange() {
        List<String> input = revealTags(
                """
                import java.util.List;
                //CS:remove:start
                import com.example.package;
                import com.example.package2;
                //CS:remove:end
                import java.util.Map;
                """
                        .lines() ).toList();
        List<String> expected = """
                              import java.util.List;
                              import java.util.Map;
                              """
                .lines().toList();
        ProcessorFactory factory = newProcessorFactory();
        assertThat( input.stream()
                .map( factory::apply ) // wrap in recipe
                .flatMap( x -> x ) // flatten the result
                //                .peek(l-> out.println("out "+ l))
                .toList() ).isEqualTo( expected );
    }

    @Test
    @DisplayName( "test add" )
    public void testAdd() {
        List<String> input = revealTags(
                """
                              import java.util.List;
                              //CS:add:import com.example.package;
                              import java.util.Map;
                              """
                        .lines() ).toList();
        List<String> expected = """
                              import java.util.List;
                              import com.example.package;
                              import java.util.Map;
                              """
                .lines().toList();
        ProcessorFactory factory = newProcessorFactory();
        assertThat( input.stream()
                .map( factory::apply ) // wrap in recipe
                .flatMap( x -> x ) // flatten the result
                //                .peek(l-> out.println("out "+ l))
                .toList() ).isEqualTo( expected );
    }

    @Test
    @DisplayName( "test comment" )
    public void testComment() {
        List<String> input = revealTags(
                """
                              import java.util.List;
                              import com.example.package;//CS:comment
                              import java.util.Map;
                              """
                        .lines() ).toList();
        List<String> expected = """
                              import java.util.List;
                              //import com.example.package;
                              import java.util.Map;
                              """
                .lines().toList();
        ProcessorFactory factory = newProcessorFactory();
        assertThat( input.stream()
                .map( factory::apply ) // wrap in recipe
                .flatMap( x -> x ) // flatten the result
                //                .peek(l-> out.println("out "+ l))
                .toList() ).isEqualTo( expected );
    }

    @Test
    @DisplayName( "test comment range" )
    public void testCommentRange() {
        List<String> input = revealTags(
                """
                             import java.util.List;
                             //CS:comment:start
                             import com.example.package;
                             import java.util.Map;
                             //CS:comment:end
                             import java.util.Set;
                              """
                        .lines() ).toList();
        List<String> expected = """
                              import java.util.List;
                              //import com.example.package;
                              //import java.util.Map;
                              import java.util.Set;
                              """
                .lines().toList();
        ProcessorFactory factory = newProcessorFactory();
        assertThat( input.stream()
                .map( factory::apply ) // wrap in recipe
                .flatMap( x -> x ) // flatten the result
                //                .peek(l-> out.println("out "+ l))
                .toList() ).isEqualTo( expected );
    }

    @Test
    @DisplayName( "test Uncomment" )
    public void testUnComment() {
        List<String> input = revealTags(
                """
                              import java.util.List;
                              //import com.example.package;//CS:uncomment
                              import java.util.Map;
                              """
                        .lines() ).toList();
        List<String> expected = """
                              import java.util.List;
                              import com.example.package;
                              import java.util.Map;
                              """
                .lines().toList();
        ProcessorFactory factory = newProcessorFactory();
        assertThat( input.stream()
                .map( factory::apply ) // wrap in recipe
                .flatMap( x -> x ) // flatten the result
                //                .peek(l-> out.println("out "+ l))
                .toList() ).isEqualTo( expected );
    }

    @Test
    @DisplayName( "test comment range" )
    public void testUnCommentRange() {
        List<String> input = revealTags(
                """
                             import java.util.List;
                             //CS:uncomment:start
                             //import com.example.package;
                             //import java.util.Map;
                             //CS:comment:end
                             import java.util.Set;
                              """
                        .lines() ).toList();
        List<String> expected = """
                              import java.util.List;
                              import com.example.package;
                              import java.util.Map;
                              import java.util.Set;
                              """
                .lines().toList();
        ProcessorFactory factory = newProcessorFactory();
        assertThat( input.stream()
                .map( factory::apply ) // wrap in recipe
                .flatMap( x -> x ) // flatten the result
                //                .peek(l-> out.println("out "+ l))
                .toList() ).isEqualTo( expected );
    }

    //@Disabled("think TDD")
    @Test @DisplayName( "Payload should replace the tag line on start and end" )
    public void testmethodPreservePlayload() {
        List<String> input = revealTags(
                """
                             import java.util.List;
                             //CS:remove:start//TODO write your solution here
                             import com.example.package;
                             import java.util.Map;
                             //CS:remove:end://You should solve it with two lines only.
                             import java.util.Set;
                              """
                        .lines() ).toList();
        List<String> expected = """
                              import java.util.List;
                              //TODO write your solution here
                              //You should solve it with two lines only.
                              import java.util.Set;
                              """
                .lines().toList();
        ProcessorFactory factory = newProcessorFactory();
        assertThat( input.stream()
                .map( factory::apply ) // wrap in recipe
                .flatMap( x -> x ) // flatten the result
                .toList() ).isEqualTo( expected );
//        fail( "method methodPreservePlayload reached end. You know what to do." );
    }

    @Test
    @DisplayName( "test remove with payload" )
    public void testRemoveReplace() {
        List<String> input = revealTags(
                """
                   import java.util.List;
                   import com.example.package;//CS:remove://Add proper import statement
                   import java.util.Map;
                   """
                        .lines() ).toList();
        List<String> expected = """
                              import java.util.List;
                              //Add proper import statement
                              import java.util.Map;
                              """
                .lines().toList();
        ProcessorFactory factory = newProcessorFactory();
        assertThat( input.stream()
                .map( factory::apply ) // wrap in recipe
                .flatMap( x -> x ) // flatten the result
                //                .peek(l-> out.println("out "+ l))
                .toList() ).isEqualTo( expected );
    }

    //@Disabled("think TDD")
    @Test @DisplayName( "nop escapes removal" )
    public void testNopEscapes() {
        var input
                = revealTags(
                        """
                import java.util.List;
                //CS:remove:start//TODO add your solution here
                import com.example.package;
                import java.util.HashMap;//CS:nop
                import java.util.Map;
                //CS:remove:end://You should solve it with two lines only.
                import java.util.Set;
                """
                                .lines() ).toList();

        var expected
                = """
                import java.util.List;
                //TODO add your solution here
                import java.util.HashMap;
                //You should solve it with two lines only.
                import java.util.Set;
                """
                        .lines().toList();
        ProcessorFactory factory = newProcessorFactory();
        assertThat( input.stream()
                .map( factory::apply ) // wrap in recipe
                .flatMap( x -> x ) // flatten the result
                //                .peek(l-> out.println("out "+ l))
                .toList() ).isEqualTo( expected );

//        fail( "method NopEscapes reached end. You know what to do." );
    }

    //@Disabled("think TDD")
    @Test @DisplayName( "uncomment single line" )
    public void testUncommentSingle() {
        var input = revealTags(
                """
            public class Test {
            //      fail( "method NopEscapes reached end. You know what to do." );//CS:uncomment
            }
            """
                        .lines() ).toList();
        var expected = """
            public class Test {
                  fail( "method NopEscapes reached end. You know what to do." );
            }
            """
                .lines().toList();
        ProcessorFactory factory = newProcessorFactory();
        assertThat( input.stream()
                .map( factory::apply ) // wrap in recipe
                .flatMap( x -> x ) // flatten the result
                //                .peek(l-> out.println("out "+ l))
                .toList() ).isEqualTo( expected );

//        fail( "method UncommentSingle reached end. You know what to do." );
    }

    //@Disabled("think TDD")
    @Test @DisplayName( "ignore the remainder" )
    public void testDeathTrap() {
        var input = revealTags(
                """
            //CS:ignore
            public class Test {
            //      fail( "method NopEscapes reached end. You know what to do." );//CS:uncomment
            }
            """
                        .lines() ).toList();
        var expected = List.of();
        var factory = newProcessorFactory();
        assertThat( input.stream()
                .map( factory::apply ) // wrap in recipe
                .flatMap( x -> x ) // flatten the result
                //                .peek(l-> out.println("out "+ l))
                .toList() ).isEqualTo( expected );

//        fail( "method UncommentSingle reached end. You know what to do." );
    }

    public static Stream<String> revealTags(Stream<String> in) {
        return in.map( l -> l.replaceFirst( "CS:", "cs" + ":" ) );
    }
}
