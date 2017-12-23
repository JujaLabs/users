package juja.microservices.users.dao.users.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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
    @NonNull
    @Column(name = "UUID", unique = true, nullable = false)
    private UUID uuid;

    @NonNull
    @Column(name = "FIRST_NAME", nullable = false)
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @NonNull
    @Column(name = "SLACK", nullable = false)
    private String slack;

    @NonNull
    @Column(name = "SLACK_ID", nullable = false)
    private String slackId;

    @Column(name = "SKYPE")
    private String skype;

    @Column(name = "LASTUPDATED")
    private Long lastUpdated;

    public User() {
    }

    public String getFullName() {
        if (this.lastName == null || this.lastName.contains("@") || "".equals(this.lastName)) {
            return this.firstName;
        }
        return this.lastName + " " + this.firstName;
    }
}