package com.aurelienmottier.spring.properties;

public class Document {

    private final byte[] content;
    private final String filename;
    private final String libelleAnomalie;
    private final RetrieveListDocReturnCode returnCode;

    public Document(final byte[] content, final String filename, final String libelleAnomalie, final RetrieveListDocReturnCode returnCode) {
        this.content = content;
        this.filename = filename;
        this.libelleAnomalie = libelleAnomalie;
        this.returnCode = returnCode;
    }

}