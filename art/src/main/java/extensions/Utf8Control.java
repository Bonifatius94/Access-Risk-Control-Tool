package extensions;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

// snippet source: https://stackoverflow.com/questions/4659929/how-to-use-utf-8-in-resource-properties-with-resourcebundle

public class Utf8Control extends ResourceBundle.Control {

    /**
     * This method gets a ResourceBundle with UTF-8 encoding (standard implementation cannot set the encoding).
     *
     * @param baseName
     *        the base bundle name of the resource bundle, a fully
     *        qualified class name
     * @param locale
     *        the locale for which the resource bundle should be
     *        instantiated
     * @param format
     *        the resource bundle format to be loaded
     * @param loader
     *        the <code>ClassLoader</code> to use to load the bundle
     * @param reload
     *        the flag to indicate bundle reloading; <code>true</code>
     *        if reloading an expired resource bundle,
     *        <code>false</code> otherwise
     * @return the resource bundle instance,
     *        or <code>null</code> if none could be found.
     * @exception NullPointerException
     *        if <code>bundleName</code>, <code>locale</code>,
     *        <code>format</code>, or <code>loader</code> is
     *        <code>null</code>, or if <code>null</code> is returned by
     *        {@link #toBundleName(String, Locale) toBundleName}
     * @exception IllegalArgumentException
     *        if <code>format</code> is unknown, or if the resource
     *        found for the given parameters contains malformed data.
     * @exception ClassCastException
     *        if the loaded class cannot be cast to <code>ResourceBundle</code>
     * @exception IllegalAccessException
     *        if the class or its nullary constructor is not
     *        accessible.
     * @exception InstantiationException
     *        if the instantiation of a class fails for some other
     *        reason.
     * @exception ExceptionInInitializerError
     *        if the initialization provoked by this method fails.
     * @exception SecurityException
     *        If a security manager is present and creation of new
     *        instances is denied. See {@link Class#newInstance()}
     *        for details.
     * @exception IOException
     *        if an error occurred when reading resources using
     *        any I/O operations
     */
    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
        throws IllegalAccessException, InstantiationException, IOException {

        ResourceBundle bundle = null;

        // The below is a copy of the default implementation.
        String bundleName = toBundleName(baseName, locale);
        String resourceName = toResourceName(bundleName, "properties");

        try (InputStream stream = getResourceInputStream(resourceName, loader, reload)) {

            // Only this line is changed to make it to read properties files as UTF-8.
            bundle = new PropertyResourceBundle(new InputStreamReader(stream, "UTF-8"));
        }

        return bundle;
    }

    private InputStream getResourceInputStream(String resourceName, ClassLoader loader, boolean reload) throws IOException {

        InputStream stream = null;

        if (reload) {

            URL url = loader.getResource(resourceName);

            if (url != null) {

                URLConnection connection = url.openConnection();

                if (connection != null) {

                    connection.setUseCaches(false);
                    stream = connection.getInputStream();
                }
            }

        } else {

            stream = loader.getResourceAsStream(resourceName);
        }

        return stream;
    }

}