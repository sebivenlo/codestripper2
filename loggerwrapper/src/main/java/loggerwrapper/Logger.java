/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package loggerwrapper;

import java.util.function.Supplier;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public interface Logger {

    void debug(Supplier<String> msg);

    void error(Supplier<String> msg);

    void fine(Supplier<String> msg);

    void info(Supplier<String> msg);

    void warn(Supplier<String> msg);

    Logger level(LoggerLevel level);

    LoggerLevel level();
}
