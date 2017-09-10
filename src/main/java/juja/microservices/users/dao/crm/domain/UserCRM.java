package juja.microservices.users.dao.crm.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Vadim Dyachenko
 */
@Entity
@Table(name = "X2_CONTACT")
@Data
@AllArgsConstructor
public class UserCRM {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @Column(name = "FIRST_NAME")
    String firstName;
    @Column(name = "LAST_NAME")
    String lastName;
    @Column(name = "SKYPE")
    String skype;
    @Column(name = "LASTUPDATED")
    long lastUpdated;
    @Column(name = "C_SLACK")
    String slack;
    @Column(name = "C_ISSTUDENT")
    String isStudent;
    @Column(name = "C_UUID")
    String uuid;

    public UserCRM(){

    }
}

/* CRM x2_contact structure
+--------------------+------------------+------+-----+---------+----------------+
| Field              | Type             | Null | Key | Default | Extra          |
+--------------------+------------------+------+-----+---------+----------------+
| id                 | int(10) unsigned | NO   | PRI | NULL    | auto_increment |
| name               | varchar(255)     | YES  |     | NULL    |                |
| firstName          | varchar(255)     | NO   |     | NULL    |                |
| lastName           | varchar(255)     | NO   |     | NULL    |                |
| skype              | varchar(32)      | YES  |     | NULL    |                |
| lastUpdated        | bigint(20)       | YES  |     | NULL    |                |
| c_gmail            | varchar(255)     | YES  |     | NULL    |                |
| c_slack            | varchar(255)     | YES  |     | NULL    |                |
| c_isStudent        | tinyint(1)       | NO   |     | 0       |                |
| c_uuid             | varchar(255)     | YES  |     | NULL    |                |
*/