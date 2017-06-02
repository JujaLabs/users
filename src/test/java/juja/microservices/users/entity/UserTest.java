package juja.microservices.users.entity;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {

    @Test
    public void shouldReturnUser() {
        String expectedUuid = "uuid";
        String expectedFirstName = "firstName";
        String expectedLastName = "lastName";
        String expectedEmail = "email";
        String expectedGmail = "gmail";
        String expectedSlack = "slack";
        String expectedSkype = "skype";
        String expectedLinkedin = "linkedin";
        String expectedFacebook = "facebook";
        String expectedTwitter = "twitter";

        User user = new User("uuid", "firstName", "lastName", "email", "gmail",
                "slack", "skype", "linkedin", "facebook", "twitter");

        assertNotNull(user);
        assertEquals(expectedUuid, user.getUuid());
        assertEquals(expectedFirstName, user.getFirstName());
        assertEquals(expectedLastName, user.getLastName());
        assertEquals(expectedEmail, user.getEmail());
        assertEquals(expectedGmail, user.getGmail());
        assertEquals(expectedSlack, user.getSlack());
        assertEquals(expectedSkype, user.getSkype());
        assertEquals(expectedLinkedin, user.getLinkedin());
        assertEquals(expectedFacebook, user.getFacebook());
        assertEquals(expectedTwitter, user.getTwitter());
    }

    @Test
    public void getFullNameTest() {
        String expectedFullName = "lastName firstName";
        User user = new User("uuid", "firstName", "lastName", "email", "gmail",
                "slack", "skype", "linkedin", "facebook", "twitter");
        assertEquals(expectedFullName, user.getFullName());
    }

    @Test
    public void getFullNameWithNullLastNameTest() {
        String expectedFullName = "firstName";
        User user = new User("uuid", "firstName", null, "email", "gmail",
                "slack", "skype", "linkedin", "facebook", "twitter");
        assertEquals(expectedFullName, user.getFullName());
    }

    @Test
    public void getFullNameWithEmptyLastNameTest() {
        String expectedFullName = "firstName";
        User user = new User("uuid", "firstName", "", "email", "gmail",
                "slack", "skype", "linkedin", "facebook", "twitter");
        assertEquals(expectedFullName, user.getFullName());
    }

    @Test
    public void getFullNameWithEmailInLastNameTest() {
        String expectedFullName = "firstName";
        User user = new User("uuid", "firstName", "user@mail.com", "email", "gmail",
                "slack", "skype", "linkedin", "facebook", "twitter");
        assertEquals(expectedFullName, user.getFullName());
    }
}