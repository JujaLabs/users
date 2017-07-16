package juja.microservices.users.service;

import juja.microservices.users.dao.UserRepository;
import juja.microservices.users.entity.*;
import juja.microservices.users.exceptions.UserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Denis Tantsev (dtantsev@gmail.com)
 * @author Olga Kulykova
 */

@Service
public class UserService {

    private final UserRepository repository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    public UserService(UserRepository userRepository) {
        this.repository = userRepository;
    }

    public List<UserDTO> getAllUsers() {
        logger.debug("Send get all users request to repository");
        List<User> users = repository.getAllUsers();
        if (users.size() == 0) {
            logger.warn("No users found. Received empty list from repository.");
            throw new UserException("No users found by your request!");
        }
        logger.debug("Received user list from repository: {}", users.toString());

        logger.debug("Converting received user list to userDTO list");
        List<UserDTO> result = users.stream()
                .map(this::convertGetAllUsersDto)
                .collect(Collectors.toList());
        logger.debug("All users converted: {}", result.toString());

        logger.info("Founded {} users", result.size());
        return  result;
    }

    private UserDTO convertGetAllUsersDto(User user) {
        return new UserDTO(user.getUuid(), user.getSlack(), user.getSkype(), user.getFullName());
    }

    public List<UserDTO> getUsersUuidBySlack(UsersSlackRequest request) {
        logger.debug("Sending get uuid by slack name request to repository");
        logger.debug("Sent slack names: {}", request.getSlackNames());
        List<User> users = request.getSlackNames().stream()
                .map(repository::getUserBySlack)
                .collect(Collectors.toList());
        logger.debug("Received response from repository: {}", users.toString());

        logger.debug("Converting received user list to userDTO list");
        List<UserDTO> result;
        try {
            result = users.stream()
                    .map(this::convertGetUuidBySlackDto)
                    .collect(Collectors.toList());
        } catch (NullPointerException e) {
            logger.warn("Users " + request.getSlackNames() + " weren't found. Received empty list from repository.");
            throw new UserException("Users " + request.getSlackNames() + " weren't found by your request!");
        }
        logger.debug("All users converted: {}", result.toString());

        logger.info("Founded {} users", result.size());
        return result;
    }

    private UserDTO convertGetUuidBySlackDto(User user) {
        return new UserDTO(user.getUuid(), user.getSlack(), null, null);
    }

    public List<UserDTO> getUsersNameByUuid(UsersUuidRequest request) {
        logger.debug("Sending get user bu uuid request to repository");
        logger.debug("Sent uuid: {}", request.getUuid());
        List<User> users = request.getUuid().stream()
                .map(repository::getUserByUuid)
                .collect(Collectors.toList());
        logger.debug("Received response from repository: {}", users.toString());

        logger.debug("Converting received user list to userDTO list");
        List<UserDTO> result = users.stream()
                .map(this::convertGetNameByUuid)
                .collect(Collectors.toList());
        logger.debug("All users converted: {}", result.toString());

        logger.info("Founded {} users", result.size());
        return result;
    }

    private UserDTO convertGetNameByUuid(User user) {
        return new UserDTO(user.getUuid(), null,null, user.getFullName());
    }

    public List<Keeper> getActiveKeepers() {
        logger.debug("Send get active keepers request to repository");
        List<Keeper> result = repository.getActiveKeepers();

        if (result.size() == 0) {
            String message = "No any active keepers founded";
            logger.warn(message);
            throw new UserException(message);
        }
        logger.debug("Received keepers list from repository: {}", result.toString());
        logger.info("Founded {} keepers", result.size());
        return result;
    }
}