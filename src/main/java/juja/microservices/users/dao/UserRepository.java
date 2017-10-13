package juja.microservices.users.dao;

import juja.microservices.users.entity.Keeper;
import juja.microservices.users.entity.User;

import java.util.List;

/**
 * @author Denis Tantsev (dtantsev@gmail.com)
 * @author Olga Kulykova
 */
public interface UserRepository {
    List<User> getAllUsers();
    User getUserBySlack(String slack);
    User getUserByUuid(String uuid);
    User getUserById(String id);

    List<Keeper> getActiveKeepers();
}
