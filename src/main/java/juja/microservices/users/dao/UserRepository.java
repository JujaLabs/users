package juja.microservices.users.dao;

import juja.microservices.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * @author Denis Tantsev (dtantsev@gmail.com)
 * @author Olga Kulykova
 * @author Vadim Dyachenko
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    User findOneBySlack(String slack);
    User findOneByUuid(UUID uuid);
}