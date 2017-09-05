package juja.microservices.users.dao.users.repository;

import juja.microservices.users.dao.users.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * @author Denis Tantsev (dtantsev@gmail.com)
 * @author Olga Kulykova
 * @author Vadim Dyachenko
 */
@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, UUID> {
    User findOneBySlack(String slack);

    User findOneByUuid(UUID uuid);

    @Query("SELECT max(u.lastUpdated) FROM User u")
    Long findMaxLastUpdate();
}