/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package codestripper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Starter for CLI version.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class CodeStripperMain {

    /**
     * Entry of program.
     *
     * @param args not used
     * @throws IOException should not occur.
     */
    public static void main(String[] args) throws IOException {
        CodeStripper codeStripper
                = new CodeStripper.Builder()
                        .extraResources( List.of( "../README.md", "../images" ) )
                        .build();
        codeStripper.strip( Path.of( "" ) );
//        logger.info( "Hello World!" );
    }

    /**
     * No instances.
     */
    private CodeStripperMain() {
    }

}
