package com.js.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Properties;

public class CustomEnvironmentPostProcessor implements EnvironmentPostProcessor {
    private final Properties properties=new Properties();
    private String propertiesFile="custom.properties";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Resource resource=new ClassPathResource(propertiesFile);
        environment.getPropertySources().addLast(loadProperties(resource));
    }

    private PropertySource<?> loadProperties(Resource resource){
        if(!resource.exists()){
            throw new RuntimeException("file not exist");
        }
        try {
            //custom.properties
            properties.load(resource.getInputStream());
            return new PropertiesPropertySource(resource.getFilename(),properties);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
