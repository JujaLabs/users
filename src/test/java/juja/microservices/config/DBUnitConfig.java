package juja.microservices.config;

import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DBUnitConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource userDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.crm")
    public DataSource crmDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public DatabaseConfigBean dbUnitDatabaseConfig() {
        DatabaseConfigBean dbConfig = new DatabaseConfigBean();
        dbConfig.setDatatypeFactory(new H2DataTypeFactory());
        return dbConfig;
    }

    @Bean(name = "usersConnection")
    public DatabaseDataSourceConnectionFactoryBean userDbUnitDatabaseConnection() {
        DatabaseDataSourceConnectionFactoryBean dbConnection = new DatabaseDataSourceConnectionFactoryBean(userDataSource());
        dbConnection.setDatabaseConfig(dbUnitDatabaseConfig());
        dbConnection.setSchema("PUBLIC");
        return dbConnection;
    }

    @Bean(name = "crmConnection")
    public DatabaseDataSourceConnectionFactoryBean crmDbUnitDatabaseConnection() {
        DatabaseDataSourceConnectionFactoryBean dbConnection = new DatabaseDataSourceConnectionFactoryBean(crmDataSource());
        dbConnection.setDatabaseConfig(dbUnitDatabaseConfig());
        dbConnection.setSchema("PUBLIC");
        return dbConnection;
    }
}
