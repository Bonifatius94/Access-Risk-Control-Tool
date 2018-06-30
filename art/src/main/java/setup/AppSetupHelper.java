package setup;

import extensions.OperatingSystemHelper;

import java.io.InputStream;
import java.nio.file.Paths;

public class AppSetupHelper {

    public static final String SAP_JCO_ROOT_DIR = Paths.get(System.getProperty("user.dir"), "lib").toAbsolutePath().toString();

    /**
     * This methods sets up the app components for first start.
     *
     * @throws Exception caused by errors while reading / writing files or retrieving system settings
     */
    public void setupApp() throws Exception {

        System.out.println(System.getProperty("user.dir"));

        // get host operating system
        OperatingSystemHelper.OperatingSystem operatingSystem = new OperatingSystemHelper().getOperatingSystem();

        // unzip sap jco redistributable archive depending on the host operating system
        String sapJcoRedistResourceZipFile = getSapJcoZipResource(operatingSystem);
        InputStream archiveInputStream = getClass().getClassLoader().getResourceAsStream(sapJcoRedistResourceZipFile);
        new ZipHelper().unzipResouceFile(archiveInputStream, SAP_JCO_ROOT_DIR);
    }

    private String getSapJcoZipResource(OperatingSystemHelper.OperatingSystem operatingSystem) {

        switch (operatingSystem) {
            case Windows: return "redist/sapjco3-NTAMD64-3.0.18.zip";
            case Linux:   return "redist/sapjco3-linuxx86_64-3.0.18.zip";
            case Mac:     return "redist/sapjco3-darwinintel64-3.0.18.zip";
            default: throw new IllegalArgumentException("unsupported operating system detected");
        }
    }

}
