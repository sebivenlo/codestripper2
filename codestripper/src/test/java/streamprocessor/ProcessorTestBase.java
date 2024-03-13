/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package streamprocessor;

import loggerwrapper.DefaultLogger;
import loggerwrapper.Logger;

/**
 * Helper to define logger.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class ProcessorTestBase {

    final Logger logger = new DefaultLogger();

    ProcessorFactory newProcessorFactory() {
        return new ProcessorFactory( logger );
    }
}
