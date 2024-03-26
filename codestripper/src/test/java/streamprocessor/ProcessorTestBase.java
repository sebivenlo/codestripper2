package streamprocessor;

import mytinylogger.Logger;

/**
 * Helper to define logger.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class ProcessorTestBase {

    final Logger logger = new Logger();

    ProcessorFactory newProcessorFactory() {
        return new ProcessorFactory( logger );
    }
}
