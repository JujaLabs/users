package juja.microservices.users.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * @author Vadim Dyachenko
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "crmEntityManagerFactory",
        transactionManagerRef = "crmTransactionManager",
        basePackages = {"juja.microservices.users.dao.crm.repository"}
)
public class CRMDatasourceConfiguration {
    @Bean(name = "crmDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.crm")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "crmEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean
    crmEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("crmDataSource") DataSource dataSource
    ) {
        return
                builder
                        .dataSource(dataSource)
                        .packages("juja.microservices.users.dao.crm.domain")
                        .persistenceUnit("crmUnit")
                        .build();
    }
    @Bean(name = "crmTransactionManager")
    public PlatformTransactionManager crmTransactionManager(
            @Qualifier("crmEntityManagerFactory") EntityManagerFactory
                    crmEntityManagerFactory
    ) {
        return new JpaTransactionManager(crmEntityManagerFactory);
    }
}
