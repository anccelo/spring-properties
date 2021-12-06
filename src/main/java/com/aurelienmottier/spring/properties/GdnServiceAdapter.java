package com.aurelienmottier.spring.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static com.aurelienmottier.spring.properties.RetrieveListDocReturnCode.ERREUR_TECHNIQUE;
import static com.aurelienmottier.spring.properties.RetrieveListDocReturnCode.TRAITEMENT_CORRECT;

@Component
public class GdnServiceAdapter implements GdnServicePort {

    static final String ERROR_MESSAGE_DOCUMENT_RETRIEVAL = "Erreur au niveau d'appel";

    private static final Logger LOGGER = LoggerFactory.getLogger(GdnServiceAdapter.class);

    private final GdnConfigurationReader gdnConfigurationReader;
    private final RestTemplate gdnRestTemplate;
    private final GdnClientProperties gdnClientProperties;
    private final ZosServiceAdapter zosServiceAdapter;
    // Here if it existed it would be better to make:   private final ZosServicePort zosServiceAdapter?

    public GdnServiceAdapter(final GdnConfigurationReader gdnConfigurationReader,
                             final @Qualifier("GDNRestTemplate") RestTemplate gdnRestTemplate,
                             final GdnClientProperties gdnClientProperties, final ZosServiceAdapter zosServiceAdapter) {
        this.gdnConfigurationReader = gdnConfigurationReader;
        this.gdnRestTemplate = gdnRestTemplate;
        this.gdnClientProperties = gdnClientProperties;
        this.zosServiceAdapter = zosServiceAdapter;
    }

    @Override
    public Document retrieve(final DocumentRequest request) {

        if (request.shouldBeChecked())
            return this.zosServiceAdapter.checkParameterIntoZos(request);

        return this.retrieveFromGdn(request);
    }

    private Document retrieveFromGdn(final DocumentRequest request) {

        LOGGER.info("METHOD - ${package} -> callGdn.\n Parameter : {}", request);

        final String url = this.gdnConfigurationReader.completeUrlUsing(request);

        ResponseEntity<byte[]> response;

        try {

            LOGGER.info("---Debut de la requete vers {} ----------", url);
            response = this.gdnRestTemplate.getForEntity(url, byte[].class);
            LOGGER.debug("---Fin de la requete vers {} ----------", url);

        } catch (final RestClientException exception) {
            return new Document(null, null, exception.getMessage(), ERREUR_TECHNIQUE);
        }

        LOGGER.info("httpBody result: {} ", response);
        LOGGER.debug("httpHeader result: {} ", response.getHeaders());
        LOGGER.debug("result: {} ", response);

        final HttpStatus httpStatusCode = response.getStatusCode();
        LOGGER.debug("[GDN][GetDocByIdOnline][getStatusCodeValue] : {}", httpStatusCode.value());

        if (httpStatusCode.is2xxSuccessful() && response.hasBody()) {
            final byte[] pdfContent = Base64Utils.encode(response.getBody());
            final String filename = request.getFilename();
            return new Document(pdfContent, filename, null, TRAITEMENT_CORRECT);
        }

        return new Document(null, null, ERROR_MESSAGE_DOCUMENT_RETRIEVAL, ERREUR_TECHNIQUE);

    }

}
