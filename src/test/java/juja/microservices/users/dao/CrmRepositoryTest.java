package juja.microservices.users.dao;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import juja.microservices.config.DBUnitConfig;
import juja.microservices.users.dao.crm.domain.UserCRM;
import juja.microservices.users.dao.crm.repository.CRMRepository;
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
import java.util.List;

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
@DatabaseSetup(connection = "crmConnection", value = "/datasets/crmData.xml")
public class CrmRepositoryTest {
    @Inject
    private CRMRepository crmRepository;

    @Test
    public void testFindAll() throws Exception {
        //when
        List<UserCRM> users = crmRepository.findUpdatedUsers(0L);

        //then
        assertEquals(4, users.size());
    }

    @Test
    public void testFindAllByLastUpdatedGreaterThan() throws Exception {
        //given
        UserCRM expected = new UserCRM(3L,"Sergey","Spiderman",
                "Sergey", 250L, "sergey.spiderman", "1", "00000000-0000-0001-0000-000000000004");
        //when
        List<UserCRM> users = crmRepository.findUpdatedUsers(220L);

        //then
        assertEquals(1, users.size());
        assertEquals(expected, users.get(0));
    }

    @Test
    public void testFindAllByLastUpdatedGreaterThanShouldReturnEmptyList() throws Exception {
        //when
        List<UserCRM> users = crmRepository.findUpdatedUsers(300L);

        //then
        assertEquals(0, users.size());
    }
}
