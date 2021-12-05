package com.aurelienmottier.spring.properties;

import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Component
public class GdnConfigurationReader {

    static final Integer GDN_PROPERTY_HOST_DEFAULT_PORT = 80;
    static final String URL_TEMPLATE = "%s?user=%s&user-type=%s&document=%s";

    static final String GDN_PROPERTY_SCHEME = "gdn-client.scheme";
    static final String GDN_PROPERTY_HOST_CONSULTATION = "gdn-client.host.consultation";
    static final String GDN_PROPERTY_HOST_PORT = "gdn-client.host.port";
    static final String GDN_PROPERTY_HOST_BASE = "gdn-client.host.basepath";
    static final String GDN_PROPERTY_HOST_GET_DOC_PATH = "gdn-client.host.getdocpath";

    private final String resource;

    public GdnConfigurationReader(final UrlBuilder urlBuilder, final ConfigurationPropertyReader propertyReader) {
        final String scheme = propertyReader.getStringProperty(GDN_PROPERTY_SCHEME);
        final String consultation = propertyReader.getStringProperty(GDN_PROPERTY_HOST_CONSULTATION);
        final Integer port = propertyReader.getIntegerPropertyOrDefault(GDN_PROPERTY_HOST_PORT, GDN_PROPERTY_HOST_DEFAULT_PORT);
        final String basePath = propertyReader.getStringProperty(GDN_PROPERTY_HOST_BASE);
        final String getDocPath = basePath + propertyReader.getStringProperty(GDN_PROPERTY_HOST_GET_DOC_PATH);
        this.resource = urlBuilder.from(scheme, consultation, port, getDocPath).toString();
    }

    public String completeUrlUsing(final DocumentRequest request) {
        return format(
                URL_TEMPLATE,
                this.resource,
                request.getIdPersonne(),
                request.getUserType(),
                request.getIdDoc()
        );
    }

}