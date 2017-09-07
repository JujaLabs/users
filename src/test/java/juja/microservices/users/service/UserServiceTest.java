package juja.microservices.users.service;

import juja.microservices.users.dao.crm.domain.UserCRM;
import juja.microservices.users.dao.crm.repository.CRMRepository;
import juja.microservices.users.dao.users.repository.UserRepository;
import juja.microservices.users.dao.users.domain.User;
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
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Denis Tantsev (dtantsev@gmail.com)
 * @author Olga Kulykova
 * @author Vadim Dyachenko
 */
@RunWith(SpringRunner.class)
@WebMvcTest(UserService.class)
public class UserServiceTest {

    @Inject
    private UserService service;

    @MockBean
    private UserRepository repository;

    @MockBean
    private CRMRepository crmRepository;

    @Test
    public void getUserAllUsersTest() throws Exception {
        UUID uuid = new UUID(1L,2L);
        List<UserDTO> expected = new ArrayList<>();
        expected.add(new UserDTO(uuid, "vasya", "vasya.ivanoff", "Ivanoff Vasya"));

        List<User> users = new ArrayList<>();
        users.add(new User(uuid, "Vasya", "Ivanoff", "vasya", "vasya.ivanoff", 777L));
        when(repository.findAll()).thenReturn(users);
        List<UserDTO> actual = service.getAllUsers();

        assertEquals(expected, actual);
    }

    @Test
    public void getUsersBySlackNames() throws Exception {
        UUID uuid1 = new UUID(1L,2L);
        UUID uuid2 = new UUID(1L,3L);
        User user1 = new User(uuid1, "Vasya", "Ivanoff", "vasya", "vasya.ivanoff", 777L);
        User user2 = new User(uuid2, "Kolya", "Sidoroff", "kolya", "kolya.sidoroff", 888L);

        List<UserDTO> expected = new ArrayList<>();
        expected.add(new UserDTO(uuid1, "vasya", "vasya.ivanoff", "Ivanoff Vasya"));
        expected.add(new UserDTO(uuid2, "kolya", "kolya.sidoroff", "Sidoroff Kolya"));

        List<String> slackNames = new ArrayList<>();
        slackNames.add("vasya");
        slackNames.add("ivan");
        UsersSlackNamesRequest request = new UsersSlackNamesRequest(slackNames);

        when(repository.findOneBySlack(request.getSlackNames().get(0))).thenReturn(user1);
        when(repository.findOneBySlack(request.getSlackNames().get(1))).thenReturn(user2);

        List<UserDTO> actual = service.getUsersBySlackNames(request);
        assertEquals(expected, actual);
    }

    @Test
    public void getUsersByUuids() throws Exception {
        UUID uuid1 = new UUID(1L,2L);
        UUID uuid2 = new UUID(1L,3L);
        User user1 = new User(uuid1, "Vasya", "Ivanoff", "vasya", "vasya.ivanoff", 777L);
        User user2 = new User(uuid2, "Kolya", "Sidoroff", "kolya", "kolya.sidoroff", 888L);
        List<UserDTO> expected = new ArrayList<>();
        expected.add(new UserDTO(uuid1, "vasya", "vasya.ivanoff", "Ivanoff Vasya"));
        expected.add(new UserDTO(uuid2, "kolya", "kolya.sidoroff", "Sidoroff Kolya"));

        List<UUID> uuids = new ArrayList<>();
        uuids.add(uuid1);
        uuids.add(uuid2);
        UsersUuidRequest request = new UsersUuidRequest(uuids);

        when(repository.findOneByUuid(request.getUuids().get(0))).thenReturn(user1);
        when(repository.findOneByUuid(request.getUuids().get(1))).thenReturn(user2);

        List<UserDTO> actual = service.getUsersByUuids(request);
        assertEquals(expected, actual);
    }

    @Test(expected = UserException.class)
    public void getAllUsersEmptyListTest() throws Exception {
        when(repository.findAll()).thenReturn(new ArrayList<>());
        service.getAllUsers();
        fail();
    }

    @Test
    public void updateUsersFromCRMTest() throws Exception {
        //given
        List<UserCRM> allCrmUsers = new ArrayList<>();
        allCrmUsers.add(new UserCRM(1L,"Alex","Batman",
                "Alex", 100L, "alex.batman", "00000000-0000-0001-0000-000000000002"));
        allCrmUsers.add(new UserCRM(2L,"Max","Superman",
                "Max", 200L, "max.superman", "00000000-0000-0001-0000-000000000003"));

        List<User> savedUser = new ArrayList<>();
        savedUser.add(new User(UUID.fromString("00000000-0000-0001-0000-000000000002"), "Alex","Batman", "alex.batman", "Alex", 100L));
        savedUser.add(new User(UUID.fromString("00000000-0000-0001-0000-000000000003"), "Max","Superman","max.superman", "Max", 200L));

        when(repository.findMaxLastUpdate()).thenReturn(null);
        when(crmRepository.findAllByLastUpdatedGreaterThan(0L)).thenReturn(allCrmUsers);
        when(repository.save(anyList())).thenReturn(savedUser);
        //when
        List<UserDTO> actual = service.updateUsersFromCRM();

        //then
        verify(repository).flush();
        assertEquals(2, actual.size());
    }

    @Test
    public void updateUsersFromCRMWithNullFieldTest() throws Exception {
        //given
        List<UserCRM> allCrmUsers = new ArrayList<>();
        allCrmUsers.add(new UserCRM(1L,"Alex","Batman","Alex", 100L,  "Alex", null));
        allCrmUsers.add(new UserCRM(2L,"Max","Superman","Max", 200L, "max.superman", "00000000-0000-0001-0000-000000000003"));

        List<User> haveToSaveUser = new ArrayList<>();
        haveToSaveUser.add(new User(UUID.fromString("00000000-0000-0001-0000-000000000003"), "Max","Superman","max.superman", "Max", 200L));

        when(repository.findMaxLastUpdate()).thenReturn(null);
        when(crmRepository.findAllByLastUpdatedGreaterThan(0L)).thenReturn(allCrmUsers);
        when(repository.save(haveToSaveUser)).thenReturn(haveToSaveUser);

        //when
        List<UserDTO> actual = service.updateUsersFromCRM();

        //then
        verify(repository).save(haveToSaveUser);
        verify(repository).flush();
    }
}