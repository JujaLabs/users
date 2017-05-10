package juja.microservices.users.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author Denis Tantsev (dtantsev@gmail.com)
 * @author Olga Kulykova
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    @Size(max = 36, min = 36, message = "UUID must consist total of 36 characters with hyphens")
    @Pattern(regexp="[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}", message="UUID must fit pattern 8-4-4-4-12")
    private String uuid;

    @Pattern(regexp="^(?=\\s*\\S).*$")
    private String slack;

    private String skype;
    private String name;

    @JsonCreator
    public UserDTO(@JsonProperty("uuid") String uuid,
                @JsonProperty("slack") String slack,
                @JsonProperty("skype") String skype,
                @JsonProperty("name") String name) {
        this.uuid = uuid;
        this.slack = slack;
        this.skype = skype;
        this.name = name;
    }
}
