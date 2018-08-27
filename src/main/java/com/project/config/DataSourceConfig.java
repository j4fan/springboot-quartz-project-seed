package com.project.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.logging.log4j2.Log4j2Impl;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Log4j2
@Configuration
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari-read")
    public HikariDataSource hikariReadOnlyDataSource() {
        log.info("hikari read-only datasource start to init...");
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.qrtz")
    public HikariDataSource qrtzDataSource() {
        log.info("qrtz datasource start to init...");
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Primary
    @Bean(name = "jdbcTemplateReadonly")
    public JdbcTemplate jdbcTemplateReadonly(@Qualifier("hikariReadOnlyDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari-write")
    public HikariDataSource hikariReadWriteDataSource() {
        log.info("hikari read-write datasource start to init...");
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean(name = "jdbcTemplateReadWrite")
    public JdbcTemplate jdbcTemplateReadWrite(@Qualifier("hikariReadWriteDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean("prodTransactionManager")
    public PlatformTransactionManager prodTransactionManager(@Qualifier("hikariReadWriteDataSource") DataSource ds) {
        return new DataSourceTransactionManager(ds);
    }

    @Bean
    public SqlSessionFactory sqlSessionFactoryBean(@Qualifier("hikariReadWriteDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean ss = new SqlSessionFactoryBean();
        ss.setDataSource(dataSource);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setLazyLoadingEnabled(false);
        configuration.setCacheEnabled(false);
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setLogImpl(Log4j2Impl.class);
        configuration.setCallSettersOnNulls(true);
        ss.setConfiguration(configuration);
        ss.setMapperLocations(resolver.getResources("classpath*:/mapper/*.xml"));
        return ss.getObject();
    }

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactoryBean");
        mapperScannerConfigurer.setBasePackage("com.project.mapper");
        return mapperScannerConfigurer;
    }
}

