package migrator;

import migrator.TagMigrator;
import java.util.List;
import java.util.regex.Matcher;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class TagMigratorTest {

    private static String lineSep = System.getProperty( "line.separator" );

    List<String> source
            = """
                //Start Solution::replaceWith:://TODO define Class Hello
                public class Hello{
                //End Solution
                    public static void main(String[] args) {
                        //Start Solution
                        System.out.println( "Hello "+Arrays.toString(args) );
                        //End Solution::replaceWith::// TODO be nice
                    }
                }
                """
                    .lines().toList();

    List<String> expected
            = """
                //cs:remove:start://TODO define Class Hello
                public class Hello{
                //cs:remove:end
                    public static void main(String[] args) {
                        //cs:remove:start
                        System.out.println( "Hello "+Arrays.toString(args) );
                        //cs:remove:end:// TODO be nice
                    }
                }
                """
                    .lines().toList();

    //@Disabled("think TDD")
    @Test @DisplayName( "some story line" )
    public void testMigrateString() {
        TagMigrator m = new TagMigrator();
        var actual = source.stream()
                .map( m::migrateLine ).toList();

        assertThat( actual ).isEqualTo( expected );
//        fail( "method MigrateString reached end. You know what to do." );
    }

    //@Disabled("think TDD")
    @ParameterizedTest
    @DisplayName( "test the regex" )
    @CsvSource( {
        "//Start Solution,5",
        "//Start Solution::replaceWith:://TODO ,5",
        "//End Solution::replaceWith:://TODO ,5",
        "//End Solution,5"//
    } )
    public void testRegex(String line, int groupCount) {
        Matcher m = TagMigrator.myPreciousPattern.matcher( line );

        assertThat( m ).matches();
        assertThat( m.groupCount() ).isEqualTo( groupCount );
//        fail( "method Regex reached end. You know what to do." );
    }
}
