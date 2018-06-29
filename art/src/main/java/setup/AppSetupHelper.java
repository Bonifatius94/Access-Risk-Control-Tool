package setup;

import extensions.OperatingSystemHelper;

import java.nio.file.Files;
import java.nio.file.Paths;

public class AppSetupHelper {

    public static final String SAP_JCO_ROOT_DIR = Paths.get(System.getProperty("user.dir"), "lib").toAbsolutePath().toString();

    /**
     * This methods sets up the app components for first start.
     *
     * @throws Exception caused by errors while reading / writing files or retrieving system settings
     */
    public void setupApp() throws Exception {

        // get host operating system
        OperatingSystemHelper.OperatingSystem operatingSystem = new OperatingSystemHelper().getOperatingSystem();

        // unzip sap jco redistributable archive depending on the host operating system
        String sapJcoRedistResourceZipFile = getSapJcoZipResource(operatingSystem);
        new ZipHelper().unzipResouceFile(sapJcoRedistResourceZipFile, SAP_JCO_ROOT_DIR);

        // rename sapjco3.jar file to sapjco-3.jar
        //Files.delete(Paths.get(SAP_JCO_ROOT_DIR, "sapjco-3.jar"));
        //Files.move(Paths.get(SAP_JCO_ROOT_DIR, "sapjco3.jar"), Paths.get(SAP_JCO_ROOT_DIR, "sapjco-3.jar"));
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
