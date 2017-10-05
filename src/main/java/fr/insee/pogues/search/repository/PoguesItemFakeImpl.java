package fr.insee.pogues.search.repository;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import fr.insee.pogues.search.model.DDIItem;
import fr.insee.pogues.search.model.DataCollectionContext;
import fr.insee.pogues.search.model.PoguesItem;

@Repository
@Component(value = "PoguesItemFakeImpl")
public class PoguesItemFakeImpl implements PoguesItemRepository {

	@Override
	public IndexResponse save(String type, PoguesItem item) throws Exception {
		return null;
	}

	@Override
	public List<DDIItem> findByLabel(String label, String... types) throws Exception {
		// TODO implement a fake method
		return null;
	}

	public List<DDIItem> findByLabelInSubGroup(String label, String subgroupId, String... types) throws Exception {
		// TODO implement a fake method
		return null;
	}

	@Override
	public List<DDIItem> getSubGroups() throws Exception {
		List<DDIItem> subGroups = new ArrayList<DDIItem>();
		DDIItem ddiItem = new DDIItem("01", "SubGroup Label 01", null, "SubGroup");
		DDIItem ddiItem2 = new DDIItem("02", "SubGroup Label 02", null, "SubGroup");
		subGroups.add(ddiItem);
		subGroups.add(ddiItem2);
		return subGroups;
	}

	@Override
	public List<DDIItem> getStudyUnits(String subgGroupId) throws Exception {
		List<DDIItem> studyUnits = new ArrayList<DDIItem>();
		DDIItem ddiItem = new DDIItem("03", "StudyUnit Label 01", subgGroupId, "StudyUnit");
		studyUnits.add(ddiItem);
		DDIItem ddiItem2 = new DDIItem("04", "StudyUnit Label 02", subgGroupId, "StudyUnit");
		studyUnits.add(ddiItem2);
		return studyUnits;
	}

	@Override
	public List<DDIItem> getDataCollections(String studyUnitId) throws Exception {
		List<DDIItem> dataCollections = new ArrayList<DDIItem>();
		DDIItem ddiItem = new DDIItem("05", "DataCollection Label 01", studyUnitId, "DataCollection");
		dataCollections.add(ddiItem);
		DDIItem ddiItem2 = new DDIItem("06", "DataCollection Label 02", studyUnitId, "DataCollection");
		dataCollections.add(ddiItem2);
		return dataCollections;
	}
	
	@Override
	public DataCollectionContext getDataCollectionContext(String dataCollectionId) throws Exception {
		DataCollectionContext dataCollectionContext = new DataCollectionContext();
		dataCollectionContext.setDataCollectionId(dataCollectionId);
		dataCollectionContext.setSerieId("02");
		dataCollectionContext.setOperationId("04");
		return dataCollectionContext;
	}

	@Override
	public DeleteResponse delete(String type, String id) throws Exception {
		return null;
	}
}
