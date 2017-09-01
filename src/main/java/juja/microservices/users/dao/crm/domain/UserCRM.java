package juja.microservices.users.dao.crm.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Vadim Dyachenko
 */
@Entity
@Table(name = "x2_contact")
@Data
public class UserCRM {
    @Id
    Long id;
    @Column(name = "FIRST_NAME")
    String firstName;
    @Column(name = "LAST_NAME")
    String lastName;
    @Column(name = "EMAIL")
    String email;
    @Column(name = "SKYPE")
    String skype;
    @Column(name = "LASTUPDATED")
    long lastUpdated;
    @Column(name = "C_GMAIL")
    String gmail;
    @Column(name = "C_SLACK")
    String slack;
    @Column(name = "C_UUID")
    String uuid;

    public UserCRM(){

    }
}