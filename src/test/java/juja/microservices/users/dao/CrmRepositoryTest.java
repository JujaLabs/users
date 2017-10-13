package juja.microservices.users.dao;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import juja.microservices.config.DBUnitConfig;
import juja.microservices.users.dao.crm.domain.UserCRM;
import juja.microservices.users.dao.crm.repository.CRMRepository;
import org.junit.Ignore;
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
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


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
@DatabaseSetup(connection = "crmConnection", value = "/datasets/crmData.xml")
public class CrmRepositoryTest {
    @Inject
    private CRMRepository crmRepository;

    @Ignore
    @Test
    public void findAll() throws Exception {
        //when
        List<UserCRM> users = crmRepository.findUpdatedUsers(0L);

        //then
        assertEquals(6, users.size());
    }

    @Ignore
    @Test
    public void findAllShouldContainsUserAssignedToAnyoneAndVisibility1() throws Exception {
        //given
        UserCRM expected = new UserCRM(7L, "Student", "Happy", "student", 100L, "student", 1, "00000000-0000-0001-0000-000000000007", "Anyone", 1);

        //when
        List<UserCRM> users = crmRepository.findUpdatedUsers(0L);

        //then
        assertTrue(users.contains(expected));
    }

    @Ignore
    @Test
    public void findAllShouldContainsUserAssignedToSomeoneAndVisibility0() throws Exception {
        //given
        UserCRM expected = new UserCRM(8L, "Boomer", "MPower", "mpower", 100L, "boomer", 1, "00000000-0000-0001-0000-000000000008", "Someone", 0);

        //when
        List<UserCRM> users = crmRepository.findUpdatedUsers(0L);

        //then
        assertTrue(users.contains(expected));
    }

    @Ignore
    @Test
    public void findAllShouldNotContainsUserAssignedToAnyoneAndVisibility0() throws Exception {
        //when
        List<UserCRM> users = crmRepository.findUpdatedUsers(0L);

        //given
        List<UserCRM> expectedEmptyList = new ArrayList<>();
        List<UserCRM> resultList = new ArrayList<>();
        for (UserCRM user : users) {
            if (user.getAssignedTo().equals("Anyone") && user.getVisibility() == 0) {
                resultList.add(user);
            }
        }

        //then
        assertEquals(expectedEmptyList, resultList);
    }

    @Ignore
    @Test
    public void findAllByLastUpdatedGreaterThan() throws Exception {
        //given
        UserCRM expected = new UserCRM(3L, "Sergey", "Spiderman",
                "Sergey", 250L, "sergey.spiderman", 1, "00000000-0000-0001-0000-000000000004", "Someone", 1);
        //when
        List<UserCRM> users = crmRepository.findUpdatedUsers(220L);

        //then
        assertEquals(1, users.size());
        assertEquals(expected, users.get(0));
    }

    @Ignore
    @Test
    public void findAllByLastUpdatedGreaterThanShouldReturnEmptyList() throws Exception {
        //when
        List<UserCRM> users = crmRepository.findUpdatedUsers(300L);

        //then
        assertEquals(0, users.size());
    }
}
