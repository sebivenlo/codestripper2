/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package io.github.sebivenlo.dependencyfinder;

import java.util.Comparator;
import java.util.Map;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public record Dependency(String groupId, String artifactId, String version,
        String scope, String type, String packaging) implements
        Comparable<Dependency> {

    static final String userHome = System.getProperty( "user.home", "/" );
    static final String fileSep = System.getProperty( "file.separator", "/" );

    public Dependency(Map<String, String> found, Resolver resolver) {
        this(
                found.getOrDefault( "groupId", "puk" ),
                found.getOrDefault( "artifactId", "suck" ),
                // resolve version only
                resolver.resolveValue( found.get( "version" ) ),
                found.getOrDefault( "scope", "compile" ),
                found.getOrDefault( "type", "jar" ),
                found.getOrDefault( "packaging", "jar" )
        );
    }

    public String dirName() {
        return userHome
               + fileSep
               + ".m2"
               + fileSep
               + "repository"
               + fileSep
               + groupId.replaceAll( "\\.", fileSep )
               + fileSep
               + artifactId.replaceAll( "\\.", fileSep )
               + fileSep
               + version()
               + fileSep;
    }

    public String baseName() {
        return dirName() + artifactId() + "-" + version();
    }

    public String toFileName() {
        return baseName() + ".jar";
    }

    public String toPomName() {
        return baseName() + ".pom";
    }

    public String toString() {
        return """
               <dependency>
                  <groupId>%1$s</groupId>
                  <artifactId>%2$s</artifactId>
                  <version>%3$s</version>
                  <scope>%4$s</scope>
                  <type>%5$s</type>
                  <packaging>%6$s</packaging>
               </dependency>
               """
                .formatted( this.groupId, this.artifactId, this.version,
                        this.scope,
                        this.type,
                        this.packaging
                );
    }

    @Override
    public int compareTo(Dependency other) {
        return Comparator.comparing( Dependency::groupId )
                .thenComparing( Dependency::artifactId )
                .thenComparing( Dependency::version )
                .compare( this, other );
    }

}
