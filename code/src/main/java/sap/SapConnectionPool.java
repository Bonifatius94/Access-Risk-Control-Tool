package sap;

import data.entities.SapConfiguration;

import java.util.HashMap;
import java.util.Set;

public class SapConnectionPool {

    public SapConnectionPool(Set<SapConfiguration> configs) {
        initConnections(configs);
    }

    private HashMap<Integer, SapConnector> connections = new HashMap<>();

    private void initConnections(Set<SapConfiguration> configs) {

        // TODO: how can the SapConnectors be initialized without username and password
        //configs.forEach(x -> x.);
    }

}
