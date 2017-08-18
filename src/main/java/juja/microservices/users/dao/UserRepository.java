package juja.microservices.users.dao;

import juja.microservices.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * @author Denis Tantsev (dtantsev@gmail.com)
 * @author Olga Kulykova
 * @author Vadim Dyachenko
 */
public interface UserRepository extends JpaRepository<User, UUID> {
    List<User> getAllUsers();
    User getUserBySlack(String slack);
    User getUserByUuid(String uuid);
}