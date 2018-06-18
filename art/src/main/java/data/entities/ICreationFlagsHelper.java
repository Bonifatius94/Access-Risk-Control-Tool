package data.entities;

import java.time.ZonedDateTime;

public interface ICreationFlagsHelper {

    void initCreationFlags(ZonedDateTime createdAt, String createdBy);

}
