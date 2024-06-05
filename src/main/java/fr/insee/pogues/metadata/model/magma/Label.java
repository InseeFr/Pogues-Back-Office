package fr.insee.pogues.metadata.model.magma;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Label{
    private String langue;
    private String contenu;
}