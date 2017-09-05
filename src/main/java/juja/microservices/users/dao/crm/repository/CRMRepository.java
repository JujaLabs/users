package juja.microservices.users.dao.crm.repository;

import juja.microservices.users.dao.crm.domain.UserCRM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Vadim Dyachenko
 */
@Repository
public interface CRMRepository extends JpaRepository<UserCRM, Long> {
    List<UserCRM> findAllByLastUpdatedGreaterThan(Long date);
}
