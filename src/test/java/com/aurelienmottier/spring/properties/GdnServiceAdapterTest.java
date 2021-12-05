package com.aurelienmottier.spring.properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static com.aurelienmottier.spring.properties.GdnServiceAdapter.ERROR_MESSAGE_DOCUMENT_RETRIEVAL;
import static com.aurelienmottier.spring.properties.RetrieveListDocReturnCode.ERREUR_TECHNIQUE;
import static com.aurelienmottier.spring.properties.RetrieveListDocReturnCode.TRAITEMENT_CORRECT;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GdnServiceAdapterTest {

    private static final String PDF_FILENAME = "my-unit-test.pdf";
    private static final byte[] PDF_CONTENT = "UNIT TESTING".getBytes(UTF_8);
    private static final String GDN_DOCUMENT_URL = "http://my-unit-test-consultation-domain:1234/unit-test-document/unit-test-get?user=1234567&user-type=BANK_AGENT&document=DOC123456789";
    private static final String REST_CLIENT_ERROR_MESSAGE = "Something went bad when calling GDN through HTTP.";

    @Mock
    private DocumentRequest documentRequestMock;
    @Mock
    private Document documentMock;
    @Mock
    private ResponseEntity<byte[]> responseMock;

    @Mock
    private RestClientException exceptionMock;

    @Mock
    private GdnConfigurationReader gdnConfigurationReaderMock;
    @Mock
    private RestTemplate gdnRestTemplateMock;
    @Mock
    private ZosServiceAdapter zosServiceAdapterMock;

    @InjectMocks
    private GdnServiceAdapter service;

    @Test
    public void should_return_the_document_from_zos_when_the_document_requires_some_checks() {

        // [Arrange]
        when(documentRequestMock.shouldBeChecked()).thenReturn(true);
        when(zosServiceAdapterMock.checkParameterIntoZos(documentRequestMock)).thenReturn(documentMock);

        // [Act]
        final Document actual = service.retrieve(documentRequestMock);

        // [Assert]
        assertThat(actual).isEqualTo(documentMock);

    }

    @Test
    public void should_return_a_document_with_an_embedded_error_message_and_status_when_the_network_is_down() {

        // [Arrange]
        when(gdnConfigurationReaderMock.completeUrlUsing(documentRequestMock)).thenReturn(GDN_DOCUMENT_URL);
        when(exceptionMock.getMessage()).thenReturn(REST_CLIENT_ERROR_MESSAGE);
        when(gdnRestTemplateMock.getForEntity(GDN_DOCUMENT_URL, byte[].class)).thenThrow(exceptionMock);

        // [Act]
        final Document actual = service.retrieve(documentRequestMock);

        // [Assert]
        assertThat(actual)
                .isNotNull()
                .hasFieldOrPropertyWithValue("content", null)
                .hasFieldOrPropertyWithValue("filename", null)
                .hasFieldOrPropertyWithValue("libelleAnomalie", REST_CLIENT_ERROR_MESSAGE)
                .hasFieldOrPropertyWithValue("returnCode", ERREUR_TECHNIQUE);

    }

    @Test
    public void should_return_a_document_with_an_embedded_error_message_and_status_when_the_document_does_not_exist() {

        // [Arrange]
        when(gdnConfigurationReaderMock.completeUrlUsing(documentRequestMock)).thenReturn(GDN_DOCUMENT_URL);
        when(responseMock.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND);
        when(gdnRestTemplateMock.getForEntity(GDN_DOCUMENT_URL, byte[].class)).thenReturn(responseMock);

        // [Act]
        final Document actual = service.retrieve(documentRequestMock);

        // [Assert]
        assertThat(actual)
                .isNotNull()
                .hasFieldOrPropertyWithValue("content", null)
                .hasFieldOrPropertyWithValue("filename", null)
                .hasFieldOrPropertyWithValue("libelleAnomalie", ERROR_MESSAGE_DOCUMENT_RETRIEVAL)
                .hasFieldOrPropertyWithValue("returnCode", ERREUR_TECHNIQUE);

    }

    @Test
    public void should_return_a_document_with_an_embedded_error_message_and_status_when_the_body_is_missing() {

        // [Arrange]
        when(gdnConfigurationReaderMock.completeUrlUsing(documentRequestMock)).thenReturn(GDN_DOCUMENT_URL);
        when(responseMock.getStatusCode()).thenReturn(HttpStatus.OK);
        when(gdnRestTemplateMock.getForEntity(GDN_DOCUMENT_URL, byte[].class)).thenReturn(responseMock);

        // [Act]
        final Document actual = service.retrieve(documentRequestMock);

        // [Assert]
        assertThat(actual)
                .isNotNull()
                .hasFieldOrPropertyWithValue("content", null)
                .hasFieldOrPropertyWithValue("filename", null)
                .hasFieldOrPropertyWithValue("libelleAnomalie", ERROR_MESSAGE_DOCUMENT_RETRIEVAL)
                .hasFieldOrPropertyWithValue("returnCode", ERREUR_TECHNIQUE);

    }

    @Test
    public void should_return_the_entire_document_when_succeeding_in_reaching_gdn_and_the_body_is_present() {

        // [Arrange]
        when(documentRequestMock.getFilename()).thenReturn(PDF_FILENAME);
        when(gdnConfigurationReaderMock.completeUrlUsing(documentRequestMock)).thenReturn(GDN_DOCUMENT_URL);
        when(responseMock.getStatusCode()).thenReturn(HttpStatus.OK);
        when(responseMock.hasBody()).thenReturn(true);
        when(responseMock.getBody()).thenReturn(PDF_CONTENT);
        when(gdnRestTemplateMock.getForEntity(GDN_DOCUMENT_URL, byte[].class)).thenReturn(responseMock);

        // [Act]
        final Document actual = service.retrieve(documentRequestMock);

        // [Assert]
        final byte[] pdfEncodedContent = Base64Utils.encode(PDF_CONTENT);
        assertThat(actual)
                .isNotNull()
                .hasFieldOrPropertyWithValue("content", pdfEncodedContent)
                .hasFieldOrPropertyWithValue("filename", PDF_FILENAME)
                .hasFieldOrPropertyWithValue("libelleAnomalie", null)
                .hasFieldOrPropertyWithValue("returnCode", TRAITEMENT_CORRECT);

    }

}