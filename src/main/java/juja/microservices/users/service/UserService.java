package juja.microservices.users.service;

import juja.microservices.users.dao.UserRepository;
import juja.microservices.users.entity.User;
import juja.microservices.users.entity.UserSearchRequest;
import juja.microservices.users.exceptions.UserException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author Denis Tantsev (dtantsev@gmail.com)
 */

@Service
public class UserService {

    private final UserRepository repository;

    @Inject
    public UserService(UserRepository userRepository) {
        this.repository = userRepository;
    }

    public List<User> getAllUsers(int page, int limit) {
        List<User> users = repository.getAllUsers();
        if (page + limit > users.size()) return users;
        if (users.size() == 0) {
            throw new UserException("Seems like no users in list yet!");
        }
        return users.subList(page, page + limit);
    }


    public List<User> searchUser(UserSearchRequest request) {
        List<User> users = repository.getUsersByParameters(request.toMap());
        if (users.size() == 0) {
            throw new UserException("No users found by your request!");
        }
        return users;
    }

    public User searchUser(String uuid) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("uuid", uuid);
        List<User> users = repository.getUsersByParameters(parameters);
        if (users.size() == 0) {
            throw new UserException("No users found by your request!");
        }
        return users.get(0);
    }

    public List<User> searchUserWithOr(List<UserSearchRequest> requests) {
        List<User> result = new ArrayList<>();
        for (UserSearchRequest request : requests) {// todo better - use one query for read user's list
             result.addAll(searchUser(request));
        }
        return result;
    }
}