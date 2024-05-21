package fr.insee.pogues.metadata.repository;

import fr.insee.pogues.metadata.client.DDIASClient;
import fr.insee.pogues.metadata.model.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetadataRepositoryImpl implements MetadataRepository {

	@Autowired
    DDIASClient DDIASClient;

	@Override
	public List<Unit> getUnits() throws Exception {
		return DDIASClient.getUnits();
	}
}
