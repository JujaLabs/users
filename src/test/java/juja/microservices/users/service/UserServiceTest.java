package juja.microservices.users.service;

import juja.microservices.users.dao.UserRepository;
import juja.microservices.users.dao.UserRepositoryTest;
import juja.microservices.users.entity.User;
import juja.microservices.users.entity.UserSearchRequest;
import org.eclipse.jetty.server.Authentication;
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
        List<User> actualList = service.getAllUsers(0, 20);
        assertEquals(expectedList, actualList);
    }

    @Test
    public void searchUserByEmailTest() throws Exception {
        List<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(mock(User.class));
        UserSearchRequest request = new UserSearchRequest();
        request.email = "vasya@mail.ru";
        when(repository.getUsersByParameters(request.toMap())).thenReturn(expectedUsers);
        List<User> actualUser = service.searchUser(request);
        assertEquals(expectedUsers, actualUser);
    }

    @Test
    public void searchUserByUuid() throws Exception {
        List<User> expectedUsers = new ArrayList<>();
        User expectedUser = mock(User.class);
        expectedUsers.add(expectedUser);
        Map params = new HashMap<String, String>();
        params.put("uuid", "AAA123");
        when(repository.getUsersByParameters(params)).thenReturn(expectedUsers);
        User actualUser = service.searchUser("AAA123");
        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void searchUserWithOr() throws Exception {
        List<User> expectedUsers1 = new ArrayList<>();
        expectedUsers1.add(mock(User.class));
        List<User> expectedUsers2 = new ArrayList<>();
        expectedUsers2.add(mock(User.class));
        List<User> expectedResult = new ArrayList<>();
        expectedResult.addAll(expectedUsers1);
        expectedResult.addAll(expectedUsers2);

        List<UserSearchRequest> requests = new ArrayList<>();
        UserSearchRequest request = new UserSearchRequest();
        request.slack = "vasya.slack";
        requests.add(request);
        UserSearchRequest request2 = new UserSearchRequest();
        request2.slack = "bob.slack";
        requests.add(request2);

        when(repository.getUsersByParameters(request.toMap())).thenReturn(expectedUsers1);
        when(repository.getUsersByParameters(request2.toMap())).thenReturn(expectedUsers2);

        List<User> actualResult = service.searchUserWithOr(requests);
        assertEquals(expectedResult, actualResult);
    }
}