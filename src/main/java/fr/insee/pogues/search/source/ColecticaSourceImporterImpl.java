package fr.insee.pogues.search.source;

import fr.insee.pogues.metadata.service.MetadataService;
import fr.insee.pogues.search.model.*;
import fr.insee.pogues.search.service.SearchService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class ColecticaSourceImporterImpl implements ColecticaSourceImporter {

    private final static Logger logger = LogManager.getLogger(ColecticaSourceImporter.class);

    @Autowired
    MetadataService metadataService;

    @Autowired
    SearchService searchService;

    List<String> groupIds;

    @PostConstruct
    public void setUp() throws Exception {
        try {
            groupIds = metadataService.getGroupIds();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    @Override
    public void source() throws Exception {
        for (String id : groupIds) {
            logger.debug("Getting data from colectica API for group " + id);
            Group f = metadataService.getGroup(id);
            searchService.save("group", f);
            saveSeries(f.getSubGroups());
        }
    }

    public void saveSeries(List<SubGroup> subGroups) throws Exception {
        for (SubGroup s : subGroups) {
            searchService.save("sub-group", s);
            saveOperations(s.getStudyUnits());
        }
    }

    public void saveOperations(List<StudyUnit> studyUnits) throws Exception {
        for (StudyUnit o : studyUnits) {
            searchService.save("study-unit", o);
            saveDataCollections(o.getDataCollections());
        }
    }

    public void saveDataCollections(List<DataCollection> dataCollections) throws Exception {
        for (DataCollection dc : dataCollections) {
            searchService.save("data-collection", dc);
            saveInstrumentSchemes(dc.getInstrumentSchemes());
        }
    }

    public void saveInstrumentSchemes(List<InstrumentScheme> instrumentSchemes) throws Exception {
        for (InstrumentScheme i : instrumentSchemes) {
            searchService.save("instrument-scheme", i);
            saveQuestionnaires(i.getInstruments());
        }
    }

    public void saveQuestionnaires(List<Instrument> instruments) throws Exception {
        for (Instrument q : instruments) {
            searchService.save("instrument", q);
        }
    }


}
