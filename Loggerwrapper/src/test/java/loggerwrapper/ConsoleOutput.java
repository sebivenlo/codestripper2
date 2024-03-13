/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package loggerwrapper;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Test helper, to be able to redirect a PrintStream to any writable.
 *
 * Use case: write to string or StringBuilder for test or e.g. javafx
 * TextInputControl for UI. Implement AppendAndClear (which can be done as a
 * lambda) and you're set.
 *
 * @author Pieter van den Hombergh {@code <p.vandenhombergh@fontys.nl>}
 */
public class ConsoleOutput extends OutputStream {

    protected final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final AppendAndClear aac;

    /**
     * Create a Console output copying data to given AppendAndClear.
     *
     * @param ta append and clear to copy all written text to.
     */
    public ConsoleOutput(AppendAndClear ta) {
        this.aac = ta;
    }

    @Override
    public void write(int b) throws IOException {
        if ( aac != null ) {
            String s = "" + ( (char) b );
            aac.appendText( s );
        }
        baos.write( (byte) b );
    }

    /**
     * Clear and discard output.
     */
    public void clear() {
        if ( aac != null ) {
            aac.clear();
        }
        baos.reset();
    }

    /**
     * Get the accumulated string since start or clear.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return new String( baos.toByteArray(), StandardCharsets.UTF_8 );
    }

    /**
     * Factory method to get this ConsoleOutput as PrintStream.
     *
     * @return the print stream.
     */
    public PrintStream asPrintStream() {
        return new PrintStream( this, true, Charset.defaultCharset() );
    }
} // eof ConsoleOutput
