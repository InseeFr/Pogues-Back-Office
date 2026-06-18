package fr.insee.pogues.exception.mapping.codes.list.registre;

public class RegistreCodesListAlreadyMappedException extends RuntimeException {

    public RegistreCodesListAlreadyMappedException(String registreCodesListId) {
        super("Mapping already exists for registreCodesListId: " + registreCodesListId);
    }
}
