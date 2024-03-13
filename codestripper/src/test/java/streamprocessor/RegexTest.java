package streamprocessor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class RegexTest extends ProcessorTestBase {

    //@Disabled("think TDD")
    @DisplayName( "Test the regex" )
    @ParameterizedTest
    @CsvSource( {
        "something//cs" + ":remove:start:pay me,true, 9,remove,start,true",
        "something else,false,0,'','',false",
        "something//cs" + ":replace:pay me,true, 9,replace,start,true",
        "something//cs" + ":remove:pay me,true, 9,remove,null,true",
        "something//cs" + ":remove:end,true, 9,remove,end,false"

    } )
    public void testMethod(String input, boolean matches, int groupCount,
            String expectInstruction, String startEndExpected,
            boolean hasPayload) {

        ProcessorFactory fact = new ProcessorFactory( logger );
        Pattern pattern = fact.getPattern();
//        System.out.println( "input = " + input );
        Matcher m = pattern.matcher( input );
        boolean assume = m.lookingAt();
        //assumeThat( assume ).isTrue();
        if ( assume ) {
            assertSoftly( softly -> {
                String instruct = m.group( "instruction" );
                softly.assertThat( m.groupCount() ).isEqualTo( groupCount );
                softly.assertThat( instruct )
                        .isEqualTo( expectInstruction );
                String payLoad = m.group( "payLoad" );
                String startEnd = m.group( "startEnd" );
                softly.assertThat( payLoad.isEmpty() ).isEqualTo( !hasPayload );
                System.out.println( "startEnd = " + startEnd );
                System.out.println( "payLoad = '" + payLoad + "'" );
                System.out.println( "instruction = " + instruct );
            } );
        }

//        fail( "method Method reached end. You know what to do." );
    }

}
