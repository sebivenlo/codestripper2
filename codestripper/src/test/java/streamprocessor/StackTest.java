/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package streamprocessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;

/**
 * Test stack.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class StackTest {

    @DisplayName( "full coverage with one test" )
    @Test
    public void testStack() {
        Stack<String> stack = new Stack<>();

        List<String> input = List.of( "A", "B", "C", "D", "E", "F" );
        for ( String s : input ) {
            stack.push( s );
            String top = stack.peek();
            assertThat( top ).isEqualTo( s );
        }

        var output = new ArrayList<String>( 10 );
        while ( !stack.isEmpty() ) {
            output.add( stack.pop() );
        }
        Collections.reverse( output );
        assertThat( output ).isEqualTo( input );
    }

}
