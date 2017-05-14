package juja.microservices.users.service;

import juja.microservices.users.dao.UserRepository;
import juja.microservices.users.entity.*;
import juja.microservices.users.exceptions.UserException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

/**
 * @author Denis Tantsev (dtantsev@gmail.com)
 * @author Olga Kulykova
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
        List<UserDTO> expected = new ArrayList<>();
        expected.add(new UserDTO("AAAA123", "vasya", "vasya.ivanoff", "Ivanoff Vasya"));
        List<User> users = new ArrayList<>();
        users.add(new User("AAAA123", "Vasya", "Ivanoff", "vasya@mail.ru", "vasya@gmail.com", "vasya", "vasya.ivanoff",
                "linkedin/vasya", "facebook/vasya", "twitter/vasya"));
        when(repository.getAllUsers()).thenReturn(users);
        List<UserDTO> actual = service.getAllUsers();
        assertEquals(expected, actual);
    }

    @Test
    public void getUsersUuidBySlack() throws Exception {
        User user1 = new User("AAAA123", "Vasya", "Ivanoff", "vasya@mail.ru", "vasya@gmail.com", "vasya", "vasya.ivanoff",
                "linkedin/vasya", "facebook/vasya", "twitter/vasya");
        User user2 = new User("AAAA456", "Kolya", "Sidoroff", "kolya@mail.ru", "kolya@gmail.com", "kolya", "kolya.sidoroff",
                "linkedin/kolya", "facebook/kolya", "twitter/kolya");
        List<UserDTO> expected = new ArrayList<>();
        expected.add(new UserDTO("AAAA123", "vasya", null, null));
        expected.add(new UserDTO("AAAA456", "kolya", null, null));

        List<String> slackNames = new ArrayList<>();
        slackNames.add("vasya");
        slackNames.add("ivan");
        UsersSlackRequest request = new UsersSlackRequest(slackNames);

        when(repository.getUserBySlack(request.getSlackNames().get(0))).thenReturn(user1);
        when(repository.getUserBySlack(request.getSlackNames().get(1))).thenReturn(user2);

        List<UserDTO> actual = service.getUsersUuidBySlack(request);
        assertEquals(expected, actual);
    }

    @Test
    public void getUsersNameByUuid() throws Exception {
        User user1 = new User("AAAA123", "Vasya", "Ivanoff", "vasya@mail.ru", "vasya@gmail.com", "vasya", "vasya.ivanoff",
                "linkedin/vasya", "facebook/vasya", "twitter/vasya");
        User user2 = new User("AAAA456", "Kolya", "Sidoroff", "kolya@mail.ru", "kolya@gmail.com", "kolya", "kolya.sidoroff",
                "linkedin/kolya", "facebook/kolya", "twitter/kolya");
        List<UserDTO> expected = new ArrayList<>();
        expected.add(new UserDTO("AAAA123", null, null, "Ivanoff Vasya"));
        expected.add(new UserDTO("AAAA456", null, null, "Sidoroff Kolya"));

        List<String> uuids = new ArrayList<>();
        uuids.add("AAAA123");
        uuids.add("AAAA456");
        UsersUuidRequest request = new UsersUuidRequest(uuids);

        when(repository.getUserByUuid(request.getUuid().get(0))).thenReturn(user1);
        when(repository.getUserByUuid(request.getUuid().get(1))).thenReturn(user2);

        List<UserDTO> actual = service.getUsersNameByUuid(request);
        assertEquals(expected, actual);
    }

    @Test
    public void getActiveKeepersTest() throws Exception {

        List<Keeper> keepers = new ArrayList<>();
        keepers.add(new Keeper("AAAA123", "description1", "Ivanoff"));
        keepers.add(new Keeper("AAAA456", "description2", "Sidoroff"));
        keepers.add(new Keeper("AAAA123", "description3", "Petrova"));

        List<Keeper> expected = keepers;
        when(repository.getActiveKeepers()).thenReturn(keepers);
        List<Keeper> actual = service.getActiveKeepers();
        assertEquals(expected, actual);
    }

    @Test
    public void getActiveKeepersNoActiveKeepersTest() throws Exception {

        when(repository.getActiveKeepers()).thenReturn(new ArrayList<>());
        try {
            List<Keeper> actual = service.getActiveKeepers();
            fail();
        } catch (UserException e) {
            assertEquals("No active keepers found by your request!", e.getMessage());
        }
    }
}