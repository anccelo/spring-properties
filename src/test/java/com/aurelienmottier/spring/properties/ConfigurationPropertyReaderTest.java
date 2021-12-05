package com.aurelienmottier.spring.properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationPropertyReaderTest {

    private static final String PROPERTY_NAME = "GDN_PROPERTY_XXX";
    private static final String PROPERTY_VALUE = "IT-WORKS";

    @Mock
    private Environment environmentMock;

    @InjectMocks
    private ConfigurationPropertyReader reader;

    @Test
    public void should_return_the_string_value_when_the_property_exists() {

        // [Arrange]
        when(environmentMock.getRequiredProperty(PROPERTY_NAME, String.class)).thenReturn(PROPERTY_VALUE);

        // [Act]
        final String actual = reader.getStringProperty(PROPERTY_NAME);

        // [Assert]
        assertThat(actual).isEqualTo(PROPERTY_VALUE);

    }


}