package fr.insee.pogues.metadata.model.magma;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@AllArgsConstructor
@Getter
@Setter
public class Frequence{
    private FrequenceType id;
    private ArrayList<Label> label;
    private String uri;
}