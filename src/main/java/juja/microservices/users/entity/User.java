package juja.microservices.users.entity;

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
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID uuid;

    @NotNull
    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @NotNull
    @Column(name = "gmail")
    private String gmail;

    @NotNull
    @Column(name = "slack")
    private String slack;

    @Column(name = "skype")
    private String skype;

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