package fr.insee.pogues.metadata.service;

import fr.insee.pogues.metadata.model.ColecticaItem;
import fr.insee.pogues.metadata.model.ColecticaItemRefList;
import fr.insee.pogues.metadata.model.Unit;
import fr.insee.pogues.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetadataServiceImpl implements MetadataService {

    private final static Logger logger = LogManager.getLogger(MetadataServiceImpl.class);

    @Autowired
    MetadataRepository metadataRepository;


    @Override
    public ColecticaItem getItem(String id) throws Exception {
        return metadataRepository.findById(id);
    }

    @Override
    public ColecticaItemRefList getChildrenRef(String id) throws Exception {
        return metadataRepository.getChildrenRef(id);
    }

    @Override
    public List<ColecticaItem> getItems(ColecticaItemRefList refs) throws Exception {
        return metadataRepository.getItems(refs);
    }

    @Override
    public List<Unit> getUnits() throws Exception {
        return metadataRepository.getUnits();
    }

    @Override
    public String getDDIDocument(String id) throws Exception {
        return metadataRepository.getDDIDocument(id);
    }
}
