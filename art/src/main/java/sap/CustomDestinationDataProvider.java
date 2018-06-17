package sap;

import com.sap.conn.jco.ext.DataProviderException;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;

import data.entities.SapConfiguration;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Properties;

// source: https://archive.sap.com/discussions/thread/1887028

public class CustomDestinationDataProvider implements DestinationDataProvider {

    // =============================
    //           singleton
    // =============================

    /**
     * This constructor is flagged private to enforce usage as singleton.
     * It also registers this instance as data provider for JCo connections.
     */
    private CustomDestinationDataProvider() {
        com.sap.conn.jco.ext.Environment.registerDestinationDataProvider(this);
    }

    private static CustomDestinationDataProvider instance = null;

    public static CustomDestinationDataProvider getInstance() {
        return instance = (instance != null ? instance : new CustomDestinationDataProvider());
    }

    // =============================
    //            members
    // =============================

    private DestinationDataEventListener destinationDataEventListener;
    private HashMap<String, Properties> settingsOfSessions = new HashMap<>();

    // =============================
    //     register / unregister
    // =============================

    /**
     * Creates a new connection session with the given configuration, username and password.
     *
     * @param config the configuration
     * @param username the username
     * @param password the password
     * @return the destination name of the new connection
     */
    public String openSession(SapConfiguration config, String username, String password) {

        // generate session key
        String sessionKey = config.getServerDestination() + "_" + ZonedDateTime.now(ZoneOffset.UTC);

        // create settings instance with given data
        final Properties settings = new Properties();
        settings.setProperty(DestinationDataProvider.JCO_DEST, sessionKey);
        settings.setProperty(DestinationDataProvider.JCO_ASHOST, config.getServerDestination());
        settings.setProperty(DestinationDataProvider.JCO_SYSNR, config.getSysNr());
        settings.setProperty(DestinationDataProvider.JCO_CLIENT, config.getClient());
        settings.setProperty(DestinationDataProvider.JCO_LANG, config.getLanguage());
        settings.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, config.getPoolCapacity());
        settings.setProperty(DestinationDataProvider.JCO_USER, username);
        settings.setProperty(DestinationDataProvider.JCO_PASSWD, password);

        // add session
        settingsOfSessions.put(sessionKey, settings);

        return sessionKey;
    }

    /**
     * This method removes a session.
     *
     * @param sessionKey the key of the session to remove
     */
    public void closeSession(String sessionKey) {

        if (!settingsOfSessions.containsKey(sessionKey)) {
            throw new IllegalArgumentException("Unknown session key: " + sessionKey);
        }

        settingsOfSessions.remove(sessionKey);
    }

    // =============================
    //          overrides
    // =============================

    /**
     * This method gets the connection settings of the given session (only used by JCo internally when connecting to the given server destination).
     *
     * @param sessionKey the session key of the session
     * @return the settings of the session
     * @throws DataProviderException caused by operational errors by JCo
     */
    @Override
    public Properties getDestinationProperties(String sessionKey) throws DataProviderException {

        if (!settingsOfSessions.containsKey(sessionKey)) {
            throw new IllegalArgumentException("Unknown session key: " + sessionKey);
        }

        return settingsOfSessions.get(sessionKey);
    }

    @Override
    public void setDestinationDataEventListener(DestinationDataEventListener destinationDataEventListener) {
        this.destinationDataEventListener = destinationDataEventListener;
    }

    @Override
    public boolean supportsEvents() {
        return true;
    }

}
