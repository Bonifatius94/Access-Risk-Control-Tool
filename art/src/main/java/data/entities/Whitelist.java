package data.entities;

import java.util.List;

public class Whitelist {

    private List<WhitelistEntry> entries;

    public List<WhitelistEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<WhitelistEntry> entries) {
        this.entries = entries;
    }
}
