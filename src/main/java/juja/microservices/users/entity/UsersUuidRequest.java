package juja.microservices.users.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * @author  Vadim Dyachenko
 */

@Getter
public class UsersUuidRequest {

    @NotEmpty
    private List<String> uuids;

    @JsonCreator
    public UsersUuidRequest(@JsonProperty("uuids") List<String> uuids) {
        this.uuids = uuids;
    }
}