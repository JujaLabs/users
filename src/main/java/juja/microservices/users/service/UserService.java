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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Denis Tantsev (dtantsev@gmail.com)
 * @author Olga Kulykova
 * @author Vadim Dyachenko
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

        List<UserDTO> result = getConvertedResult(users);
        logger.debug("All users converted: {}", result.toString());

        return result;
    }

    private List<UserDTO> getConvertedResult(List<User> users) {
        return users.stream()
                .map(this::convertUserToUserDto)
                .collect(Collectors.toList());
    }

    private UserDTO convertUserToUserDto(User user) {
        return new UserDTO(
                user.getUuid(),
                user.getSlack(),
                user.getSkype(),
                user.getFullName()
        );
    }

    public List<UserDTO> getUsersBySlackNames(UsersSlackNamesRequest request) {
        List<User> users = request.getSlackNames().stream()
                .map(repository::findOneBySlack)
                .collect(Collectors.toList());
        logger.debug("Received response from repository: {}", users.toString());

        List<UserDTO> result = getConvertedResult(users);
        logger.debug("All users converted: {}", result.toString());

        return result;
    }

    public List<UserDTO> getUsersByUuids(UsersUuidRequest request) {
        List<User> users = request.getUuids().stream()
                .map(repository::findOneByUuid)
                .collect(Collectors.toList());
        logger.debug("Received response from repository: {}", users.toString());

        List<UserDTO> result = getConvertedResult(users);
        logger.debug("All users converted: {}", result.toString());

        return result;
    }

    @Scheduled(cron = "${cron.expression}")
    public void scheduleUpdateUsers() {
        updateUsersFromCRM();
    }

    public List<UserDTO> updateUsersFromCRM() {
        Long lastUpdate = repository.findMaxLastUpdate();
        if (lastUpdate == null) lastUpdate = 0L;

        logger.info("Starting update users database. Last update was at {}",
                LocalDateTime.ofEpochSecond(lastUpdate, 0, OffsetDateTime.now().getOffset()));

        return getConvertedResult(updateUsersDatabase(getUpdatedUsersFromCRM(lastUpdate)));
    }

    private List<User> updateUsersDatabase(List<User> users) {
        List<User> result = new ArrayList<>();
        if (users.size() != 0) {
            result = repository.save(users);
            repository.flush();
            logger.info("{} records updated", result.size());
        } else {
            logger.info("No update required. There are no updated entries in CRM");
        }
        return result;
    }

    private List<User> getUpdatedUsersFromCRM(Long lastUpdate) {
        List<User> result = new ArrayList<>();
        List<UserCRM> usersCrm = crmRepository.findAllByLastUpdatedGreaterThan(lastUpdate);
        for (UserCRM userCRM : usersCrm) {
            try {
                result.add(convertUserCRMtoUser(userCRM));
            } catch (NullPointerException ex) {
                logger.warn("The user [{}] can not be saved because it has a null at the required field: [{}]",
                        userCRM, ex.getMessage());
            }
        }
        return result;
    }

    private User convertUserCRMtoUser(UserCRM userCRM) throws NullPointerException {
        return new User(
                UUID.fromString(userCRM.getUuid()),
                userCRM.getFirstName(),
                userCRM.getLastName(),
                userCRM.getEmail(),
                userCRM.getGmail(),
                userCRM.getSlack(),
                userCRM.getSkype(),
                userCRM.getLastUpdated()
        );
    }
}