package juja.microservices.users.entity;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {

    @Test
    public void shouldReturnUser() {
        String expectedUuid = "uuid";
        String expectedFirstName = "firstName";
        String expectedLastName = "lastName";
        String expectedGmail = "gmail";
        String expectedSlack = "slack";
        String expectedSkype = "skype";


        User user = new User("uuid", "firstName", "lastName", "email", "gmail",
                "slack", "skype");

        assertNotNull(user);
        assertEquals(expectedUuid, user.getUuid());
        assertEquals(expectedFirstName, user.getFirstName());
        assertEquals(expectedLastName, user.getLastName());
        assertEquals(expectedGmail, user.getEmail());
        assertEquals(expectedSlack, user.getSlack());
        assertEquals(expectedSkype, user.getSkype());
    }


    @Test
    public void shouldReturnEmailWhenGmailIsNull(){

        String expectedEmail = "email";
        User user = new User("uuid", "firstName", "lastName", expectedEmail, null,
                "slack", "skype");

        assertEquals(expectedEmail, user.getEmail());
    }

    @Test
    public void getFullNameTest() {
        String expectedFullName = "lastName firstName";
        User user = new User("uuid", "firstName", "lastName", "email", "gmail",
                "slack", "skype");
        assertEquals(expectedFullName, user.getFullName());
    }

    @Test
    public void getFullNameWithNullLastNameTest() {
        String expectedFullName = "firstName";
        User user = new User("uuid", "firstName", null, "email", "gmail",
                "slack", "skype");
        assertEquals(expectedFullName, user.getFullName());
    }

    @Test
    public void getFullNameWithEmptyLastNameTest() {
        String expectedFullName = "firstName";
        User user = new User("uuid", "firstName", "", "email", "gmail",
                "slack", "skype");
        assertEquals(expectedFullName, user.getFullName());
    }

    @Test
    public void getFullNameWithEmailInLastNameTest() {
        String expectedFullName = "firstName";
        User user = new User("uuid", "firstName", "user@mail.com", "email", "gmail",
                "slack", "skype");
        assertEquals(expectedFullName, user.getFullName());
    }
}