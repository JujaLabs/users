package juja.microservices.users.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class UsersSlackRequest {
    private List<String> slackNames;

    @JsonCreator
    public UsersSlackRequest(@JsonProperty("slackNames") List<String> slackNames) {
        this.slackNames = slackNames;
    }
}
