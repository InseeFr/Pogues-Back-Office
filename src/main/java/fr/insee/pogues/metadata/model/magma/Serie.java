package fr.insee.pogues.metadata.model.magma;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@AllArgsConstructor
@Getter
@Setter
public class Serie {
    private ArrayList<AltLabel> altLabel;
    private ArrayList<Label> label;
    private Type type;
    private String series;
    private String id;
    private Frequence frequence;
    private String nbOperation;
    private Famille famille;
    private ArrayList<String> proprietaire;
}