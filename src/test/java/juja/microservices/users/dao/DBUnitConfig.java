package juja.microservices.users.dao;

import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.h2.jdbcx.JdbcDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import javax.sql.DataSource;

/**
 * @author Vadim Dyachenko
 * This class is required for configuration DatatypeFactory to H2DataTypeFactory
 * https://stackoverflow.com/questions/3942965/dbunit-warning-abstracttablemetadata
 */
@TestConfiguration
public class DBUnitConfig {
//    @Inject
//    private DataSource dataSource;
//
//    @Bean
//    public DatabaseDataSourceConnectionFactoryBean dbUnitDatabaseConnection() {
//        DatabaseConfigBean bean = new DatabaseConfigBean();
//        bean.setDatatypeFactory(new H2DataTypeFactory());
//
//        DatabaseDataSourceConnectionFactoryBean dbConnectionFactory = new DatabaseDataSourceConnectionFactoryBean(dataSource);
//        dbConnectionFactory.setDatabaseConfig(bean);
//        return dbConnectionFactory;
//    }

//    @Bean
//    public DatabaseConfigBean dbUnitDatabaseConfig() {
//        final DatabaseConfigBean bean = new DatabaseConfigBean();
//        bean.setDatatypeFactory(new H2DataTypeFactory());
//        bean.setEscapePattern("`");
//        return bean;
//    }
//
//    @Bean
//    public DatabaseDataSourceConnectionFactoryBean dbUnitDatabaseConnection(
//            DataSource dataSource) {
//        final DatabaseDataSourceConnectionFactoryBean bean =
//                new DatabaseDataSourceConnectionFactoryBean(dataSource);
//        bean.setDatabaseConfig(dbUnitDatabaseConfig());
//        bean.setSchema("PUBLIC");
//        return bean;
//    }


    @Bean(name = "dataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "crmDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.crm")
    public DataSource crmDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public DatabaseDataSourceConnectionFactoryBean dbUnitDatabaseConnection() {
        DatabaseConfigBean bean = new DatabaseConfigBean();
        bean.setDatatypeFactory(new H2DataTypeFactory());

        DatabaseDataSourceConnectionFactoryBean dbConnectionFactory = new DatabaseDataSourceConnectionFactoryBean(dataSource());
        dbConnectionFactory.setDatabaseConfig(bean);
        dbConnectionFactory.setSchema("PUBLIC");
        return dbConnectionFactory;
    }
}