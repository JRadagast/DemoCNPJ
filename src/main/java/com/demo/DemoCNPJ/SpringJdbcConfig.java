package com.demo.DemoCNPJ;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Configuration
public class SpringJdbcConfig {

    public Environment environment;

    @Autowired
    public SpringJdbcConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    @ConfigurationProperties(prefix = "datasource")
    public DataSource postgresDataSource() {

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(environment.getProperty("datasource.driver"));
        dataSource.setUrl(environment.getProperty("datasource.url"));
        dataSource.setUsername(environment.getProperty("datasource.username"));
        dataSource.setPassword(environment.getProperty("datasource.password"));

        dataSource.setSchema("public");

        System.out.println(environment.getProperty("datasource.driver"));

        return dataSource;
    }
}
