package sap;

public class SapConfiguration {

    private String serverDestination;
    private String sysNr;
    private String client;
    private String user;
    private String password;
    private String language;
    private String poolCapacity;

    public String getServerDestination() {
        return serverDestination;
    }

    public void setServerDestination(String serverDestination) {
        this.serverDestination = serverDestination;
    }

    public String getSysNr() {
        return sysNr;
    }

    public void setSysNr(String sysNr) {
        this.sysNr = sysNr;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPoolCapacity() {
        return poolCapacity;
    }

    public void setPoolCapacity(String poolCapacity) {
        this.poolCapacity = poolCapacity;
    }

    // TODO: remove explicit empty constructor
    public SapConfiguration() {
    }
}