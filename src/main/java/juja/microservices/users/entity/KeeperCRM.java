package juja.microservices.users.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Vitalii Viazovoi
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KeeperCRM {

    private String contact;  // Consists of name and id (example: "John Smitn_123")
    private String description;
    private String from;

    @JsonCreator
    public KeeperCRM(@JsonProperty("c_contact") String contact,
                     @JsonProperty("c_description") String description,
                     @JsonProperty("c_from") String from) {
        this.contact = contact;
        this.description = description;
        this.from = from;
    }

}
