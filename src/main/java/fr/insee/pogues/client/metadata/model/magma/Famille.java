package fr.insee.pogues.client.metadata.model.magma;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@AllArgsConstructor
@Getter
@Setter
public class Famille{
    private String id;
    private ArrayList<Label> label;
    private String uri;
}