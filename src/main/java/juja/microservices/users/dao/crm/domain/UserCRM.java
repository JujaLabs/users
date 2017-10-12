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
 * @author Danil Kuznetsov kuznetsov.danil.v@gmail.com
 */
@Entity
@Table(name = "\"x2_contacts\"")
@Data
@AllArgsConstructor
public class UserCRM {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(name = "\"firstName\"")
    String firstName;

    @Column(name = "\"lastName\"")
    String lastName;

    @Column(name = "\"skype\"")
    String skype;

    @Column(name = "\"lastUpdated\"")
    long lastUpdated;

    @Column(name = "\"c_slack\"")
    String slack;

    @Column(name = "\"c_isStudent\"")
    int isStudent;

    @Column(name = "\"c_uuid\"")
    String uuid;

    public UserCRM() {

    }
}

/* CRM x2_contacts structure
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