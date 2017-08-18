package juja.microservices.users.dao;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import juja.microservices.users.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Vadim Dyachenko
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class UsersRepositoryTest {

    @Inject
    private UserRepository repository;

    @Test
    public void testFind() throws Exception {
        List<User> users = repository.getAllUsers();
        assertEquals(1, users.size());
        assertEquals("Alex", users.get(0).getFirstName());
    }
}
