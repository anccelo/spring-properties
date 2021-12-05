package com.aurelienmottier.spring.properties;

import org.springframework.stereotype.Component;

import static com.aurelienmottier.spring.properties.RetrieveListDocReturnCode.TRAITEMENT_CORRECT;

@Component
public class ZosServiceAdapter {

    public Document checkParameterIntoZos(final DocumentRequest request) {
        return new Document(null, "test.pdf", null, TRAITEMENT_CORRECT);
    }

}