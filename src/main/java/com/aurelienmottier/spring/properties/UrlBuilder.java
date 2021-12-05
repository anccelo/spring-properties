package com.aurelienmottier.spring.properties;

import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

import static java.lang.String.format;

@Component
public class UrlBuilder {

    static final String ERROR_MESSAGE_INPUTS = "An error occurred when building an URL from the following inputs : %s, %s, %s and %s";

    public URL from(final String protocol, final String host, final Integer port, final String file) {
        try {
            return new URL(protocol, host, port, file);
        } catch (final MalformedURLException exception) {
            final String inputs = format(ERROR_MESSAGE_INPUTS, protocol, host, port, file);
            throw new IllegalArgumentException("An error occurred when building an URL from the following inputs : " + inputs, exception);
        }
    }

}