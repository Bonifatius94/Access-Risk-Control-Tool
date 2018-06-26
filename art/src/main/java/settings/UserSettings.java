package settings;

import java.util.Locale;
import java.util.Properties;

public class UserSettings extends Properties {

    private static final String SETTING_LANGUAGE = "app-language";

    public Locale getLanguage() {
        return new Locale(getProperty(SETTING_LANGUAGE));
    }

    public UserSettings setLanguage(Locale locale) {
        setProperty(SETTING_LANGUAGE, locale.toString());
        return this;
    }

    /**
     * This method gets a new instance of user settings with default settings.
     *
     * @return a new instance of user settings with default settings
     */
    public static UserSettings defaultSettings() {

        UserSettings settings = new UserSettings();
        settings.setProperty(SETTING_LANGUAGE, Locale.GERMAN.toString());

        return settings;
    }
}
