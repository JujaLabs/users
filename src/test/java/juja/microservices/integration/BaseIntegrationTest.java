package juja.microservices.integration;

import juja.microservices.users.Users;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;

@SpringBootTest(classes = {Users.class})
public class BaseIntegrationTest {

    @Inject
    protected WebApplicationContext webApplicationContext;
}
