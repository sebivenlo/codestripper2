/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package streamprocessor;

import codestripper.LoggerLevel;
import codestripper.LoggerWrapper;
import org.apache.maven.plugin.logging.SystemStreamLog;

/**
 * Helper to define logger.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class ProcessorTestBase {

    final LoggerWrapper logger = new LoggerWrapper( new SystemStreamLog(),
            LoggerLevel.INFO );

    ProcessorFactory newProcessorFactory() {
        return new ProcessorFactory( logger );
    }
}
