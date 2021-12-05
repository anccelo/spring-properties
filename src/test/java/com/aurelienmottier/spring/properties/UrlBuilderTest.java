package com.aurelienmottier.spring.properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.MalformedURLException;
import java.net.URL;

import static com.aurelienmottier.spring.properties.UrlBuilder.ERROR_MESSAGE_INPUTS;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(MockitoJUnitRunner.class)
public class UrlBuilderTest {

    private static final String PROTOCOL = "http";
    private static final String HOST = "my-unit-test-consultation-domain";
    private static final Integer PORT = 1234;
    private static final Integer INVALID_PORT = -9876;
    private static final String PATH = "/unit-test-document/unit-test-get";
    private static final String QUERY = "user=1234567&user-type=BANK_AGENT&document=DOC123456789";
    private static final String RESOURCE = PATH + "?" + QUERY;

    @InjectMocks
    private UrlBuilder builder;

    @Test
    public void should_throw_an_error_when_the_port_is_invalid() {

        // [Arrange]
        final String expectedInputs = format(ERROR_MESSAGE_INPUTS, PROTOCOL, HOST, INVALID_PORT, RESOURCE);

        // [Act / Assert]
        assertThatThrownBy(() -> this.builder.from(PROTOCOL, HOST, INVALID_PORT, RESOURCE))
                .hasRootCauseInstanceOf(MalformedURLException.class)
                .hasRootCauseMessage("Invalid port number :" + INVALID_PORT)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("An error occurred when building an URL from the following inputs : " + expectedInputs);

    }

    @Test
    public void should_build_the_url_when_all_inputs_are_valid() {

        // [Act]
        final URL actual = this.builder.from(PROTOCOL, HOST, PORT, RESOURCE);

        // [Assert]
        assertThat(actual).isNotNull()
                .hasProtocol(PROTOCOL)
                .hasHost(HOST)
                .hasPort(PORT)
                .hasPath(PATH)
                .hasQuery(QUERY);

    }


}