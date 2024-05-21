package fr.insee.pogues.metadata.repository;

import fr.insee.pogues.metadata.client.DDIASClient;
import fr.insee.pogues.metadata.client.MagmaClient;
import fr.insee.pogues.metadata.model.Unit;
import fr.insee.pogues.metadata.model.magma.Operation;
import fr.insee.pogues.metadata.model.magma.Serie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetadataRepositoryImpl implements MetadataRepository {

	@Autowired
    DDIASClient DDIASClient;

	@Autowired
	MagmaClient magmaClient;

	@Override
	public List<Unit> getUnits() throws Exception {
		return DDIASClient.getUnits();
	}

	@Override
	public List<Serie> getSeries() throws Exception {
		return magmaClient.getSeries();
	}

	@Override
	public List<Operation> getOperationsByIdSerie(String idSerie) throws Exception {
		return magmaClient.getOperationsByIdSerie(idSerie);
	}

	@Override
	public Operation getOperationByIdOperation(String idOperation) throws Exception {
		return magmaClient.getOperationByIdOperation(idOperation);
	}
}
