package juja.microservices.users.controller;


import juja.microservices.users.entity.User;
import juja.microservices.users.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@RestController
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
    public ResponseEntity<?> searchUser(@RequestParam Map<String, String> param) {
        List<User> users = userService.searchUser(param);
        logger.info("Search for users by: {} completed", param.toString());
        return ResponseEntity.ok(users);
    }


}