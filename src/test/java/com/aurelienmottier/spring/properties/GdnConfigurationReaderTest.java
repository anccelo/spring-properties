package com.aurelienmottier.spring.properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.URL;

import static com.aurelienmottier.spring.properties.GdnConfigurationReader.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GdnConfigurationReaderTest {

    private static final String SCHEME_VALUE = "http";
    private static final String HOST_CONSULTATION_VALUE = "my-unit-test-consultation-domain";
    private static final Integer HOST_PORT_VALUE = 1234;
    private static final String HOST_BASE_VALUE = "/unit-test-document";
    private static final String HOST_GET_DOC_PATH_VALUE = "/unit-test-get";

    private static final String ID_PERSONNE = "1234567";
    private static final String USER_TYPE = "BANK_AGENT";
    private static final String ID_DOC = "DOC123456789";

    @Mock
    private UrlBuilder urlBuilderMock;
    @Mock
    private ConfigurationPropertyReader propertyReaderMock;
    @Mock
    private IllegalStateException exceptionMock;
    @Mock
    private DocumentRequest documentRequestMock;

    @Test
    public void should_throw_an_error_when_the_base_path_property_is_missing_or_undefined() {

        // [Arrange]);
        when(propertyReaderMock.getStringProperty(GDN_PROPERTY_SCHEME)).thenReturn(SCHEME_VALUE);
        when(propertyReaderMock.getStringProperty(GDN_PROPERTY_HOST_CONSULTATION)).thenReturn(HOST_CONSULTATION_VALUE);
        when(propertyReaderMock.getIntegerPropertyOrDefault(GDN_PROPERTY_HOST_PORT, GDN_PROPERTY_HOST_DEFAULT_PORT)).thenReturn(HOST_PORT_VALUE);
        when(propertyReaderMock.getStringProperty(GDN_PROPERTY_HOST_BASE)).thenThrow(exceptionMock);

        // [Act / Assert]
        assertThatThrownBy(() -> new GdnConfigurationReader(urlBuilderMock, propertyReaderMock))
                .isEqualTo(exceptionMock);

    }

    @Test
    public void should_instantiate_the_reader_when_all_properties_are_correctly_defined() throws Exception {

        // [Arrange]
        when(propertyReaderMock.getStringProperty(GDN_PROPERTY_SCHEME)).thenReturn(SCHEME_VALUE);
        when(propertyReaderMock.getStringProperty(GDN_PROPERTY_HOST_CONSULTATION)).thenReturn(HOST_CONSULTATION_VALUE);
        when(propertyReaderMock.getIntegerPropertyOrDefault(GDN_PROPERTY_HOST_PORT, GDN_PROPERTY_HOST_DEFAULT_PORT)).thenReturn(HOST_PORT_VALUE);
        when(propertyReaderMock.getStringProperty(GDN_PROPERTY_HOST_BASE)).thenReturn(HOST_BASE_VALUE);
        when(propertyReaderMock.getStringProperty(GDN_PROPERTY_HOST_GET_DOC_PATH)).thenReturn(HOST_GET_DOC_PATH_VALUE);
        final URL url = new URL(SCHEME_VALUE, HOST_CONSULTATION_VALUE, HOST_PORT_VALUE, HOST_BASE_VALUE + HOST_GET_DOC_PATH_VALUE);
        when(urlBuilderMock.from(SCHEME_VALUE, HOST_CONSULTATION_VALUE, HOST_PORT_VALUE, HOST_BASE_VALUE + HOST_GET_DOC_PATH_VALUE)).thenReturn(url);

        // [Act]
        final GdnConfigurationReader actual = new GdnConfigurationReader(urlBuilderMock, propertyReaderMock);

        // [Assert]
        assertThat(actual)
                .isNotNull()
                .hasFieldOrPropertyWithValue("resource", url.toString());

    }

    @Test
    public void should_format_the_document_url_when_the_document_request_properties_are_valid() throws Exception {

        // [Arrange]
        when(propertyReaderMock.getStringProperty(GDN_PROPERTY_SCHEME)).thenReturn(SCHEME_VALUE);
        when(propertyReaderMock.getStringProperty(GDN_PROPERTY_HOST_CONSULTATION)).thenReturn(HOST_CONSULTATION_VALUE);
        when(propertyReaderMock.getIntegerPropertyOrDefault(GDN_PROPERTY_HOST_PORT, GDN_PROPERTY_HOST_DEFAULT_PORT)).thenReturn(HOST_PORT_VALUE);
        when(propertyReaderMock.getStringProperty(GDN_PROPERTY_HOST_BASE)).thenReturn(HOST_BASE_VALUE);
        when(propertyReaderMock.getStringProperty(GDN_PROPERTY_HOST_GET_DOC_PATH)).thenReturn(HOST_GET_DOC_PATH_VALUE);
        final URL url = new URL(SCHEME_VALUE, HOST_CONSULTATION_VALUE, HOST_PORT_VALUE, HOST_BASE_VALUE + HOST_GET_DOC_PATH_VALUE);
        when(urlBuilderMock.from(SCHEME_VALUE, HOST_CONSULTATION_VALUE, HOST_PORT_VALUE, HOST_BASE_VALUE + HOST_GET_DOC_PATH_VALUE)).thenReturn(url);
        final GdnConfigurationReader reader = new GdnConfigurationReader(urlBuilderMock, propertyReaderMock);
        when(documentRequestMock.getIdPersonne()).thenReturn(ID_PERSONNE);
        when(documentRequestMock.getUserType()).thenReturn(USER_TYPE);
        when(documentRequestMock.getIdDoc()).thenReturn(ID_DOC);

        // [Act]
        final String actual = reader.completeUrlUsing(documentRequestMock);

        // [Assert]
        final String expected = "http://my-unit-test-consultation-domain:1234/unit-test-document/unit-test-get?user=1234567&user-type=BANK_AGENT&document=DOC123456789";
        assertThat(actual).isEqualTo(expected);

    }

}