/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package codestripper;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class BuilderTest extends StripperTestBase {
//@Disabled("think TDD")

//    @Test @DisplayName( "test the builder" )
    public void testBuilder() throws IOException {

        CodeStripper.Builder builder = new CodeStripper.Builder();
        var stripper = builder
                .dryRun( true )
                .extraResources( List.of( "../LICENSE" ) )
                .build();

//        try {
//            assertThat( stripper.expandedArchive() ).startsWith( pwd
//                    .resolve( "target" ).resolve( "stripper-out" ) );
//        } catch ( Throwable t ) {
//            t.printStackTrace();
//            throw new RuntimeException( t );
//        }
//        fail( "method Builder reached end. You know what to do." );
    }
}
