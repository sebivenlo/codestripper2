/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package codestripper;

import static codestripper.StrippedCodeValidator.pathSep;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.*;
//import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class CompilerAltTest extends StripperTestBase {

    //@Disabled("think TDD")
    @Test @DisplayName( "some story line" )
    public void testCompilerRun() {
        StrippedCodeValidator val = new StrippedCodeValidator( log, locations );
        var sourceFiles = val.getSourceFiles( locations.work()
                .resolve( "src" ) );
        var compileClassPath = val.getSneakyClassPath();
        var options = new String[]{ "-p", compileClassPath,
                                "-sourcepath", "src/main/java" + pathSep + "src/test/java",
                                "-cp", compileClassPath,
                                "-d", locations.out()
                                .toString() };
        for ( String option : options ) {
            System.out.println( "option = " + option );
        }

        final Set<Path> problematicFiles = new HashSet<>();
        final List<String> compilerOutput = new ArrayList<>();
        val.runCompilerAlt( options, sourceFiles, problematicFiles,
                compilerOutput );
        System.out.println( "compilerOutput = " + compilerOutput );
        fail( "method CompilerRun reached end. You know what to do." );

    }

    @Override
    public void cleanup() throws IOException {
//        super.cleanup(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }

}
