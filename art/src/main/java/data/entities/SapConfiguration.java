package data.entities;

import java.time.ZonedDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SapConfigurations")
public class SapConfiguration implements ICreationFlagsHelper {

    private Integer id;
    private String serverDestination;
    private String sysNr;
    private String client;
    private String language;
    private String poolCapacity;

    private boolean isArchived;
    private ZonedDateTime createdAt;
    private String createdBy;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    // =============================
    //          overrides
    // =============================

    @Override
    public void initCreationFlags(ZonedDateTime createdAt, String createdBy) {

        setCreatedAt(createdAt);
        setCreatedBy(createdBy);
    }

    @Override
    public String toString() {
        return "Server Destination = " + getServerDestination() + ", SysNr = " + getSysNr() + ", Client = " + getClient()
            + ", Language = " + getLanguage() + ", Pool Capacity = " + getPoolCapacity();
    }

    /**
     * This is a custom implementation of equals method that checks for data equality.
     *
     * @param other the object to compare with
     * @return whether they are equal
     */
    @Override
    public boolean equals(Object other) {

        /*boolean ret = (other == this);

        if (other instanceof SapConfiguration) {

            SapConfiguration cmp = (SapConfiguration) other;

            ret = (this.serverDestination.equals(cmp.getServerDestination())
                && this.sysNr.equals(cmp.getSysNr())
                && this.client.equals(cmp.getClient())
                && this.language.equals(cmp.getLanguage())
                && this.poolCapacity.equals(cmp.getPoolCapacity())
                && this.id == null || (
                    this.isArchived == cmp.isArchived()
                    && this.createdAt.equals(cmp.getCreatedAt())
                    && this.createdBy.equals(cmp.getCreatedBy())
                ));
        }

        return ret;*/

        return super.equals(other);
    }

    @Override
    public int hashCode() {
        //return (id != null) ? id : 0;
        return super.hashCode();
    }

}