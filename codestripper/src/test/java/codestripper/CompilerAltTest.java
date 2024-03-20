/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package codestripper;

import static codestripper.StrippedCodeValidator.pathSep;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.tools.Diagnostic;
import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.*;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;

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
        var compileClassPath = val.getClassPath();
        var options = List.of( "-p", compileClassPath,                                "-sourcepath", locations.work()
                                .resolve( "src/main/java" ) + pathSep + locations
                                .work()
                                .resolve( "src/test/java" ),
                                "-cp", compileClassPath,
                                "-d", locations.out()
                        .toString() );

        final Map<Path, Diagnostic> problematicFiles = new HashMap<>();
        final List<String> compilerOutput = new ArrayList<>();
        ThrowingCallable code = () -> {
            val.runCompilerAlt( options, sourceFiles, problematicFiles,
                    compilerOutput );
        };
        assertThatCode( code )
                .doesNotThrowAnyException();

    }

}
