/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package codestripper;

import java.io.IOException;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class CodeStripperMain {

    public static void main(String[] args) throws IOException {

        Logger logger = LoggerFactory.getLogger( CodeStripperMain.class );
        CodeStripper codeStripper = new CodeStripper( logger,
                Path.of( "target", "stripper-out" ) );
        codeStripper.strip( Path.of( "" ) );
        logger.atInfo().log( "Hello {}", "World!" );
    }
}
