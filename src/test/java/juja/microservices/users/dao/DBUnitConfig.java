package juja.microservices.users.dao;

import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;
import juja.microservices.users.dao.crm.domain.UserCRM;
import juja.microservices.users.dao.users.domain.User;
import org.dbunit.DatabaseUnitException;
import org.dbunit.ext.h2.H2Connection;
import org.dbunit.ext.h2.H2DataTypeFactory;
//import org.h2.jdbcx.JdbcDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author Vadim Dyachenko
 * This class is required for configuration DatatypeFactory to H2DataTypeFactory
 * https://stackoverflow.com/questions/3942965/dbunit-warning-abstracttablemetadata
 */
@Configuration
public class DBUnitConfig {

//
//    @Inject
//    private Environment environment;

//    @Bean(name = "dataSource")
//    public DataSource dataSource() {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName(environment.getProperty("spring.datasource.driver-class-name"));
//        dataSource.setUrl(environment.getProperty("jdbc:h2:mem:users;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"));
//        dataSource.setUsername(environment.getProperty("spring.datasource.username"));
//        dataSource.setPassword(environment.getProperty("spring.datasource.password"));
//        return dataSource;
//    }

//    @Bean
//    public H2Connection h2Connection() throws SQLException, DatabaseUnitException {
//        return new H2Connection(dataSource().getConnection(), "PUBLIC");
//    }
//
//    @Bean
//    public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
//        return new JpaTransactionManager(emf);
//    }

//    @Bean
//    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
//        return new PersistenceExceptionTranslationPostProcessor();
//    }

//    @Bean(name = "dataSource")
//    @Primary
//    @ConfigurationProperties("app.datasource")
//    public DataSource dataSource() {
//        return DataSourceBuilder.create().build();
//    }

//    @Bean(name = "crmDataSource")
//    @ConfigurationProperties(prefix = "spring.datasource.crm")
//    public DataSource crmDataSource() {
//        return DataSourceBuilder.create().build();
//    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties userDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "userDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource userDataSource() {
        return userDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.crm")
    public DataSourceProperties crmDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "crmDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.crm")
    public DataSource crmDataSource() {
        return crmDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean userEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(userDataSource())
                .packages(User.class)
                .persistenceUnit("userUnit")
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean crmEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(crmDataSource())
                .packages(UserCRM.class)
                .persistenceUnit("crmUnit")
                .build();
    }

/*Рабочий вариант для одной базы
    @Inject
    private DataSource dataSource;

    @Bean
    public DatabaseDataSourceConnectionFactoryBean dbUnitDatabaseConnection() {
        DatabaseConfigBean bean = new DatabaseConfigBean();
        bean.setDatatypeFactory(new H2DataTypeFactory());

        DatabaseDataSourceConnectionFactoryBean dbConnectionFactory = new DatabaseDataSourceConnectionFactoryBean(dataSource());
        dbConnectionFactory.setDatabaseConfig(bean);
        dbConnectionFactory.setSchema("PUBLIC");
        return dbConnectionFactory;
    }*/
}
