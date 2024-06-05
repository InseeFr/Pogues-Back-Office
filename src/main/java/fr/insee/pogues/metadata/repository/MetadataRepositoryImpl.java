package fr.insee.pogues.metadata.repository;

import fr.insee.pogues.configuration.cache.CacheName;
import fr.insee.pogues.metadata.client.DDIASClient;
import fr.insee.pogues.metadata.client.MagmaClient;
import fr.insee.pogues.metadata.model.ddias.Unit;
import fr.insee.pogues.metadata.model.magma.Operation;
import fr.insee.pogues.metadata.model.magma.Serie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetadataRepositoryImpl implements MetadataRepository {

	/**
	 * DDIAS (DDI Access Service) Client is used of units & codeLists (suggester)
	 */
	@Autowired
    DDIASClient ddiasClient;

	/**
	 * Magma Client is used of operations & series
	 */
	@Autowired
	MagmaClient magmaClient;

	@Override
	@Cacheable(CacheName.UNITS)
	public List<Unit> getUnits() throws Exception {
		return ddiasClient.getUnits();
	}

	@Override
	@Cacheable(CacheName.SERIES)
	public List<Serie> getSeries() throws Exception {
		return magmaClient.getSeries();
	}

	@Override
	@Cacheable(CacheName.SERIE)
	public Serie getSerieById(String id) throws Exception {
		return magmaClient.getSerieById(id);
	}

	@Override
	@Cacheable(CacheName.OPERATIONS)
	public List<Operation> getOperationsByIdSerie(String idSerie) throws Exception {
		return magmaClient.getOperationsByIdSerie(idSerie);
	}

	@Override
	@Cacheable(CacheName.OPERATION)
	public Operation getOperationById(String idOperation) throws Exception {
		return magmaClient.getOperationById(idOperation);
	}
}
