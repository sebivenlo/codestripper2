package codestripper;

import java.util.List;
import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.*;
import static java.util.stream.Collectors.joining;
/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class TagMigratorTest extends StripperTestBase {
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
                """.lines().toList();

    List<String> expected
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
                """.lines().toList();

    //@Disabled("think TDD")
    @Test @DisplayName( "test migrate Line" )
    public void testMigrateString() {
        TagMigrator m = new TagMigrator( log, locations );
        var actual = source.stream()
                .map( m::migrateLine )
                .toList();
        assertThat( actual ).isEqualTo( expected );
        fail( "method MigrateString reached end. You know what to do." );
    }

}
