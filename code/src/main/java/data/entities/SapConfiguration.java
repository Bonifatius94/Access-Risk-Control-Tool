package data.entities;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SapConfigurations")
public class SapConfiguration implements IDataEntity {

    // =============================
    //         constructors
    // =============================

    public SapConfiguration() {

    }

    /**
     * This constructor sets all members of the new instance.
     */
    public SapConfiguration(String serverDestination, String description, String sysNr, String client, String language, String poolCapacity) {

        setServerDestination(serverDestination);
        setDescription(description);
        setSysNr(sysNr);
        setClient(client);
        setLanguage(language);
        setPoolCapacity(poolCapacity);
    }

    /**
     * This constructor clones the given instance.
     *
     * @param original the instance to be cloned
     */
    public SapConfiguration(SapConfiguration original) {

        setServerDestination(original.getServerDestination());
        setDescription(original.getDescription());
        setSysNr(original.getSysNr());
        setClient(original.getClient());
        setLanguage(original.getLanguage());
        setPoolCapacity(original.getPoolCapacity());
    }

    // =============================
    //           members
    // =============================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String serverDestination;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String sysNr;

    @Column(nullable = false)
    private String client;

    @Column(nullable = false)
    private String language;

    @Column(nullable = false)
    private String poolCapacity;

    @Column(nullable = false)
    private boolean isArchived;

    @Column(nullable = false)
    private ZonedDateTime createdAt;

    @Column(nullable = false)
    private String createdBy;

    // =============================
    //      getters / setters
    // =============================

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

}