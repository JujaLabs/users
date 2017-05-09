package juja.microservices.users.service;

import juja.microservices.users.dao.UserRepository;
import juja.microservices.users.entity.User;
import juja.microservices.users.entity.UserDTO;
import juja.microservices.users.entity.UsersSlackRequest;
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
        List<User> users = repository.getAllUsers();
        if (users.size() == 0) {
            throw new UserException("No users found by your request!");
        }
        return users.stream()
                .map(this::convertGetAllUsersDto)
                .collect(Collectors.toList());
    }

    private UserDTO convertGetAllUsersDto(User user) {
        return new UserDTO(user.getUuid(), user.getSlack(), user.getSkype(), user.getFullName());
    }

    public List<UserDTO> getUsersUuidBySlack(UsersSlackRequest request) {
        List<User> users = request.getSlackNames().stream()
                .map(repository::getUserBySlack)
                .collect(Collectors.toList());
        logger.debug("List of users: ", users.toString());
        return users.stream()
                .map(this::convertGetUuidBySlackDto)
                .collect(Collectors.toList());
    }

    private UserDTO convertGetUuidBySlackDto(User user) {
        return new UserDTO(user.getUuid(), user.getSlack(), null, null);
    }
}