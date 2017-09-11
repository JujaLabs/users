package juja.microservices.users.dao.crm.repository;

import juja.microservices.users.dao.crm.domain.UserCRM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Vadim Dyachenko
 */
@Repository
public interface CRMRepository extends JpaRepository<UserCRM, Long> {
    @Query("SELECT u FROM UserCRM u WHERE u.lastUpdated > :date AND u.isStudent = 1")
    List<UserCRM> findUpdatedUsers(@Param("date")Long date);
}
