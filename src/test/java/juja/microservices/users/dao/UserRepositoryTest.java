package juja.microservices.users.dao;

import juja.microservices.users.entity.Keeper;
import juja.microservices.users.entity.User;
import juja.microservices.users.exceptions.UserException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * @author Denis Tantsev (dtantsev@gmail.com)
 * @author Olga Kulykova
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTest {

    @Inject
    private CRMUserRepository crmUserRepository;

    @Inject
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @Value("${x2.baseUrl}")
    private String x2BaseUrl;

    @Value("${x2.contactsUrl}")
    private String x2ContactsUrl;

    @Value("${x2.keepersUrl}")
    private String x2KeepersUrl;

    @Before
    public void setup() {
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    public void getAllUsersCRMUserRepositoryTest() throws URISyntaxException, IOException {
        String allUsers = jsonFromFile("allUsers.json");

        List<User> expected = new ArrayList<>();
        expected.add(new User("AAAA123", "Vasya", "Ivanoff", "vasya@mail.ru", "vasya@gmail.com", "vasya", "vasya.ivanoff"));
        expected.add(new User("AAAA456", "Kolya", "Sidoroff", "kolya@mail.ru", "kolya@gmail.com", "kolya", "kolya.sidoroff"));
        expected.add(new User("AAAA789", "Lena", "Petrova", "lena@mail.ru", "lena@gmail.com", "lena", "lena.petrova"));

        mockServer.expect(requestTo(x2BaseUrl + x2ContactsUrl+"?c_isStudent=1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(allUsers, MediaType.APPLICATION_JSON));

        List<User> result = crmUserRepository.getAllUsers();

        mockServer.verify();
        assertThat(result, is(expected));
    }

    @Test
    public void searchUserBySlackTest() throws URISyntaxException, IOException {
        String mockUser = jsonFromFile("vasya.json");

        User expected = new User("AAAA123", "Vasya", "Ivanoff", "vasya@mail.ru", "vasya@gmail.com", "vasya",
                "vasya.ivanoff");

        mockServer.expect(requestTo(x2BaseUrl + x2ContactsUrl+"?c_isStudent=1&c_slack=vasya"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mockUser, MediaType.APPLICATION_JSON));

        User result = crmUserRepository.getUserBySlack("vasya");
        mockServer.verify();
        assertThat(result, is(expected));
    }

    @Test
    public void searchUserByUuidTest() throws URISyntaxException, IOException {
        String mockUser = jsonFromFile("vasya.json");

        User expected = new User("AAAA123", "Vasya", "Ivanoff", "vasya@mail.ru", "vasya@gmail.com", "vasya",
                "vasya.ivanoff");

        mockServer.expect(requestTo(x2BaseUrl + x2ContactsUrl+"?c_isStudent=1&c_uuid=AAAA123"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mockUser, MediaType.APPLICATION_JSON));

        User result = crmUserRepository.getUserByUuid("AAAA123");
        mockServer.verify();
        assertThat(result, is(expected));
    }

    @Test
    public void getActiveKeepersCRMUserRepositoryTest() throws URISyntaxException, IOException {
        String keepersCRM = jsonFromFile("keepersCRM.json");

        mockServer.expect(requestTo(x2BaseUrl + x2KeepersUrl+"?c_isActive=1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(keepersCRM, MediaType.APPLICATION_JSON));

        mockUser("vasya.json", "123");
        mockUser("kolya.json", "456");
        mockUser("vasya.json", "123");

        List<Keeper> expected = new ArrayList<>();
        expected.add(new Keeper("AAAA123", "codejoy keeper", "Ivanoff"));
        expected.add(new Keeper("AAAA456", "interview keeper", "Sidoroff"));
        expected.add(new Keeper("AAAA123", "gamification keeper", "Petrova"));

        List<Keeper> result = crmUserRepository.getActiveKeepers();

        mockServer.verify();
        assertThat(result, is(expected));
    }

    private void mockUser(String userJson, String userId) throws URISyntaxException, IOException {
        URI uri = UserRepositoryTest.class.getClassLoader().getResource(userJson).toURI();
        String mockUser = new String(Files.readAllBytes(Paths.get(uri)), Charset.forName("utf-8"));

        mockServer.expect(requestTo(x2BaseUrl + x2ContactsUrl+"?c_isStudent=1&c_id=" + userId))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mockUser, MediaType.APPLICATION_JSON));
    }


    @Test(expected = UserException.class)
    public void searchUnexistedUserByUuidTest() throws URISyntaxException, IOException {
        //given
        String mockUser = "[]";
        mockServer.expect(requestTo(x2BaseUrl + x2ContactsUrl+"?c_isStudent=1&c_id=123"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mockUser, MediaType.APPLICATION_JSON));

        //when
        User result = crmUserRepository.getUserById("123");

        //then
        fail();
    }

    @Test(expected = UserException.class)
    public void searchDuplicateUserByUuidTest() throws URISyntaxException, IOException {
        //given
        String mockUser = jsonFromFile("duplicateUser.json");;
        mockServer.expect(requestTo(x2BaseUrl + x2ContactsUrl+"?c_isStudent=1&c_id=123"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mockUser, MediaType.APPLICATION_JSON));

        //when
        User result = crmUserRepository.getUserById("123");

        //then
        fail();
    }

    private String jsonFromFile(String file) throws URISyntaxException, IOException {
        URI uri = UserRepositoryTest.class.getClassLoader().getResource(file).toURI();
        return new String(Files.readAllBytes(Paths.get(uri)), Charset.forName("utf-8"));
    }

}