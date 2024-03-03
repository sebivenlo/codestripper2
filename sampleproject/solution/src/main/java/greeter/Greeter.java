package greeter;

import java.time.Clock;
import java.time.LocalTime;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public record Greeter(Clock clock, String format) implements SayHi {

    @Override
    public String timeOfDay() {
        String result = "";
        //cs:remove start
        LocalTime lt = LocalTime.now( clock );
        int dayPhase = lt.getHour() / 6;
        result
                = switch ( dayPhase ) {
            case 0 ->
                "Night";
            case 1 ->
                "Morning";
            case 2 ->
                "Afternoon";
            case 3 ->
                "Evening";
            case 4 ->
                "Night";
            case 5 ->
                "Night";
            default ->
                "Day";

        };
        //cs:remove:end
        return result;
    }

    public Greeter(String format) {
        this( java.time.Clock.systemDefaultZone(), format );
    }
}
