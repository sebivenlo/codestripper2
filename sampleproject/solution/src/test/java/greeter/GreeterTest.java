/*
 * It is free.
 */
package greeter;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class GreeterTest {
//@Disabled("think TDD")

    @DisplayName( "some story line" )
    @ParameterizedTest
    @CsvSource(
             {
                "05:45:00,'Good %1$s %2$s', Pieter, 'Good Morning Pieter'",
                //cs:remove:start
                "09:45:00,'Good %1$s %2$s', Richard, 'Good Morning Richard'",
                "13:45:00,'Good %1$s %2$s', Martijn, 'Good Afternoon Martijn'",
                "19:45:00,'Good %1$s %2$s', Ibrahim, 'Good Evening Ibrahim'", //
            //cs:remove:end
            }
    )
    public void testGreeterClock(String clockInput, String format, String name,
            String expected) {
        //cs:remove:start
        Clock clk = setupClock( clockInput );

        Greeter instance = new Greeter( clk, format );

        assertThat( instance.greet( name ) ).isEqualTo( expected );
        //cs:remove:end://fail( "method GreeterClock reached end. You know what to do." );
    }

    final Clock setupClock(String clockInput) {
        LocalTime at = LocalTime.parse( clockInput );
        LocalDateTime ldt = LocalDateTime.of( LocalDate.now(), at );
        ZoneOffset offset = ZoneOffset.UTC;
        Instant instant = ldt.toInstant( offset );
        //        Instant i =
        Clock clk = Clock.fixed( instant, ZoneId.systemDefault() );
        return clk;
    }

}