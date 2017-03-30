package juja.microservices.users.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Denis Tantsev (dtantsev@gmail.com)
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    private String uuid;
    private String firstName;
    private String lastName;
    private String email;
    private String gmail;
    private String slack;
    private String skype;
    private String linkedin;
    private String facebook;
    private String twitter;

    @JsonCreator
    public User(@JsonProperty("c_uuid") String uuid,
                @JsonProperty("firstName") String firstName,
                @JsonProperty("lastName") String lastName,
                @JsonProperty("email") String email,
                @JsonProperty("c_gmail") String gmail,
                @JsonProperty("c_slack") String slack,
                @JsonProperty("skype") String skype,
                @JsonProperty("linkedin") String linkedin,
                @JsonProperty("facebook") String facebook,
                @JsonProperty("twitter") String twitter) {
        this.uuid = uuid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.gmail = gmail;
        this.slack = slack;
        this.skype = skype;
        this.linkedin = linkedin;
        this.facebook = facebook;
        this.twitter = twitter;
    }

}
