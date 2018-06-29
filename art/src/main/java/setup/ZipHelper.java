package setup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

// snippet source: http://www.baeldung.com/java-compress-and-uncompress

public class ZipHelper {

    /**
     * This method unzips a *.zip file from resources and writes it to the given output file.
     *
     * @param inputStream the input stream of the zip archive
     * @param outputDirectoryPath the output directory path
     * @throws Exception caused by errors while reading / writing files etc.
     */
    public void unzipResouceFile(InputStream inputStream, String outputDirectoryPath) throws Exception {

        // get output directory handle
        File outputDir = new File(outputDirectoryPath);

        // create output directory if it does not exist
        if (!outputDir.exists() || !outputDir.isDirectory()) {
            outputDir.mkdirs();
        }

        // load zip resource as stream
        try (ZipInputStream zipStream = new ZipInputStream(inputStream)) {

            ZipEntry zipEntry;

            // got through all archive entries on zip file
            while ((zipEntry = zipStream.getNextEntry()) != null) {

                // get output file handle
                Path entryPath = Paths.get(zipEntry.getName());
                String fileName = entryPath.subpath(1, entryPath.getNameCount()).toString();
                File outputFile = new File(Paths.get(outputDirectoryPath, fileName).toString());

                if (zipEntry.isDirectory()) {

                    outputFile.mkdirs();

                } else {

                    // copy archived file from zip to destination file
                    try (FileOutputStream fos = new FileOutputStream(outputFile)) {

                        int len;
                        byte[] buffer = new byte[1024];

                        while ((len = zipStream.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
            }

            // close last entry
            zipStream.closeEntry();
        }
    }
}
