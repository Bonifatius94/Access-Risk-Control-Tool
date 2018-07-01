package extensions;

import org.apache.commons.lang3.SystemUtils;

public class OperatingSystemHelper {

    public enum OperatingSystem {
        Windows,
        Linux,
        Mac
    }

    /**
     * This method gets the current host operating system.
     *
     * @return the current host operating system
     */
    public OperatingSystem getOperatingSystem() {

        OperatingSystem operatingSystem;

        if (SystemUtils.IS_OS_WINDOWS) {
            operatingSystem = OperatingSystem.Windows;
        } else if (SystemUtils.IS_OS_LINUX) {
            operatingSystem = OperatingSystem.Linux;
        } else if (SystemUtils.IS_OS_MAC) {
            operatingSystem = OperatingSystem.Mac;
        } else {
            throw new IllegalArgumentException("unknown operating system detected that is not supported by this app");
        }


        return operatingSystem;
    }

}
