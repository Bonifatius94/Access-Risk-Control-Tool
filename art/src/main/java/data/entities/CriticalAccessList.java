package data.entities;

import java.util.ArrayList;
import java.util.List;

public class CriticalAccessList {

    private List<CriticalAccessEntry> entries;

    public CriticalAccessList() {
        this.entries = new ArrayList<>();
    }

    public CriticalAccessList(List<CriticalAccessEntry> list) {
        this.entries = list;
    }

    public List<CriticalAccessEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<CriticalAccessEntry> entries) {
        this.entries = entries;
    }
}
