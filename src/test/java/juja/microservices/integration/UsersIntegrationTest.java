package juja.microservices.integration;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import juja.microservices.config.DBUnitConfig;
import juja.microservices.users.dao.crm.repository.CRMRepository;
import juja.microservices.users.dao.users.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.inject.Inject;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.http.MediaType.APPLICATION_PDF;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@Import(DBUnitConfig.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class})
@DbUnitConfiguration(databaseConnection = {"usersConnection", "crmConnection"})
public class UsersIntegrationTest extends BaseIntegrationTest {

    private static final String USERS_URL = "/v1/users";
    private static final String USERS_BY_UUIDS_URL = "/v1/users/usersByUuids";
    private static final String USERS_BY_SLACK_NAMES_URL = "/v1/users/usersBySlackNames";
    private static final String USERS_UPDATE_URL = "/v1/users/update";
    private static final String Fake_URL = "/fake";

    private MockMvc mockMvc;

    @Inject
    private UserRepository repository;

    @Inject
    private CRMRepository crmRepository;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DatabaseSetup(value = "/datasets/usersData.xml")
    public void getAllUsers() throws Exception {
        //given
        String expected = "[" +
                "{\"uuid\":\"00000000-0000-0001-0000-000000000002\",\"name\":\"Batman Alex\", \"skype\":\"Alex\",\"slack\":\"alex.batman\"}," +
                "{\"uuid\":\"00000000-0000-0001-0000-000000000003\",\"name\":\"Superman Max\", \"skype\":\"Max\",\"slack\":\"max.superman\"}" +
                "]";

        //when
        String result = mockMvc.perform(get(USERS_URL)
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        //then
        assertThatJson(result).isEqualTo(expected);
    }

    @Test
    @DatabaseSetup(value = "/datasets/usersData.xml")
    public void getUsersByUuids() throws Exception {
        //given
        String jsonRequest = "{\"uuids\":[\"00000000-0000-0001-0000-000000000003\"]}";
        String expected = "[{\"uuid\":\"00000000-0000-0001-0000-000000000003\",\"name\":\"Superman Max\", \"skype\":\"Max\",\"slack\":\"max.superman\"}]";

        //when
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
    @DatabaseSetup(value = "/datasets/usersData.xml")
    public void getUsersBySlackNames() throws Exception {
        //given
        String jsonRequest = "{\"slackNames\":[\"alex.batman\"]}";
        String expected = "[{\"uuid\":\"00000000-0000-0001-0000-000000000002\",\"name\":\"Batman Alex\", \"skype\":\"Alex\",\"slack\":\"alex.batman\"}]";

        //when
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
    @DatabaseSetup(value = "/datasets/usersData.xml")
    @DatabaseSetup(connection = "crmConnection", value = "/datasets/crmData.xml")
    @ExpectedDatabase(value = "/datasets/usersDataAfterUpdate.xml")
    public void updateUsersDatabaseFromCRM() throws Exception {
        //when
        mockMvc.perform(post(USERS_UPDATE_URL)
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
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