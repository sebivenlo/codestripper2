package io.github.sebivenlo.dependencyfinder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.joining;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class DependencyFinder {

    public static void main(String[] args) {
        String target = "pom.xml";
        if ( args.length > 0 ) {
            target = args[ 0 ];
        }
//        configureLogger();

        Path fileName = Path.of( target );
        DependencyFinder finder = new DependencyFinder( fileName );
        Collection<Dependency> found = finder.find();

    }

    static final String pathSep = System.getProperty( "path.separator", ":" );

    private final Path pomFileName;

    public DependencyFinder(Path fileName) {
        this.pomFileName = fileName;
    }

    public static String testCompileclassPath() {
        return new DependencyFinder( Path.of( "pom.xml" ) ).find()
                .stream()
                .map( Dependency::toFileName )
                .collect( joining( pathSep ) );
    }

    Collection<Dependency> find() {
        return find( this.pomFileName );
    }

    Collection<Dependency> find(Path fileName) {
        MyHandler myHandler = new MyHandler( fileName );
        try {

            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();

            sp.parse( fileName.toFile(), myHandler );
            final Collection<Dependency> result = myHandler.result();
            for ( Dependency dependency : result ) {
            }
            return result;
        } catch ( IOException | ParserConfigurationException | SAXException e ) {
        }
        return List.of();
    }

    Set<Dependency> seenDeps = new LinkedHashSet<>();

    final Map<String, String> properties = new LinkedHashMap<>();

    class MyHandler extends DefaultHandler {

        private boolean inParent = false;
        boolean inProperties = false;
        boolean inDependency = false;
        boolean inDependencies = false;
        boolean inBuild = false;

        private final Set<Dependency> result = new LinkedHashSet<>();
        private final Set<Dependency> resultParent = new LinkedHashSet<>();

        public Collection<Dependency> result() {
            return result;
        }

        MyHandler(Path fileName) {
            pathStack.push( fileName.toString() );
        }

        boolean inProject() {
            return !( inParent || inProperties || inDependency );
        }
        PeekDownStack<String> pathStack = new PeekDownStack( 10 );

        @Override
        public void startElement(String uri, String localName, String qName,
                Attributes attributes)
                throws SAXException {
            pathStack.push( qName );

            switch ( qName ) {
                case "properties" -> inProperties = true;
                case "dependency" -> inDependency = true;
                case "dependencies" -> inDependencies = true;
                case "build" -> inBuild = true;
                case "parent" -> inParent = true;
            }
        }

        Set<String> interesting
                = Set.of( "groupId", "artifactId", "version", "type", "scope",
                        "packaging", "relativePath" );

        Map<String, String> collectedDependency = new LinkedHashMap<>();
        Map<String, String> rootCoordinates = new LinkedHashMap<>();
        List<Dependency> children = new ArrayList<>();

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            final var qNameT = qName.trim();
            if ( inProperties ) {
                if ( !characters.isBlank() ) {
                    properties.put( qNameT, characters );
                }
            }

            if ( interesting.contains( qName ) ) {
                if ( ( inParent || inDependency ) && !( inBuild ) ) {
                    collectedDependency.put( qNameT, characters );
                }

//                if ( inProject() ) {
//                    rootCoordinates.putIfAbsent( qNameT, characters );
//                }
            }

            if ( inDependencies && qNameT.equals( "dependency" ) && collectedDependency
                    .containsKey( "groupId" ) ) {
                Dependency dep = new Dependency( collectedDependency,
                        new Resolver( properties ) );
                collectedDependencyClear();

                boolean added = seenDeps.add( dep );
                if ( added ) {
                    if ( Files.exists( Path.of( dep.toFileName() ) ) ) {
                        result.add( dep );
                        result.addAll( find( Path.of( dep.toPomName() ) ) );
                    }
                }
            }

            if ( inParent && qNameT.equals( "parent" ) ) {
                // a parent pack as pom
                collectedDependency.put( "packaging", "pom" );
                Dependency p = new Dependency( collectedDependency,
                        new Resolver(
                                properties ) );
                collectedDependency.clear();
                resultParent.addAll( find( Path.of( p.toPomName() ) ) );
            }

            switch ( qNameT ) {
                case "properties" -> inProperties = false;
                case "dependency" -> inDependency = false;
                case "dependencies" -> inDependencies = false;
                case "build" -> inBuild = false;
                case "parent" -> inParent = false;
            }
            pathStack.pop();
        }

        @Override
        public void endDocument() throws SAXException {

            result.addAll( resultParent );
        }

        private String characters = "";

        public void characters(char[] ch, int start, int length) {
            this.characters = new String( ch, start, length ).trim();
        }

        private void collectedDependencyClear() {
            collectedDependency.remove( "packaging" );
            collectedDependency.remove( "type" );
            collectedDependency.remove( "scope" );
        }
    }

}
