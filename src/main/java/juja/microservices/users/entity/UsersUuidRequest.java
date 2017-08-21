package juja.microservices.users.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

/**
 * @author  Vadim Dyachenko
 */

@Getter
public class UsersUuidRequest {

    @NotEmpty
    private List<UUID> uuids;

    @JsonCreator
    public UsersUuidRequest(@JsonProperty("uuids") List<UUID> uuids) {
        this.uuids = uuids;
    }
}