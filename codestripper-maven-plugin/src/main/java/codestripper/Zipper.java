/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codestripper;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Creates zip file and adds content to it with given files names and lines of
 * text.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
class Zipper implements AutoCloseable {

    final String zipFileName;
    FileOutputStream fos;
    ZipOutputStream zos;

    /**
     * Create a zipper ready to receive content.
     *
     * @param zipFileName sic
     */
    public Zipper(String zipFileName) {
        this.zipFileName = zipFileName;
    }

    /**
     * Add a virtual file to the zip file. The file is the actual path as
     * appears in the zip file. The lines are the contents of the file. The file
     * needs not exists in the actual file system, but will be present inside
     * the zip file.
     *
     * @param pathInzip virtual file to add
     * @param lines to add
     * @throws IOException
     */
    public void add(Path pathInzip, List<String> lines) throws IOException {
        ensureOpen();
        try {
            ZipEntry ze = new ZipEntry( pathInzip.toString() );
            zos.putNextEntry( ze );
            for ( String line : lines ) {
                byte[] bytes = ( line + lineSep ).getBytes();
                zos.write( bytes, 0, bytes.length );
            }
            zos.closeEntry();
        } catch ( IOException ex ) {
            Logger.getLogger( CodeStripper.class.getName() )
                    .log( Level.SEVERE, null, ex );
        }
    }

    /**
     * Add a file source verbatim (as in binary) to this zip. This is used to
     * ship non strip-able files to this zip.
     *
     * @param entryName the name of the zip entry
     * @param source the source of the data.
     */
    public void add(Path entryName, Path source) {
        if ( !Files.isRegularFile( source ) ) {
            return;
        }
        ensureOpen();
        try ( FileInputStream fis = new FileInputStream( source.toFile() ); ) {
            ZipEntry ze = new ZipEntry( entryName.toString().toString() );
            zos.putNextEntry( ze );
            byte[] buffer = new byte[ 8192 ];
            try {
                int bytesRead;
                while ( ( bytesRead = fis.read( buffer, 0, buffer.length ) ) > 0 ) {
                    zos.write( buffer, 0, bytesRead );

                }
                zos.closeEntry();
            } catch ( IOException ex ) {
                Logger.getLogger( Zipper.class.getName() ).log( Level.SEVERE,
                        null,
                        ex );
            }
        } catch ( IOException ex ) {
            Logger.getLogger( Zipper.class.getName() ).log( Level.SEVERE, null,
                    ex );
        }
    }

    static String lineSep = System.getProperty( "line.separator" );

    /**
     * If the zip file is not yet open, open it.
     */
    private void ensureOpen() {
        try {
            if ( zos != null ) {
                return;
            }
            Files.createDirectories( Path.of( zipFileName ).getParent() );
            fos = new FileOutputStream( zipFileName );
            zos = new ZipOutputStream( fos );
        } catch ( IOException ex ) {
            Logger.getLogger( CodeStripper.class.getName() )
                    .log( Level.SEVERE, null, ex );
        }
    }

    /**
     * Close.
     *
     * @throws Exception sic
     */
    @Override
    public void close() throws Exception {
        try {
            if ( null != zos ) {
                zos.close();
            }
        } finally {
            if ( null != fos ) {
                fos.close();
            }
        }
    }

}
