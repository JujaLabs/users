package juja.microservices.users.service;

import juja.microservices.users.dao.UserRepository;
import juja.microservices.users.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;


/**
 * @author Denis Tantsev (dtantsev@gmail.com)
 */


@RunWith(SpringRunner.class)
@WebMvcTest(UserService.class)
public class UserServiceTest {

    @Inject
    private UserService service;

    @MockBean
    private UserRepository repository;

    @Test
    public void getUserAllUsersTest() throws Exception {
        List<User> expectedList = new ArrayList<>();
        expectedList.add(mock(User.class));
        when(repository.getAllUsers()).thenReturn(expectedList);
        List<User> actualList = service.getAllUsers();
        assertEquals(expectedList, actualList);
    }

    @Test
    public void searchUserByEmailTest() throws Exception {
        List<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(mock(User.class));
        Map params = new HashMap<String,String>();
        params.put("email","vasya@mail.ru");
        when(repository.getUsersByParameters(params)).thenReturn(expectedUsers);
        List<User> actualUser = service.searchUser(params);
        assertEquals(expectedUsers, actualUser);
    }
}