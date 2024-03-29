package codestripper;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import mytinylogger.Logger;

/**
 * Creates zip file and adds content to it with given files names and lines of
 * text.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
class Zipper implements AutoCloseable {

    final Path zipFile;
    FileOutputStream fos;
    ZipOutputStream zos;
    final Logger logger;

    /**
     * Create a zipper ready to receive content.
     *
     * @param zipFileName sic
     */
    public Zipper(Logger logger, Path zipFile) {
        Objects.requireNonNull( logger );
        Objects.requireNonNull( zipFile );
        this.logger = logger;
        this.zipFile = zipFile;
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
            logger.error( () -> ex.getMessage() );
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
        Objects.requireNonNull( source );
        if ( !Files.isRegularFile( source ) ) {
            return;
        }
        if ( entryName.isAbsolute() ) {
            IllegalArgumentException ex
                    = new IllegalArgumentException( "Absolute Path now allowed" );
            ex.printStackTrace();
            throw ex;
        }
        ensureOpen();
        try ( FileInputStream fis = new FileInputStream( source.toFile() ); ) {
            ZipEntry ze = new ZipEntry( entryName.toString() );
            zos.putNextEntry( ze );
            byte[] buffer = new byte[ 8192 ];
            try {
                int bytesRead;
                while ( ( bytesRead = fis.read( buffer, 0, buffer.length ) ) > 0 ) {
                    zos.write( buffer, 0, bytesRead );

                }
                zos.closeEntry();
            } catch ( IOException ex ) {
                logger.error( () -> ex.getMessage() );
                throw new RuntimeException( ex );
            }
        } catch ( IOException ex ) {
            logger.error( () -> ex.getMessage() );
            throw new RuntimeException( ex );
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
            Files.createDirectories( zipFile.getParent() );
            fos = new FileOutputStream( zipFile.toFile() );
            zos = new ZipOutputStream( fos );
        } catch ( IOException ex ) {
            logger.error( () -> ex.getMessage() );
            throw new RuntimeException( ex );
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
                zos.finish();
                zos.close();
            }
        } finally {
            if ( null != fos ) {
                fos.close();
            }
        }
    }

}
