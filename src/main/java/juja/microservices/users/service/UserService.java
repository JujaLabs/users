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

        return getConvertedResult(users);
    }

    private List<UserDTO> getConvertedResult(List<User> users) {
        List<UserDTO> result = users.stream()
                .map(this::convertUserToUserDto)
                .collect(Collectors.toList());

        logger.debug("All users converted: {}", result.toString());
        return result;
    }

    private UserDTO convertUserToUserDto(User user) {
        return new UserDTO(
                user.getUuid(),
                user.getSlack(),
                user.getSlackId(),
                user.getSkype(),
                user.getFullName()
        );
    }

    public List<UserDTO> getUsersBySlackNames(UsersSlackNamesRequest request) {
        List<String> slackNames = request.getSlackNames();
        List<User> users = repository.findBySlackIn(slackNames);
        logger.debug("Received response from repository: {}", users.toString());
        findAbsentUsersBySlackNames(slackNames, users);
        return getConvertedResult(users);
    }

    private void findAbsentUsersBySlackNames(List<String> slackNames, List<User> users) {
        logger.debug("Compare slackNames '{}' and users '{}'", slackNames.toString(), users.toString());
        List<String> foundSlackNames = users.stream().map(r -> r.getSlack()).collect(Collectors.toList());
        List<String> notFoundSlackNames = new ArrayList<>(slackNames);
        notFoundSlackNames.removeAll(foundSlackNames);
        if (!notFoundSlackNames.isEmpty()) {
            String message = String.format("Slacknames '%s' has not been found", notFoundSlackNames.toString());
            logger.warn(message);
            throw new UserException(message);
        }
    }

    public List<UserDTO> getUsersByUuids(UsersUuidRequest request) {
        List<UUID> uuids = request.getUuids();
        List<User> users = repository.findByUuidIn(uuids);
        logger.debug("Received response from repository: {}", users.toString());
        findAbsentUsersByUUIDS(uuids, users);
        return getConvertedResult(users);
    }

    private void findAbsentUsersByUUIDS(List<UUID> uuids, List<User> users) {
        logger.debug("Compare uuids '{}' and users '{}'", uuids.toString(), users.toString());
        List<UUID> foundUUIDs = users.stream().map(r -> r.getUuid()).collect(Collectors.toList());
        List<UUID> notFoundUUIDs = new ArrayList<>(uuids);
        notFoundUUIDs.removeAll(foundUUIDs);
        if (!notFoundUUIDs.isEmpty()) {
            String message = String.format("Uuids '%s' has not been found", notFoundUUIDs.toString());
            logger.warn(message);
            throw new UserException(message);
        }
    }

    @Scheduled(cron = "${cron.expression}")
    public void scheduleUpdateUsers() {
        logger.info("Starting scheduled task: Update database from CRM");
        updateUsersFromCRM();
    }

    public List<UserDTO> updateUsersFromCRM() {
        Long lastUpdate = getLastUpdate();
        logger.info("Last update was at {}",
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
            logger.info("{} records updated successful", result.size());
        } else {
            logger.info("No update required. There are no updated entries in CRM");
        }
        return result;
    }

    private List<User> getUpdatedUsersFromCRM(Long lastUpdate) {
        List<User> result = new ArrayList<>();
        List<UserCRM> usersCrm = crmRepository.findUpdatedUsers(lastUpdate);
        logger.info("Received {} records from CRM", usersCrm.size());

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
                userCRM.getSlackId(),
                userCRM.getSkype(),
                userCRM.getLastUpdated()
        );
    }
}