package settings;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import javafx.scene.paint.Color;

public class UserSettings extends Properties {

    private static final String SETTING_LANGUAGE = "app-language";
    private static final String SETTING_PRIMARY_COLOR = "app-primary-color";

    private static final Color DEFAULT_PRIMARY_COLOR = Color.color((double) 76 / 255, (double) 150 / 255, (double) 135 / 255);

    public Locale getLanguage() {
        return new Locale(getProperty(SETTING_LANGUAGE));
    }

    public UserSettings setLanguage(Locale locale) {
        setProperty(SETTING_LANGUAGE, locale.toString());
        return this;
    }

    public Color getPrimaryColor() {
        return getColorFromHexString(getProperty(SETTING_PRIMARY_COLOR));
    }

    public UserSettings setPrimaryColor(Color color) {
        setProperty(SETTING_PRIMARY_COLOR, getColorAsHexString(color));
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
        settings.setProperty(SETTING_PRIMARY_COLOR, getColorAsHexString(DEFAULT_PRIMARY_COLOR));

        return settings;
    }

    /**
     * Returns the available locales for the application.
     *
     * @return the available locales
     */
    public static List<Locale> getAvailableLocales() {
        return Arrays.asList(Locale.GERMAN, Locale.ENGLISH);
    }


    /**
     * Returns the default theme.
     *
     * @return the dark default theme.
     */
    public String getDarkThemeCss() {
        String colorString = getProperty(SETTING_PRIMARY_COLOR);

        return
            "-fx-primary: " + colorString + ";";
    }

    /**
     * Converts a Color into a HexString and return it.
     *
     * @param color the color to convert (as Color)
     * @return the color as HexString
     */
    private static String getColorAsHexString(Color color) {
        return String.format("#%02x%02x%02x",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255));
    }

    /**
     * Converts a HexString into a Color and return it.
     *
     * @param hex the color to convert (as HexString)
     * @return the color as Color
     */
    private static Color getColorFromHexString(String hex) {
        double red = (double) Integer.decode("0x" + hex.substring(1, 3)) / 255;
        double green = (double) Integer.decode("0x" + hex.substring(3, 5)) / 255;
        double blue = (double) Integer.decode("0x" + hex.substring(5, 7)) / 255;

        return Color.color(red, green, blue);
    }
}
