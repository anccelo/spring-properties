package com.aurelienmottier.spring.properties;

@FunctionalInterface
public interface GdnServicePort {

    Document retrieve(final DocumentRequest request);

}