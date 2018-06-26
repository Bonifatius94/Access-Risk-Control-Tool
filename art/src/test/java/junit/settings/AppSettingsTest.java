package junit.settings;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import settings.UserSettings;
import settings.UserSettingsHelper;

import java.io.File;
import java.util.Locale;

public class AppSettingsTest {

    @BeforeEach
    public void cleanupSettings() {

        // delete settings file if it exists
        File userSettings = new File("user.settings");

        if (userSettings.exists()) {
            new File("user.settings").delete();
        }
    }

    @Test
    public void testUserSettings() {

        boolean ret = false;

        try {

            // create default settings file
            UserSettings settings = new UserSettingsHelper().loadUserSettings();
            UserSettings defaults = UserSettings.defaultSettings();

            // check if the default settings match
            ret = doSettingsMatch(settings, defaults);

            // change settings and store them
            settings.setLanguage(Locale.ENGLISH);
            new UserSettingsHelper().storeUserSettings(settings);

            // get settings and check if storing worked
            UserSettings settingsFromFile = new UserSettingsHelper().loadUserSettings();

            // check if the changes were written to the settings file
            ret = ret && doSettingsMatch(settings, settingsFromFile);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assert(ret);
    }

    private boolean doSettingsMatch(UserSettings s1, UserSettings s2) {
        return s1.entrySet().stream().allMatch(x -> s2.containsKey(x.getKey()) && x.getValue().equals(s2.get(x.getKey())));
    }

}
