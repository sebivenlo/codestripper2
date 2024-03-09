
package streamprocessor;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Box that processes its content with a given function into a Stream of
 * Strings.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 *
 * @param line the original complete line
 * @param payLoad after the tag and instruction
 * @param transformation the transformation to be effective for the current line
 * @param instruction the instruction word found in the line
 * @param lineNumber sic
 * @param text the text before the comment and tag
 * @param indent the indentation to apply
 * @param startEnd '', 'start' or 'end'
 */
public record Processor(String line, String payLoad,
        Function<Processor, Stream<String>> transformation,
        String instruction, int lineNumber, String text, String indent,
        String startEnd) implements
        Function<Processor, Stream<String>> {

    @Override
    public Stream<String> apply(Processor proc) {
        return this.transformation.apply( this );
    }

    @Override
    public String toString() {
        return lineNumber + "  line=(" + line + ") - payload {" + payLoad + "}- instruction<" + instruction +
                ">\" text:'"+text+"'"+ " indent:["+indent+"]="+indent.length();
    }

}
