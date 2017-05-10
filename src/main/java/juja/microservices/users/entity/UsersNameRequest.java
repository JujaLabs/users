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
public class UsersNameRequest {

    @NotEmpty
    private List<String> uuid;

    @JsonCreator
    public UsersNameRequest(@JsonProperty("uuid") List<String> uuid) {
        this.uuid = uuid;
    }
}