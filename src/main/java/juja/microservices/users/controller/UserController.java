package juja.microservices.users.controller;

import juja.microservices.users.entity.UserDTO;
import juja.microservices.users.entity.UsersSlackRequest;
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

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<UserDTO> getAllUsers() {
        //this UserDTO should have fields: uuid, slack, skype, name
        List<UserDTO> users = userService.getAllUsers();
        logger.info("Successfully completed GET all users. List of users: ", users.toString());
        return users;
    }

    //todo Uncomment and implement all chain of this endpoint
    /*@RequestMapping(value = "/nameByUuid", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<UserDTO> getUsersNameByUuid(@RequestBody UsersUuidRequest request){
        //this UserDTO should have fields: uuid, name
        List<UserDTO> users = userService.getUsersNameByUuid(request);
        logger.info("Get users name by uuid completed: ", users.toString());
        return users;
    }*/


    @RequestMapping(value = "/uuidBySlack", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<UserDTO> getUsersUuidBySlack(@RequestBody UsersSlackRequest request){
        //this UserDTO should have fields: uuid, slack
        List<UserDTO> users = userService.getUsersUuidBySlack(request);
        logger.info("Get users uuid by slack completed: ", users.toString());
        return users;
    }
}