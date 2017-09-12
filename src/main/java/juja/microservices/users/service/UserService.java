package juja.microservices.users.service;

import juja.microservices.users.dao.crm.domain.UserCRM;
import juja.microservices.users.dao.crm.repository.CRMRepository;
import juja.microservices.users.dao.users.domain.User;
import juja.microservices.users.dao.users.repository.UserRepository;
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
 * @author Ivan Shapovalov
 */
@Service
public class UserService {

    private final UserRepository repository;
    private final CRMRepository crmRepository;
    private final Logger wrongUserLogger = LoggerFactory.getLogger("Wrong User");
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
        List<String> slackNames = request.getSlackNames();
        List<User> users = repository.findBySlackIn(slackNames);
        logger.debug("Received response from repository: {}", users.toString());
        compareSlackNamesAndUsers(slackNames, users);
        List<UserDTO> result = getConvertedResult(users);
        logger.debug("All users converted: {}", result.toString());
        return result;
    }

    private void compareSlackNamesAndUsers(List<String> slackNames, List<User> users) {
        logger.debug("Compare slackNames '{}' and users '{}'", slackNames.toString(), users.toString());
        List<String> foundSlackNames = users.stream()
                .map(r -> r.getSlack())
                .filter(sn -> slackNames.contains(sn))
                .collect(Collectors.toList());
        List<String> notFoundSlackNames = slackNames.stream()
                .filter(sn -> !foundSlackNames.contains(sn))
                .collect(Collectors.toList());
        if (!notFoundSlackNames.isEmpty()) {
            String message = "Error. ";
            message += String.format("Slacknames '%s' has not been found",
                    notFoundSlackNames.stream().sorted().collect(Collectors.joining(" ")));
            logger.warn(message);
            throw new UserException("Error. Some slacknames has not been found");
        }
    }

    public List<UserDTO> getUsersByUuids(UsersUuidRequest request) {
        List<UUID> uuids = request.getUuids();
        List<User> users = repository.findByUuidIn(uuids);
        logger.debug("Received response from repository: {}", users.toString());
        compareUUIDsAndUsers(uuids, users);
        List<UserDTO> result = getConvertedResult(users);
        logger.debug("All users converted: {}", result.toString());

        return result;
    }

    private void compareUUIDsAndUsers(List<UUID> uuids, List<User> users) {
        logger.debug("Compare uuids '{}' and users '{}'", uuids.toString(), users.toString());
        List<UUID> foundUUIDs = users.stream()
                .map(r -> r.getUuid())
                .filter(sn -> uuids.contains(sn))
                .collect(Collectors.toList());
        List<UUID> notFoundUUIDs = uuids.stream()
                .filter(sn -> !foundUUIDs.contains(sn))
                .collect(Collectors.toList());
        if (!notFoundUUIDs.isEmpty()) {
            String message = String.format("Error. UUIDS '%s' has not been found",
                    notFoundUUIDs.stream().sorted()
                            .map(uuid -> uuid.toString())
                            .collect(Collectors.joining(" ")));
            logger.warn(message);
            throw new UserException("Error. Some uuids has not been found");
        }
    }

    @Scheduled(cron = "${cron.expression}")
    public void scheduleUpdateUsers() {
        updateUsersFromCRM();
    }

    public List<UserDTO> updateUsersFromCRM() {
        Long lastUpdate = getLastUpdate();
        logger.info("Starting update users database. Last update was at {}",
                LocalDateTime.ofEpochSecond(lastUpdate, 0, OffsetDateTime.now().getOffset()));

        final List<User> crmUsers = getUpdatedUsersFromCRM(lastUpdate);
        final List<User> result = updateUsersDatabase(crmUsers);

        return getConvertedResult(result);
    }

    private Long getLastUpdate() {
        Long lastUpdate = repository.findMaxLastUpdate();
        return lastUpdate == null ? 0L : lastUpdate;
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
                wrongUserLogger.warn("{} can not be saved because it has a null at the required field: [{}]",
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
                userCRM.getSlack(),
                userCRM.getSkype(),
                userCRM.getLastUpdated()
        );
    }
}