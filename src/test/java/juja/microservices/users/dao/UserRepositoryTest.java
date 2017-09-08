package juja.microservices.users.dao;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import juja.microservices.config.DBUnitConfig;
import juja.microservices.users.dao.users.domain.User;

import juja.microservices.users.dao.users.repository.UserRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * @author Vadim Dyachenko
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Import(DBUnitConfig.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class})
@DbUnitConfiguration(databaseConnection = {"usersConnection", "crmConnection"})
@DatabaseSetup(value = "/datasets/usersData.xml")
public class UserRepositoryTest {

    @Inject
    private UserRepository repository;

    @Test
    public void testFindAll() throws Exception {
        //when
        List<User> users = repository.findAll();

        //then
        assertEquals(2, users.size());
        assertEquals("Alex", users.get(0).getFirstName());
        assertEquals("Max", users.get(1).getFirstName());
    }

    @Test
    public void testFindBySlack() throws Exception {
        //when
        User user = repository.findOneBySlack("alex.batman");

        //then
        assertEquals("Batman Alex", user.getFullName());
        assertEquals("alex.batman", user.getSlack());
    }

    @Test
    public void testFindByUuid() throws Exception {
        //when

        User user = repository.findOneByUuid(new UUID(1L, 3L));
        //then
        assertEquals("Superman Max", user.getFullName());
    }

    @Test
    @ExpectedDatabase(value = "/datasets/usersDataAfterUpdate.xml")
    public void testUpdateUsersDatabaseFromCRM() throws Exception {
        //given
        List<User> users = new ArrayList<>();
        users.add(new User(UUID.fromString("00000000-0000-0001-0000-000000000003"), "Max", "Ironman",
                "max.ironman", "Max", 200L));
        users.add(new User(UUID.fromString("00000000-0000-0001-0000-000000000004"), "Sergey", "Spiderman",
                "sergey.spiderman", "Sergey", 250L));

        //when
        repository.save(users);
        repository.flush();
    }
}