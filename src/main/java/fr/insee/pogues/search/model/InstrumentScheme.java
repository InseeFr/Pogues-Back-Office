package fr.insee.pogues.search.model;

import java.util.ArrayList;
import java.util.List;

public class InstrumentScheme extends PoguesItem {

    private List<Instrument> instruments;

    public InstrumentScheme(){
        this.instruments = new ArrayList<>();
    }

    public InstrumentScheme(String label, String parent,  String id) {
        super(label, parent, id);
        this.instruments = new ArrayList<>();
    }


    public List<Instrument> getInstruments() {
        return instruments;
    }

    public void setInstruments(List<Instrument> instruments) {
        this.instruments = instruments;
    }
}
