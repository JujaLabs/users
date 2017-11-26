package juja.microservices.users.controller;

import juja.microservices.users.entity.UserDTO;
import juja.microservices.users.entity.UsersSlackIdsRequest;
import juja.microservices.users.entity.UsersSlackNamesRequest;
import juja.microservices.users.entity.UsersUuidRequest;
import juja.microservices.users.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Olga Kulykova
 */
@RestController
@Validated
@RequestMapping(value = "/v1/users", produces = "application/json", consumes = "application/json")
public class UserController {

    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDTO> getAllUsers() {
        logger.debug("Received get all users request");

        List<UserDTO> users = userService.getAllUsers();
        logger.info("Successfully completed GET all users. Sent {} records", users.size());

        return users;
    }

    @PostMapping("/usersByUuids")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDTO> getUsersByUuids(@RequestBody UsersUuidRequest request) {
        logger.debug("Received get users name by uuid request. Requested uuid: {}", request.getUuids());

        List<UserDTO> users = userService.getUsersByUuids(request);
        logger.info("Get users name by uuid request processed. Transmitted {} records", users.size());

        return users;
    }

    @PostMapping("/usersBySlackNames")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDTO> getUsersBySlackNames(@RequestBody UsersSlackNamesRequest request) {
        logger.debug("Received get users by slack names request. Requested slack names: {}", request.getSlackNames());

        List<UserDTO> users = userService.getUsersBySlackNames(request);
        logger.info("Get users uuid by slack name completed. Transmitted {} records", users.size());

        return users;
    }

    @PostMapping("/usersBySlackIds")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDTO> getUsersBySlackIds(@RequestBody UsersSlackIdsRequest request) {
        logger.debug("Received get users by slackId ids request. Requested slackId ids: {}", request.getSlackIds());

        List<UserDTO> users = userService.getUsersBySlackIds(request);
        logger.info("Get users uuid by slack id completed. Transmitted {} records", users.size());

        return users;
    }

    /**
     * There is undocumented endpoint for manual update database from CRM
     * Only for first production tests. This endpoint should be removed in the next release.
     */
    @PostMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDTO> updateUsersFromCRM() {
        logger.info("Received request for manual update the database");
        return userService.updateUsersFromCRM();
    }
}