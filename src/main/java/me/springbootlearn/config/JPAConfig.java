package me.springbootlearn.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import me.springbootlearn.CipherTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.*;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by reimi on 11/18/17.
 */
@Configuration
@EnableJpaRepositories(basePackages = "me.springbootlearn.repository")
@EnableTransactionManagement
public class JPAConfig {

    static Logger LOGGER = LoggerFactory.getLogger(JPAConfig.class);

    public static Properties loadProperties(String path) {
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = contextLoader.getResourceAsStream(path);
        Properties properties = new Properties();
        try {
            properties.load(input);
        } catch (IOException e) {
            LOGGER.warn("failed to load properties by path {}", path);
        }
        return properties;
    }

    @Bean(destroyMethod = "close")
    public DataSource c3p0Datasource(@Value("#{systemProperties['dbaccess_pass'] ?: null}") String dbAccessPass) {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        Properties prop = loadProperties("META-INF/c3p0.properties");
        Enumeration<Object> enumeration = prop.keys();
        DefaultConversionService converter = new DefaultConversionService();
        while(enumeration.hasMoreElements()) {
            String key = enumeration.nextElement().toString();
            String propName = key.replace("c3p0.", "");
            String setterName = "set" + StringUtils.capitalize(propName);
            Method method = BeanUtils.findMethodWithMinimalParameters(ComboPooledDataSource.class, setterName);
            Class<?> targetClass = method.getParameterTypes()[0];
            Object val = converter.convert(prop.get(key), targetClass);
            try {
                method.invoke(dataSource, val);
            } catch (Exception e) {
                LOGGER.warn("failed to set property {}.", key);
            }
        }
        String cipher = dataSource.getPassword();
        char[] pass = null;
        if(dbAccessPass == null)
            pass = CipherTools.showPasswordInputPane();
        else
            pass = dbAccessPass.toCharArray();
        String realPass = CipherTools.decryptByPassword(cipher, pass);
        dataSource.setPassword(realPass);
        return dataSource;
    }


    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Value("project-pu") String unitName, DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setPackagesToScan("me.springbootlearn.eneity");
        factoryBean.setDataSource(dataSource);
        factoryBean.setJpaDialect(new HibernateJpaDialect());
        factoryBean.setPersistenceUnitName(unitName);
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(true);
        factoryBean.setJpaVendorAdapter(vendorAdapter);
        factoryBean.setJpaProperties(additionalHibernateProperties());
        return factoryBean;
    }

    public Properties additionalHibernateProperties() {
        Properties properties = new Properties();
        String path = Paths.get(System.getProperty("user.dir"), "lucene_index").toString();
        properties.setProperty("hibernate.search.default.directory_provider", "filesystem");
        properties.setProperty("hibernate.search.default.indexBase", path + "/");
        LOGGER.info("LUCENE BASE PATH: {}", path);
        return properties;
    }

    @Bean
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean emf) {
        PlatformTransactionManager manager = new JpaTransactionManager(emf.getObject());
        return manager;
    }


}
