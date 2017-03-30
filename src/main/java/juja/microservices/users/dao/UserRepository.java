package juja.microservices.users.dao;

import juja.microservices.users.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


/**
 * @author Denis Tantsev (dtantsev@gmail.com)
 */
public interface UserRepository {
    List<User> getAllUsers();
    List<User> getUsersByParameters(Map<String, String> fields);
}
