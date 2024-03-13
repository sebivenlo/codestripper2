package loggerwrapper;

@FunctionalInterface
public interface AppendAndClear {

    /**
     * Append a string to this AppendAndClear.
     *
     * @param toAppend text to add.
     */
    void appendText(String toAppend);

    /**
     * Clear the output. Optional operation.
     */
    default void clear() {
    }
}
