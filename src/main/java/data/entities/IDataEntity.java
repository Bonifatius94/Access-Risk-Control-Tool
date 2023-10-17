package data.entities;

import java.time.ZonedDateTime;

public interface IDataEntity {

    void initCreationFlags(ZonedDateTime createdAt, String createdBy);

    boolean isArchived();

}
