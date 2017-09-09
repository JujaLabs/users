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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItems;
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
    private User user1 = new User(uuid1, "Alex", "Batman", "alex.batman", "Alex", 100L);
    private User user2 = new User(new UUID(1L, 3L), "Max", "Superman", "max.superman", "Max", 100L);

    @Test
    public void testFindAll() throws Exception {
        List<User> expected = Arrays.asList(user1, user2);

        List<User> actual = repository.findAll();

        assertThat(expected, is(actual));
    }

    @Test
    public void testFindOneUserBySlack() throws Exception {
        List<User> users = repository.findBySlackIn(Collections.singletonList(user1.getSlack()));

        assertEquals(1, users.size());
        assertThat(users, contains(user1));
    }

    @Test
    public void testFindTwoUsersByThreeeSlackName() throws Exception {
        List<User> expected = Arrays.asList(user1, user2);
        List<String> slackNames = Arrays.asList(user1.getSlack(), user2.getSlack(), "fake.user");

        List<User> actual = repository.findBySlackIn(slackNames);

        assertThat(expected, is(actual));
    }

    @Test
    public void testFindOneUserByUuid() throws Exception {
        List<User> users = repository.findByUuidIn(Collections.singletonList(user1.getUuid()));

        assertEquals(1, users.size());
        assertThat(users, contains(user1));
    }

    @Test
    public void testFindTwoUsersByUUID() throws Exception {
        List<User> expected = Arrays.asList(user1, user2);
        List<UUID> uuids = Arrays.asList(uuid1, uuid2);

        List<User> actual = repository.findByUuidIn(uuids);

        assertThat(expected, is(actual));
    }

    @Test
    @ExpectedDatabase(value = "/datasets/usersDataAfterUpdate.xml")
    public void testUpdateUsersDatabaseFromCRM() throws Exception {
        User user1Updated = new User(UUID.fromString("00000000-0000-0001-0000-000000000003"), "Max", "Ironman",
                "max.ironman", "Max", 200L);
        User user4 = new User(UUID.fromString("00000000-0000-0001-0000-000000000004"), "Sergey", "Spiderman",
                "sergey.spiderman", "Sergey", 250L);
        List<User> expected = Arrays.asList(user1, user1Updated, user4);
        List<User> newUsers = Arrays.asList(user1Updated, user4);

        repository.save(newUsers);
        repository.flush();

        List<User> actual = repository.findAll();
        assertEquals(3, actual.size());
        assertThat(actual, hasItems(expected.toArray(new User[expected.size()])));
    }
}