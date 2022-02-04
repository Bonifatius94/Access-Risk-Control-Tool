package settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;

public class UserSettingsHelper {

    /**
     * This method gets the current user settings from a text file.
     *
     * @return the current user settings
     * @throws Exception caused by file access issues etc.
     */
    public UserSettings loadUserSettings() throws Exception {

        UserSettings settings;

        // init settings file handle
        File file = new File(getUserSettingsFilePath());

        if (file.exists()) {

            // load settings from settings file
            try (InputStream stream = new FileInputStream(file)) {

                try (InputStreamReader reader = new InputStreamReader(stream, "UTF-8")) {

                    settings = new UserSettings();
                    settings.load(reader);
                }
            }

        } else {

            // load default settings
            settings = UserSettings.defaultSettings();
            storeUserSettings(settings);
        }

        return settings;
    }

    /**
     * This method stores the given user settings to a text file.
     *
     * @param settings the settings to be stored
     * @throws Exception caused by file access issues etc.
     */
    public void storeUserSettings(UserSettings settings) throws Exception {

        // init settings file handle
        File file = new File(getUserSettingsFilePath());

        // save settings to settings file
        try (OutputStream stream = new FileOutputStream(file)) {

            try (OutputStreamWriter writer = new OutputStreamWriter(stream, "UTF-8")) {

                settings.store(writer, null);
            }
        }
    }

    private static String getUserSettingsFilePath() {

        // get settings file path (relative to execution directory)
        String currentExeFolder = System.getProperty("user.dir");
        return Paths.get(currentExeFolder, "user.settings").toAbsolutePath().toString();
    }

}
