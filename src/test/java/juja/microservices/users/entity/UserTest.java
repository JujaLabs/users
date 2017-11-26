package juja.microservices.users.entity;

import juja.microservices.users.dao.users.domain.User;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class UserTest {

    @Test
    public void shouldReturnUser() {
        UUID expectedUuid = UUID.fromString("00000000-0000-0001-0000-000000000002");
        String expectedFirstName = "firstName";
        String expectedLastName = "lastName";
        String expectedSlack = "slack";
        String expectedSkype = "skype";

        User user = new User(new UUID(1L, 2L), "firstName", "lastName", "slack", "slackId", "skype", 777L);

        assertNotNull(user);
        assertEquals(expectedUuid, user.getUuid());
        assertEquals(expectedFirstName, user.getFirstName());
        assertEquals(expectedLastName, user.getLastName());
        assertEquals(expectedSlack, user.getSlack());
        assertEquals(expectedSkype, user.getSkype());
    }

    @Test
    public void getFullNameTest() {
        String expectedFullName = "lastName firstName";

        User user = new User(new UUID(1L, 2L), "firstName", "lastName", "slack", "slackId", "skype", 777L);
        assertEquals(expectedFullName, user.getFullName());
    }

    @Test
    public void getFullNameWithNullLastNameTest() {
        String expectedFullName = "firstName";

        User user = new User(new UUID(1L, 2L), "firstName", null, "slack", "slackId", "skype", 777L);
        assertEquals(expectedFullName, user.getFullName());
    }

    @Test
    public void getFullNameWithEmptyLastNameTest() {
        String expectedFullName = "firstName";

        User user = new User(new UUID(1L, 2L), "firstName", "", "slack", "slackId", "skype", 777L);
        assertEquals(expectedFullName, user.getFullName());
    }

    @Test
    public void getFullNameWithEmailInLastNameTest() {
        String expectedFullName = "firstName";

        User user = new User(new UUID(1L, 2L), "firstName", "user@mail.com", "slack", "slackId", "skype", 777L);
        assertEquals(expectedFullName, user.getFullName());
    }
}