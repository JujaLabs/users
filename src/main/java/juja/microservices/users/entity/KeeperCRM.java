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

    private String contactAndId;
    private String description;
    private String from;

    @JsonCreator
    public KeeperCRM(@JsonProperty("c_contact") String nameWithId,
                     @JsonProperty("c_description") String description,
                     @JsonProperty("c_from") String from) {
        this.contactAndId = nameWithId;
        this.description = description;
        this.from = from;
    }

}
