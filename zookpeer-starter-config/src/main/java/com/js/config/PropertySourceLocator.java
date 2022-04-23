package com.js.config;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import java.util.Collection;
import java.util.Collections;

public interface PropertySourceLocator {
    PropertySource<?> locate(Environment environment, ConfigurableApplicationContext applicationContext);

    default Collection<PropertySource<?>> locateCollection(Environment environment,
                                                           ConfigurableApplicationContext applicationContext) {
        return locateCollections(this, environment, applicationContext);
    }

    /**
     * 收集属性源列表
     */
    static Collection<PropertySource<?>> locateCollections(PropertySourceLocator locator, Environment environment,
                                                           ConfigurableApplicationContext applicationContext) {
        PropertySource<?> propertySource = locator.locate(environment, applicationContext);
        if (propertySource == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(propertySource);
    }

}
