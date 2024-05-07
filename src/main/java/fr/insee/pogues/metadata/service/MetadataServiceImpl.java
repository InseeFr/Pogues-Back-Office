package fr.insee.pogues.metadata.service;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.insee.pogues.metadata.model.ColecticaItem;
import fr.insee.pogues.metadata.model.ColecticaItemRefList;
import fr.insee.pogues.metadata.model.Unit;
import fr.insee.pogues.metadata.repository.MetadataRepository;

@Service
@Slf4j
public class MetadataServiceImpl implements MetadataService {

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

	@Override
	public String getCodeList(String id) throws Exception {
		
		String codeList = metadataRepository.getCodeList(id);
		
		
		
		return null;
	}
}
