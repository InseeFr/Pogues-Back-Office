package fr.insee.pogues.exception.mapping.codes.list.registre;

public class MappingNotFoundException
        extends RuntimeException {

    public MappingNotFoundException(Long id) {

        super("Mapping not found with id: " + id);
    }
}
