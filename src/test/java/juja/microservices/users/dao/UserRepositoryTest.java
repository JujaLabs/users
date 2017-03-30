package juja.microservices.users.dao;

import juja.microservices.users.entity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import javax.inject.Inject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * @author Denis Tantsev (dtantsev@gmail.com)
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTest {

    @Inject
    private CRMUserRepository crmUserRepository;

    @Inject
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @Before
    public void setup() {
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    public void getAllUsersCRMUserRepositoryTest() throws URISyntaxException, IOException {

        URI uri = UserRepositoryTest.class.getClassLoader().getResource("allUsers.json").toURI();
        String allUsers = new String(Files.readAllBytes(Paths.get(uri)), Charset.forName("utf-8"));

        List<User> expected = new ArrayList<>();
        expected.add(new User("AAAA123", "Vasya", "Ivanoff", "vasya@mail.ru", "vasya@gmail.com", "vasya", "vasya.ivanoff",
                        "linkedin/vasya", "facebook/vasya", "twitter/vasya"));
        expected.add(new User("AAAA456", "Kolya", "Sidoroff", "kolya@mail.ru", "kolya@gmail.com", "kolya", "kolya.sidoroff",
                "linkedin/kolya", "facebook/kolya", "twitter/kolya"));
        expected.add(new User("AAAA789", "Lena", "Petrova", "lena@mail.ru", "lena@gmail.com", "lena", "lena.petrova",
                "linkedin/lena", "facebook/lena", "twitter/lena"));

        mockServer.expect(requestTo("http://127.0.0.1/x2engine/index.php/api2/Contacts?c_isStudent=1")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(allUsers, MediaType.APPLICATION_JSON));

        List<User> result = crmUserRepository.getAllUsers();

        mockServer.verify();
        assertThat(result, is(expected));

    }


    @Test
    public void searchUserByEmailTest() throws URISyntaxException, IOException {

        URI uri = UserRepositoryTest.class.getClassLoader().getResource("vasya.json").toURI();
        String mockUser = new String(Files.readAllBytes(Paths.get(uri)), Charset.forName("utf-8"));

        List<User> expected = new ArrayList<>();
        expected.add(new User("AAAA123", "Vasya", "Ivanoff", "vasya@mail.ru", "vasya@gmail.com", "vasya", "vasya.ivanoff",
                "linkedin/vasya", "facebook/vasya", "twitter/vasya"));

        mockServer.expect(requestTo("http://127.0.0.1/x2engine/index.php/api2/Contacts?c_isStudent=1&email=vasya@mail.ru"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mockUser, MediaType.APPLICATION_JSON));

        Map param = new HashMap<String,String>();
        param.put("email","vasya@mail.ru");
        List<User> result = crmUserRepository.getUsersByParameters(param);
        mockServer.verify();
        assertThat(result, is(expected));
    }

    @Test
    public void searchUserByUuidTest() throws URISyntaxException, IOException {

        URI uri = UserRepositoryTest.class.getClassLoader().getResource("vasya.json").toURI();
        String mockUser = new String(Files.readAllBytes(Paths.get(uri)), Charset.forName("utf-8"));

        List<User> expected = new ArrayList<>();
        expected.add(new User("AAAA123", "Vasya", "Ivanoff", "vasya@mail.ru", "vasya@gmail.com", "vasya", "vasya.ivanoff",
                "linkedin/vasya", "facebook/vasya", "twitter/vasya"));

        mockServer.expect(requestTo("http://127.0.0.1/x2engine/index.php/api2/Contacts?c_isStudent=1&c_uuid=AAAA123"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mockUser, MediaType.APPLICATION_JSON));

        Map param = new HashMap<String,String>();
        param.put("uuid","AAAA123");
        List<User> result = crmUserRepository.getUsersByParameters(param);
        mockServer.verify();
        assertThat(result, is(expected));
    }

    @Test
    public void searchUserBySlackTest() throws URISyntaxException, IOException {

        URI uri = UserRepositoryTest.class.getClassLoader().getResource("vasya.json").toURI();
        String mockUser = new String(Files.readAllBytes(Paths.get(uri)), Charset.forName("utf-8"));

        List<User> expected = new ArrayList<>();
        expected.add(new User("AAAA123", "Vasya", "Ivanoff", "vasya@mail.ru", "vasya@gmail.com", "vasya", "vasya.ivanoff",
                "linkedin/vasya", "facebook/vasya", "twitter/vasya"));

        mockServer.expect(requestTo("http://127.0.0.1/x2engine/index.php/api2/Contacts?c_isStudent=1&c_slack=vasya"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mockUser, MediaType.APPLICATION_JSON));

        Map param = new HashMap<String,String>();
        param.put("slack","vasya");
        List<User> result = crmUserRepository.getUsersByParameters(param);
        mockServer.verify();
        assertThat(result, is(expected));
    }

}