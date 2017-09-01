package juja.microservices.users.dao.users.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * @author Denis Tantsev (dtantsev@gmail.com)
 * @author Olga Kulykova
 * @author Vadim Dyachenko
 */
@Entity
@Table(name = "USERS")
@Data
@AllArgsConstructor
public class User {

    @Id
    @Column(name = "UUID", unique = true, nullable = false)
    private UUID uuid;

    @NotNull
    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "EMAIL")
    private String email;

    @NotNull
    @Column(name = "GMAIL")
    private String gmail;

    @NotNull
    @Column(name = "SLACK")
    private String slack;

    @Column(name = "SKYPE")
    private String skype;

    public User() {
    }

    public String getFullName() {
        if (this.lastName == null || this.lastName.contains("@") || "".equals(this.lastName)) {
            return this.firstName;
        }
        return this.lastName + " " + this.firstName;
    }

    public String getEmail() {
        if (this.gmail == null) {
            return this.email;
        }
        return this.gmail;
    }
}