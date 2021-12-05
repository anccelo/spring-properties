package com.aurelienmottier.spring.properties;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationPropertyReader {

    private final Environment environment;

    public ConfigurationPropertyReader(final Environment environment) {
        this.environment = environment;
    }

    protected String getStringProperty(final String property) {
        return this.environment.getRequiredProperty(property, String.class);
    }

    protected Integer getIntegerPropertyOrDefault(final String property, final Integer defaultValue) {
        return this.environment.getProperty(property, Integer.class, defaultValue);
    }

}