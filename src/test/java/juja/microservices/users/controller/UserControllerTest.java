package juja.microservices.users.controller;

import juja.microservices.users.entity.User;
import juja.microservices.users.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;

/**
 * @author Denis Tantsev (dtantsev@gmail.com)
 */

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    private static final String ALL_USERS = "[{\"uuid\":\"AAAA123\",\"firstName\":\"Vasya\",\"lastName\":\"Ivanoff\"," +
            "\"email\":\"vasya@mail.ru\",\"gmail\":\"vasya@gmail.com\",\"slack\":\"vasya\",\"skype\":\"vasya.ivanoff\"," +
            "\"linkedin\":\"linkedin/vasya\",\"facebook\":\"facebook/vasya\",\"twitter\":\"twitter/vasya\"}]";

    private static final String vasyaUser = "[{\"uuid\":\"AAAA123\",\"firstName\":\"Vasya\",\"lastName\":\"Ivanoff\"," +
            "\"email\":\"vasya@mail.ru\",\"gmail\":\"vasya@gmail.com\",\"slack\":\"vasya\",\"skype\":\"vasya.ivanoff\"," +
            "\"linkedin\":\"linkedin/vasya\",\"facebook\":\"facebook/vasya\",\"twitter\":\"twitter/vasya\"}]";

    @Inject
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @Test
    public void getAllUsers() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(new User("AAAA123", "Vasya", "Ivanoff", "vasya@mail.ru", "vasya@gmail.com", "vasya", "vasya.ivanoff",
                "linkedin/vasya", "facebook/vasya", "twitter/vasya"));
        when(service.getAllUsers()).thenReturn(users);
        String result = mockMvc.perform(get("/users")
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThatJson(result).isEqualTo(ALL_USERS);
    }

    @Test
    public void searchUserByEmailTest() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(new User("AAAA123", "Vasya", "Ivanoff", "vasya@mail.ru", "vasya@gmail.com", "vasya", "vasya.ivanoff",
                "linkedin/vasya", "facebook/vasya", "twitter/vasya"));
        Map params = new HashMap<String,String>();
        params.put("email","vasya@mail.ru");
        when(service.searchUser(params)).thenReturn(users);
        String result = mockMvc.perform(get("/users/search")
                .param("email","vasya@mail.ru")
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThatJson(result).isEqualTo(vasyaUser);

    }
}