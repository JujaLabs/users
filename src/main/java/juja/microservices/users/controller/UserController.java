package juja.microservices.users.controller;

import juja.microservices.users.entity.Keeper;
import juja.microservices.users.entity.UserDTO;
import juja.microservices.users.entity.UsersSlackRequest;
import juja.microservices.users.entity.UsersUuidRequest;
import juja.microservices.users.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

/**
 *@author Olga Kulykova
 */
@RestController
@Validated
@RequestMapping(value = "/users", produces = "application/json", consumes = "application/json")
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
        //this UserDTO should have fields: uuid, slack, skype, name
        logger.info("Received get all users request");

        List<UserDTO> users = userService.getAllUsers();

        logger.info("Successfully completed GET all users. Sent {} records", users.size());
        logger.debug("Sent users: [{}]", users.toString());
        return users;
    }

    @PostMapping("/nameByUuid")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDTO> getUsersNameByUuid(@RequestBody UsersUuidRequest request){
        //this UserDTO should have fields: uuid, name
        logger.info("Received get users name by uuid request.");
        logger.debug("Requested uuid: {}", request.getUuid());

        List<UserDTO> users = userService.getUsersNameByUuid(request);

        logger.info("Get users name by uuid request processed. Transmitted {} records", users.size());
        logger.debug("Sent users: [{}]", users.toString());
        return users;
    }


    @PostMapping("/uuidBySlack")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDTO> getUsersUuidBySlack(@RequestBody UsersSlackRequest request){
        //this UserDTO should have fields: uuid, slack
        logger.info("Received get users uuid by slack name request.");
        logger.debug("Requested slack names: {}", request.getSlackNames());

        List<UserDTO> users = userService.getUsersUuidBySlack(request);

        logger.info("Get users uuid by slack name completed. Transmitted {} records", users.size());
        logger.debug("Sent users: [{}]", users.toString());
        return users;
    }

    @GetMapping("/activeKeepers")
    @ResponseStatus(HttpStatus.OK)
    public List<Keeper> getActiveKeepers() {
        logger.info("Received get active keeper request");

        List<Keeper> keepers = userService.getActiveKeepers();

        logger.info("Successfully completed GET all active keepers. Transmitted {} records", keepers.size());
        logger.debug("Sent keepers: [{}]", keepers.toString());
        return keepers;
    }
}