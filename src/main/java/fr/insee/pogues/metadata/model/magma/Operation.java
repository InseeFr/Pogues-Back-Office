package fr.insee.pogues.metadata.model.magma;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@AllArgsConstructor
@Getter
@Setter
public class Operation {
    private ArrayList<AltLabel> altLabel;
    private ArrayList<Label> label;
    private String uri;
    private Serie serie;
    private String id;
}