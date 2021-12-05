package com.aurelienmottier.spring.properties;

public class DocumentRequest {

    /**
     * Je ne connais pas la longueur alors j'ai mis 5 comme exemple mais cela importe peu.
     */
    private static final Integer MINIMUM_LENGTH_FOR_CHECK = 5;

    private final String idPersonne;
    private final String userType;
    private final String idDoc;
    private final String identLoc;

    public DocumentRequest(final String idPersonne, final String userType, final String idDoc, final String identLoc) {
        this.idPersonne = idPersonne;
        this.userType = userType;
        this.idDoc = idDoc;
        this.identLoc = identLoc;
    }

    public String getIdPersonne() {
        return this.idPersonne;
    }

    public String getUserType() {
        return this.userType;
    }

    public String getIdDoc() {
        return this.idDoc;
    }

    public String getFilename() {
        return this.idDoc.concat(this.identLoc);
    }

    public boolean shouldBeChecked() {
        return this.idPersonne.length() > MINIMUM_LENGTH_FOR_CHECK;
    }

}