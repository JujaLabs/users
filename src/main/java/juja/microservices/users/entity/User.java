package juja.microservices.users.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Denis Tantsev (dtantsev@gmail.com)
 * @author Olga Kulykova
 */

@Data
@AllArgsConstructor
public class User {

    private String uuid;
    private String firstName;
    private String lastName;
    private String email;
    private String gmail;
    private String slack;
    private String skype;
    private String linkedin;
    private String facebook;
    private String twitter;

    public String getFullName() {
        if (this.lastName == null || this.lastName.contains("@") || "".equals(this.lastName)) {
            return this.firstName;
        }
        return this.lastName + " " + this.firstName;
    }
}
