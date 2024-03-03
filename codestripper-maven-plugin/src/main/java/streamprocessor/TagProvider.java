/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package streamprocessor;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Plugin definition.
 * 
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public interface TagProvider {
    
    /**
     * A plugin should provide a map of instruction names and functions
     * that implement that instruction implementation.
     * 
     * @return the map
     */
  Map<String,Function<Processor,Stream<String>>> newTags();
}
