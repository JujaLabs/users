package juja.microservices.users.dao;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import juja.microservices.users.entity.User;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * @author Vadim Dyachenko
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class})
@DatabaseSetup("classpath:datasets/users-data.xml")
public class UserRepositoryTest {

    @Inject
    private UserRepository repository;

    @Test
    public void testFindAll() throws Exception {
        List<User> users = repository.findAll();
        assertEquals(2, users.size());
        assertEquals("Alex", users.get(0).getFirstName());
        assertEquals("Max", users.get(1).getFirstName());
    }

    @Test
    public void testFindBySlack() throws Exception {
        User user = repository.findOneBySlack("alex.batman");
        assertEquals("Batman Alex", user.getFullName());
        assertEquals("alex.batman", user.getSlack());
    }

    @Test
    public void testFindByUuid() throws Exception {
        User user = repository.findOneByUuid(new UUID(1L,3L));
        assertEquals("Superman Max", user.getFullName());
    }
}