package juja.microservices.users.service;

import juja.microservices.users.dao.UserRepository;
import juja.microservices.users.entity.User;
import juja.microservices.users.entity.UserDTO;
import juja.microservices.users.entity.UsersSlackNamesRequest;
import juja.microservices.users.entity.UsersUuidRequest;
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
        List<User> users = repository.findAll();
        if (users.size() == 0) {
            logger.warn("No users found. Received empty list from repository.");
            throw new UserException("No users found by your request!");
        }
        logger.debug("Received user list from repository: {}", users.toString());

        List<UserDTO> result = users.stream()
                .map(this::convertUserToUserDto)
                .collect(Collectors.toList());
        logger.debug("All users converted: {}", result.toString());

        return  result;
    }

    public List<UserDTO> getUsersBySlackNames(UsersSlackNamesRequest request) {
        List<User> users = request.getSlackNames().stream()
                .map(repository::findOneBySlack)
                .collect(Collectors.toList());
        logger.debug("Received response from repository: {}", users.toString());

        List<UserDTO> result =users.stream()
                .map(this::convertUserToUserDto)
                .collect(Collectors.toList());
        logger.debug("All users converted: {}", result.toString());

        return result;
    }

    public List<UserDTO> getUsersByUuids(UsersUuidRequest request) {
        List<User> users = request.getUuids().stream()
                .map(repository::findOneByUuid)
                .collect(Collectors.toList());
        logger.debug("Received response from repository: {}", users.toString());

        List<UserDTO> result = users.stream()
                .map(this::convertUserToUserDto)
                .collect(Collectors.toList());
        logger.debug("All users converted: {}", result.toString());

        return result;
    }

    private UserDTO convertUserToUserDto(User user) {
        return new UserDTO(user.getUuid(), user.getSlack(), user.getSkype(), user.getFullName());
    }
}