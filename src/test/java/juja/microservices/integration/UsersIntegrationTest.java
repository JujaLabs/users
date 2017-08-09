package juja.microservices.integration;

import juja.microservices.users.dao.UserRepository;
import juja.microservices.users.entity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.http.MediaType.APPLICATION_PDF;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class UsersIntegrationTest extends BaseIntegrationTest{

    private static final String USERS_URL = "/v1/users";
    private static final String USERS_BY_UUIDS_URL = "/v1/users/usersByUuids";
    private static final String USERS_BY_SLACK_NAMES_URL = "/v1/users/usersBySlackNames";
    private static final String Fake_URL = "/fake";

    private MockMvc mockMvc;

    @MockBean
    private UserRepository repository;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void getAllUsers() throws Exception {
        //given
        List<User> users = new ArrayList<>();
        UUID uuid = new UUID(1L, 2L);
        User user = new User(uuid, "Vasya","Ivanoff", "vasya@mail.ru",
                "vasya@gmail.com","vasya","vasya.ivanoff");
        users.add(user);
        String expected ="[{\"uuid\":\"00000000-0000-0001-0000-000000000002\",\"name\":\"Ivanoff Vasya\"," +
                "\"skype\":\"vasya.ivanoff\",\"slack\":\"vasya\"}]";

        //when
        when(repository.getAllUsers()).thenReturn(users);
        String result = mockMvc.perform(get(USERS_URL)
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        //then
        assertThatJson(result).isEqualTo(expected);
    }

    @Test
    public void getUsersByUuids() throws Exception {
        //given
        UUID uuid = new UUID(1L, 2L);
        User user = new User(uuid, "Vasya","Ivanoff", "vasya@mail.ru",
                "vasya@gmail.com","vasya","vasya.ivanoff");
        String jsonRequest = "{\"uuids\":[\"00000000-0000-0001-0000-000000000002\"]}";
        String expected ="[{\"uuid\":\"00000000-0000-0001-0000-000000000002\",\"name\":\"Ivanoff Vasya\"," +
                "\"skype\":\"vasya.ivanoff\",\"slack\":\"vasya\"}]";

        //when
        when(repository.getUserByUuid("00000000-0000-0001-0000-000000000002")).thenReturn(user);
        String result = mockMvc.perform(post(USERS_BY_UUIDS_URL)
                .content(jsonRequest)
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        //then
        assertThatJson(result).isEqualTo(expected);
    }

    @Test
    public void getUsersBySlackNames() throws Exception {
        //given
        UUID uuid = new UUID(1L, 2L);
        User user = new User(uuid, "Vasya","Ivanoff", "vasya@mail.ru",
                "vasya@gmail.com","vasya","vasya.ivanoff");
        String jsonRequest = "{\"slackNames\":[\"vasya\"]}";
        String expected ="[{\"uuid\":\"00000000-0000-0001-0000-000000000002\",\"name\":\"Ivanoff Vasya\"," +
                "\"skype\":\"vasya.ivanoff\",\"slack\":\"vasya\"}]";

        //when
        when(repository.getUserBySlack("vasya")).thenReturn(user);
        String result = mockMvc.perform(post(USERS_BY_SLACK_NAMES_URL)
                .content(jsonRequest)
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        //then
        assertThatJson(result).isEqualTo(expected);
    }

    @Test
    public void getUsersBySlackNamesUnsupportedMediaType() throws Exception {
        //when
        mockMvc.perform(post(USERS_BY_SLACK_NAMES_URL)
                .contentType(APPLICATION_PDF))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void getUsersBySlackNamesBadRequest() throws Exception {
        //given
        String jsonRequest = "{\"slackkkkNames\":[\"vasya\"]}";

        //when
        mockMvc.perform(post(USERS_BY_SLACK_NAMES_URL)
                .content(jsonRequest)
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getUsersBySlackNamesMethodNotAllowed() throws Exception {
        //given
        String jsonRequest = "{\"slackNames\":[\"vasya\"]}";

        //when
        mockMvc.perform(get(USERS_BY_SLACK_NAMES_URL)
                .content(jsonRequest)
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isMethodNotAllowed());

    }

    @Test
    public void getUsersBySlackNamesNotFound() throws Exception {
        //when
        mockMvc.perform(get(Fake_URL)
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());
    }
}