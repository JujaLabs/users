package juja.microservices.users.service;

import juja.microservices.users.dao.crm.domain.UserCRM;
import juja.microservices.users.dao.crm.repository.CRMRepository;
import juja.microservices.users.dao.users.domain.User;
import juja.microservices.users.dao.users.repository.UserRepository;
import juja.microservices.users.entity.UserDTO;
import juja.microservices.users.entity.UsersSlackNamesRequest;
import juja.microservices.users.entity.UsersUuidRequest;
import juja.microservices.users.exceptions.UserException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Denis Tantsev (dtantsev@gmail.com)
 * @author Olga Kulykova
 * @author Vadim Dyachenko
 * @author Ivan Shapovalov
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
        //given
        UUID uuid = new UUID(1L, 2L);
        List<UserDTO> expected = new ArrayList<>();
        expected.add(new UserDTO(uuid, "vasya", "vasya.ivanoff", "Ivanoff Vasya"));
        List<User> users = new ArrayList<>();
        users.add(new User(uuid, "Vasya", "Ivanoff", "vasya", "vasya.ivanoff", 777L));
        when(repository.findAll()).thenReturn(users);

        //when
        List<UserDTO> actual = service.getAllUsers();

        //then
        assertEquals(expected, actual);
    }

    @Test
    public void getUsersBySlackNames() throws Exception {
        //given
        UUID uuid1 = new UUID(1L, 2L);
        UUID uuid2 = new UUID(1L, 3L);
        User user1 = new User(uuid1, "Vasya", "Ivanoff", "vasya", "vasya.ivanoff", 777L);
        User user2 = new User(uuid2, "Kolya", "Sidoroff", "kolya", "kolya.sidoroff", 888L);

        List<UserDTO> expected = new ArrayList<>();
        expected.add(new UserDTO(uuid1, "vasya", "vasya.ivanoff", "Ivanoff Vasya"));
        expected.add(new UserDTO(uuid2, "kolya", "kolya.sidoroff", "Sidoroff Kolya"));

        List<String> slackNames = new ArrayList<>();
        slackNames.add("vasya");
        slackNames.add("kolya");
        UsersSlackNamesRequest request = new UsersSlackNamesRequest(slackNames);
        given(repository.findBySlackIn(slackNames)).willReturn(Arrays.asList(user1, user2));

        //when
        List<UserDTO> actual = service.getUsersBySlackNames(request);

        //then
        assertEquals(expected, actual);
        verify(repository).findBySlackIn(slackNames);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void getUsersByUuids() throws Exception {
        //given
        UUID uuid1 = new UUID(1L, 2L);
        UUID uuid2 = new UUID(1L, 3L);
        User user1 = new User(uuid1, "Vasya", "Ivanoff", "vasya", "vasya.ivanoff", 777L);
        User user2 = new User(uuid2, "Kolya", "Sidoroff", "kolya", "kolya.sidoroff", 888L);

        List<UserDTO> expected = new ArrayList<>();
        expected.add(new UserDTO(uuid1, "vasya", "vasya.ivanoff", "Ivanoff Vasya"));
        expected.add(new UserDTO(uuid2, "kolya", "kolya.sidoroff", "Sidoroff Kolya"));

        List<UUID> uuids = new ArrayList<>();
        uuids.add(uuid1);
        uuids.add(uuid2);
        UsersUuidRequest request = new UsersUuidRequest(uuids);
        given(repository.findByUuidIn(request.getUuids())).willReturn(Arrays.asList(user1, user2));

        //when
        List<UserDTO> actual = service.getUsersByUuids(request);

        //then
        assertEquals(expected, actual);
        verify(repository).findByUuidIn(uuids);
        verifyNoMoreInteractions(repository);
    }

    @Test(expected = UserException.class)
    public void getAllUsersEmptyListTest() throws Exception {
        //given
        when(repository.findAll()).thenReturn(new ArrayList<>());

        //when
        service.getAllUsers();

        //then
        fail();
    }

    @Test
    public void updateUsersFromCRMTest() throws Exception {
        //given
        List<UserCRM> allCrmUsers = new ArrayList<>();
        allCrmUsers.add(new UserCRM(1L,"Alex","Batman",
                "Alex", 100L, "alex.batman", 1,"00000000-0000-0001-0000-000000000002"));
        allCrmUsers.add(new UserCRM(2L,"Max","Superman",
                "Max", 200L, "max.superman", 1,"00000000-0000-0001-0000-000000000003"));

        List<User> savedUser = new ArrayList<>();
        savedUser.add(new User(UUID.fromString("00000000-0000-0001-0000-000000000002"), "Alex","Batman", "alex.batman", "Alex", 100L));
        savedUser.add(new User(UUID.fromString("00000000-0000-0001-0000-000000000003"), "Max","Superman","max.superman", "Max", 200L));

        when(repository.findMaxLastUpdate()).thenReturn(null);
        when(crmRepository.findUpdatedUsers(0L)).thenReturn(allCrmUsers);
        when(repository.save(savedUser)).thenReturn(savedUser);

        //when
        List<UserDTO> actual = service.updateUsersFromCRM();

        //then
        assertEquals(2, actual.size());
        verify(repository).findMaxLastUpdate();
        verify(crmRepository).findUpdatedUsers(0L);
        verify(repository).save(savedUser);
        verify(repository).flush();
        verifyNoMoreInteractions(repository, crmRepository);
    }

    @Test
    public void updateUsersFromCRMWithNullFieldTest() throws Exception {
        //given
        List<UserCRM> allCrmUsers = new ArrayList<>();
        allCrmUsers.add(new UserCRM(1L,"Alex","Batman","Alex", 100L,  "Alex", 1, null));
        allCrmUsers.add(new UserCRM(2L,"Max","Superman","Max", 200L, "max.superman", 1,"00000000-0000-0001-0000-000000000003"));

        List<User> haveToSaveUser = new ArrayList<>();
        haveToSaveUser.add(new User(UUID.fromString("00000000-0000-0001-0000-000000000003"), "Max", "Superman", "max.superman", "Max", 200L));

        when(repository.findMaxLastUpdate()).thenReturn(null);
        when(crmRepository.findUpdatedUsers(0L)).thenReturn(allCrmUsers);
        when(repository.save(haveToSaveUser)).thenReturn(haveToSaveUser);

        //when
        service.updateUsersFromCRM();

        //then
        verify(repository).findMaxLastUpdate();
        verify(crmRepository).findUpdatedUsers(0L);
        verify(repository).save(haveToSaveUser);
        verify(repository).flush();
        verifyNoMoreInteractions(repository, crmRepository);
    }
}