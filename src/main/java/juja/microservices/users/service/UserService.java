package juja.microservices.users.service;

import juja.microservices.users.dao.crm.repository.CRMRepository;
import juja.microservices.users.dao.users.repository.UserRepository;
import juja.microservices.users.dao.users.domain.User;
import juja.microservices.users.dao.crm.domain.UserCRM;
import juja.microservices.users.entity.UserDTO;
import juja.microservices.users.entity.UsersSlackNamesRequest;
import juja.microservices.users.entity.UsersUuidRequest;
import juja.microservices.users.exceptions.UserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Denis Tantsev (dtantsev@gmail.com)
 * @author Olga Kulykova
 */
@Service
public class UserService {

    private final UserRepository repository;
    private final CRMRepository crmRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    public UserService(UserRepository userRepository, CRMRepository crmRepository) {
        this.repository = userRepository;
        this.crmRepository = crmRepository;
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

    public List<UserDTO> updateUsersFromCRM() {
        List<UserCRM> crmUsers = crmRepository.findAllByLastUpdatedGreaterThan(1504237985L);

        List<User> users = crmUsers.stream()
                .map(this::convertUserCRMtoUser)
                .collect(Collectors.toList());
        List<User> savedUser = repository.save(users);
        repository.flush();

        List<UserDTO> result = savedUser.stream()
                .map(this::convertUserToUserDto)
                .collect(Collectors.toList());

        return  result;
    }

    private User convertUserCRMtoUser(UserCRM userCRM) {
        return new User(UUID.fromString(userCRM.getUuid()), userCRM.getFirstName(), userCRM.getLastName(),
                userCRM.getEmail(), userCRM.getGmail(), userCRM.getSlack(), userCRM.getSkype());
    }
}