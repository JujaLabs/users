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

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author Vadim Dyachenko
 * @author Ivan Shapovalov
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

    private UUID uuid1 = new UUID(1L, 2L);
    private UUID uuid2 = new UUID(1L, 3L);
    private User user1 = new User(uuid1, "Alex", "Batman", "alex.batman", "AlexSlackID", "Alex", 100L);
    private User user2 = new User(new UUID(1L, 3L), "Max", "Superman", "max.superman", "MaxSlackID", "Max", 100L);

    @Test
    public void testFindAll() throws Exception {
        //given
        List<User> expected = Arrays.asList(user1, user2);

        //when
        List<User> actual = repository.findAll();

        //then
        assertThat(expected, is(actual));
    }

    @Test
    public void testFindOneUserBySlack() throws Exception {
        //when
        List<User> users = repository.findBySlackIn(Collections.singletonList(user1.getSlack()));

        //then
        assertEquals(1, users.size());
        assertThat(users, contains(user1));
    }

    @Test
    public void testFindTwoUsersByThreeSlackName() throws Exception {
        //given
        List<User> expected = Arrays.asList(user1, user2);
        List<String> slackNames = Arrays.asList(user1.getSlack(), user2.getSlack(), "fake.user");

        //when
        List<User> actual = repository.findBySlackIn(slackNames);

        //then
        assertThat(expected, is(actual));
    }

    @Test
    public void testFindOneUserByUuid() throws Exception {
        //when
        List<User> users = repository.findByUuidIn(Collections.singletonList(user1.getUuid()));

        //then
        assertEquals(1, users.size());
        assertThat(users, contains(user1));
    }

    @Test
    public void testFindTwoUsersByUUID() throws Exception {
        //given
        List<User> expected = Arrays.asList(user1, user2);
        List<UUID> uuids = Arrays.asList(uuid1, uuid2);

        //when
        List<User> actual = repository.findByUuidIn(uuids);

        //then
        assertThat(expected, is(actual));
    }

    @Test
    @ExpectedDatabase(value = "/datasets/usersDataAfterUpdate.xml")
    public void testUpdateUsersDatabaseFromCRM() throws Exception {
        //given
        List<User> users = new ArrayList<>();
        users.add(new User(UUID.fromString("00000000-0000-0001-0000-000000000003"), "Max", "Ironman",
                "max.ironman", "MaxSlackID", "Max", 200L));
        users.add(new User(UUID.fromString("00000000-0000-0001-0000-000000000004"), "Sergey", "Spiderman",
                "sergey.spiderman", "SergeySlackID", "Sergey", 250L));

        //when
        repository.save(users);
        repository.flush();
    }
}