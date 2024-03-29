package sap;

import com.sap.conn.jco.JCoException;
import data.entities.Configuration;
import data.entities.CriticalAccessQuery;

public interface ISapConnector {

    /**
     * This method pings the sap server specified in the sap server config.
     * @return a boolean value that indicates whether the ping was successful
     * @throws JCoException if server ping fails, error is used by Test ping
     */
    boolean canPingServer() throws JCoException;

    /**
     * This method runs a SAP analysis for the given config.
     *
     * @param config the configuration used for the query (contains a whitelist and a set of access patterns)
     * @return the results of the query (including all configuration settings used for the query)
     * @throws Exception caused by network errors during sap query
     */
    CriticalAccessQuery runAnalysis(Configuration config) throws Exception;

}
