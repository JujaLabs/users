package juja.microservices.users.controller;


import juja.microservices.users.entity.User;
import juja.microservices.users.entity.UserSearchRequest;
import juja.microservices.users.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Validated
public class UserController {

    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/users", params = {"_page", "_limit"}, method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> getAllUsers(@RequestParam("_page") int limit, @RequestParam("_limit") int page) {
        List<User> users = userService.getAllUsers(page, limit);
        logger.info("Successfully completed GET all users");
        return ResponseEntity.ok(users);
    }

    @RequestMapping(value = "/users/search", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> searchUser(@Validated UserSearchRequest request){
        List<User> users = userService.searchUser(request);
        logger.info("Search for users by: {} completed", request.toString());
        return ResponseEntity.ok(users);
    }


    @RequestMapping(value = "/users/{uuid}", method = RequestMethod.GET, produces = "application/json" )
    @ResponseBody
    public ResponseEntity<?> searchUserByUuid(@PathVariable("uuid") String uuid){
        User user = userService.searchUser(uuid);
        logger.info("Search for users by: {} completed", user.toString());
        return ResponseEntity.ok(user);
    }


    @RequestMapping(value = "/users/uuidBySlack", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> searchUuidBySlack(@RequestParam("slack") List<String> slacknames){
        List<UserSearchRequest> requests = new ArrayList<>();
        for (String slackname : slacknames) {
            UserSearchRequest request = new UserSearchRequest();
            request.setSlack(slackname);
            requests.add(request);
        }
        List<User> users = userService.searchUserWithOr(requests);
        logger.info("Search for users by:{} {} completed", "slack", slacknames.toString());
        Map<String, String> response = new HashMap<>();
        for (User user : users) {
            response.put(user.getUuid(), user.getSlack());
        }
        return ResponseEntity.ok(response);
    }
}