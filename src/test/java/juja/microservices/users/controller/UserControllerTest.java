package juja.microservices.users.controller;

import juja.microservices.users.entity.UserDTO;
import juja.microservices.users.entity.UsersUuidRequest;
import juja.microservices.users.entity.UsersSlackRequest;
import juja.microservices.users.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Denis Tantsev (dtantsev@gmail.com)
 * @author Olga Kulykova
 */
@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Inject
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @Test
    public void getAllUsersShouldReturnOk() throws Exception {
        String expected =
                "[{\"uuid\":\"00000000-0000-0001-0000-000000000002\",\"slack\":\"vasya\",\"skype\":\"vasya.ivanoff\",\"name\":\"Ivanoff Vasya\"}," +
                " {\"uuid\":\"00000000-0000-0001-0000-000000000003\",\"slack\":\"ivan\",\"skype\":\"ivan.vasilieff\",\"name\":\"Vasilieff Ivan\"}]";

        List<UserDTO> users = new ArrayList<>();
        users.add(new UserDTO(new UUID(1L, 2L), "vasya", "vasya.ivanoff", "Ivanoff Vasya"));
        users.add(new UserDTO(new UUID(1L, 3L), "ivan", "ivan.vasilieff", "Vasilieff Ivan"));

        when(service.getAllUsers()).thenReturn(users);

        String result = mockMvc.perform(get("/v1/users")
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThatJson(result).isEqualTo(expected);
    }

    @Test
    public void getUserUuidBySlackShouldReturnOk() throws Exception {
        String expected =
                "[{\"uuid\":\"00000000-0000-0001-0000-000000000002\",\"slack\":\"vasya\"}," +
                " {\"uuid\":\"00000000-0000-0001-0000-000000000003\",\"slack\":\"ivan\"}]";

        List<UserDTO> users = new ArrayList<>();
        users.add(new UserDTO(new UUID(1L, 2L), "vasya", null, null));
        users.add(new UserDTO(new UUID(1L, 3L), "ivan", null, null));
        String jsonRequest = "{\"slackNames\":[\"vasya\",\"ivan\"]}";

        when(service.getUsersUuidBySlack(any(UsersSlackRequest.class))).thenReturn(users);

        String result = mockMvc.perform(post("/v1/users/uuidBySlack")
                .contentType(APPLICATION_JSON_UTF8)
                .content(jsonRequest))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThatJson(result).isEqualTo(expected);
    }

    @Test
    public void getUsersNameByUuidShouldReturnOk() throws Exception {
        String expected =
                "[{\"uuid\":\"00000000-0000-0001-0000-000000000002\",\"name\":\"vasya\"}," +
                " {\"uuid\":\"00000000-0000-0001-0000-000000000003\",\"name\":\"ivan\"}]";

        List<UserDTO> users = new ArrayList<>();
        users.add(new UserDTO(new UUID(1L, 2L), null, null, "vasya"));
        users.add(new UserDTO(new UUID(1L, 3L), null, null, "ivan"));
        String jsonRequest = "{\"uuid\":[\"00000000-0000-0001-0000-000000000002\",\"00000000-0000-0001-0000-000000000003\"]}";

        when(service.getUsersNameByUuid(any(UsersUuidRequest.class))).thenReturn(users);

        String result = mockMvc.perform(post("/v1/users/nameByUuid")
                .contentType(APPLICATION_JSON_UTF8)
                .content(jsonRequest))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThatJson(result).isEqualTo(expected);
    }
}