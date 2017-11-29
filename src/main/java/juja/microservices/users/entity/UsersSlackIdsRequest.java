package juja.microservices.users.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

/**
 * author Vadim Dyachenko
 */
@Getter
public class UsersSlackIdsRequest {

    private List<String> slackIds;

    @JsonCreator
    public UsersSlackIdsRequest(@JsonProperty("slackIds") List<String> slackIds) {
        this.slackIds = slackIds;
    }
}
