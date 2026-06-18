package fr.insee.pogues.exception.mapping.codes.list.registre;

public class PoguesCodesListAlreadyMappedException extends RuntimeException {

    public PoguesCodesListAlreadyMappedException(String poguesCodesListId) {
        super("Mapping already exists for poguesCodesListId: " + poguesCodesListId);
    }
}
