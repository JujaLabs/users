package juja.microservices.users.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @author Denis Tantsev (dtantsev@gmail.com)
 * @author Olga Kulykova
 */

@Data
@AllArgsConstructor
public class User {
    @JsonProperty("c_uuid")
    private String uuid;
    @JsonProperty("firstName")
    private String firstName;
    @JsonProperty("lastName")
    private String lastName;
    @Getter(AccessLevel.NONE)
    @JsonProperty("email")
    private String email;
    @Getter(AccessLevel.NONE)
    @JsonProperty("c_gmail")
    private String gmail;
    @JsonProperty("c_slack")
    private String slack;
    @JsonProperty("skype")
    private String skype;


    public String getFullName() {
        if (this.lastName == null || this.lastName.contains("@") || "".equals(this.lastName)) {
            return this.firstName;
        }
        return this.lastName + " " + this.firstName;
    }

    public String getEmail() {
        if (this.gmail == null) {
            return this.email;
        }
        return this.gmail;
    }
}
