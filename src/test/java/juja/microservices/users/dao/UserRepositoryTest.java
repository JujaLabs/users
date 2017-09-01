package juja.microservices.users.dao;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import juja.microservices.users.dao.users.domain.User;

import juja.microservices.users.dao.users.repository.UserRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import javax.inject.Inject;
import javax.sql.DataSource;
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
@DatabaseSetup(connection = "dataSource", value = "classpath:datasets/users-data.xml")
@DbUnitConfiguration(databaseConnection = {"dataSource", "crmDataSource"})
public class UserRepositoryTest {

//    @Bean(name = "dataSource")
//    @ConfigurationProperties(prefix = "spring.datasource")
//    public DataSource dataSource() {
//        return DataSourceBuilder.create().build();
//    }

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
        User user = repository.findOneByUuid(new UUID(1L, 3L));
        assertEquals("Superman Max", user.getFullName());
    }
}