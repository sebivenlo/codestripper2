/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.github.sebivenlo.dependencyfinder;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resolves a macro name to a property.
 *
 * The property is used like {@code <version>${propName}</version>} in maven files.
 * This resolver looks up the property and returns the value in the map, if any.
 *
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class Resolver {

    private final Map<String, String> properties;

    /**
     * A resolver needs a map.
     * @param properties map
     */
    Resolver( Map<String, String> properties ) {
        this.properties = properties;
    }

    Pattern resolverPattern = Pattern.compile( "\\$\\{(?<name>.*?)\\}" );

    String resolveValue( String value ) {
        if ( value == null ) {
            return "";
        }
        Matcher matcher = resolverPattern.matcher( value );
        if ( matcher.lookingAt() ) {
            String key = matcher.group( "name" );
            return properties.getOrDefault( key, value );
        }
        return value;
    }

}
