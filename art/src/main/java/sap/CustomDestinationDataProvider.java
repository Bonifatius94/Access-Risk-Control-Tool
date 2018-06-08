package sap;

import com.sap.conn.jco.ext.DataProviderException;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;

import data.entities.SapConfiguration;

import java.util.Properties;


public class CustomDestinationDataProvider implements DestinationDataProvider {
    private DestinationDataEventListener destinationDataEventListener;

    private SapConfiguration config;
    private String username;
    private String password;

    /**
     * Creates a new CustomDestinationDataProvider with the given configuration, username and password.
     *
     * @param config the configuration
     * @param username the username
     * @param password the password
     */
    public CustomDestinationDataProvider(SapConfiguration config, String username, String password) {
        // init this instance with sap config
        this.config = config;
        this.username = username;
        this.password = password;
    }

    @Override
    public Properties getDestinationProperties(String connectionName) throws DataProviderException {
        final Properties settings = new Properties();
        try {
            settings.setProperty(DestinationDataProvider.JCO_ASHOST, config.getServerDestination());
            settings.setProperty(DestinationDataProvider.JCO_SYSNR, config.getSysNr());
            settings.setProperty(DestinationDataProvider.JCO_CLIENT, config.getClient());
            settings.setProperty(DestinationDataProvider.JCO_USER, username);
            settings.setProperty(DestinationDataProvider.JCO_PASSWD, password);
            settings.setProperty(DestinationDataProvider.JCO_LANG, config.getLanguage());
            settings.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, config.getPoolCapacity());
            return settings;
        } catch (Exception e) {
            return new Properties();
        }
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
