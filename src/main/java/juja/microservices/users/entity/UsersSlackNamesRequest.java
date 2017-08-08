package juja.microservices.users.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class UsersSlackNamesRequest {
    private List<String> slackNames;

    @JsonCreator
    public UsersSlackNamesRequest(@JsonProperty("slackNames") List<String> slackNames) {
        this.slackNames = slackNames;
    }
}
