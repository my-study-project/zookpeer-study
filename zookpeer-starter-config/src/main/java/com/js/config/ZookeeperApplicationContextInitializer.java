package com.js.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ZookeeperApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private final List<PropertySourceLocator> propertySourceLocators;

    public ZookeeperApplicationContextInitializer() {
        ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
        //加载所有的PropertySourceLocator的扩展实现（SPI）
        //ZookeeperPropertySourceLocator
        propertySourceLocators = new ArrayList<>(SpringFactoriesLoader
                .loadFactories(PropertySourceLocator.class, classLoader));
        System.out.println("====");

    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        //去动态加载扩展的配置到Environment中
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        MutablePropertySources mutablePropertySources = environment.getPropertySources();
        for (PropertySourceLocator locator : this.propertySourceLocators) {
            Collection<PropertySource<?>> sources = locator.locateCollection(environment, applicationContext);
            if (sources == null || sources.size() == 0) {
                continue;
            }
            for (PropertySource<?> p : sources) {
                mutablePropertySources.addLast(p);
                //把属性源添加到Environment
            }
        }
    }
}
