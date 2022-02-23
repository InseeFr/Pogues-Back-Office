package fr.insee.pogues.metadata.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fr.insee.pogues.metadata.model.DataCollectionOut;
import fr.insee.pogues.metadata.model.Label;
import fr.insee.pogues.metadata.model.Operation;
import fr.insee.pogues.metadata.model.Serie;

public class MetadataServiceImplTest {
	
	Operation op;
	
	@Before
	public void setUp() {
		Label labelFr = new Label("fr","Label fr");
		Label labelEn = new Label("en","Label en");
		List<Label> labels = new ArrayList<>();
		labels.add(labelFr);
		labels.add(labelEn);
		Serie serie = new Serie();
		serie.setId("idSerie");
		this.op = new Operation(labels,"idtest",serie);
    }
	
	@Test
	public void generateDataCollectionSizeTest() {
		MetadataServiceImpl metadataServiceImpl = new MetadataServiceImpl();
		List<DataCollectionOut> dcOut = metadataServiceImpl.generateDataCollections(12, "T", this.op);
		assertEquals(12, dcOut.size());
	}
	
	@Test
	public void generateDataCollectionLabelTest() {
		MetadataServiceImpl metadataServiceImpl = new MetadataServiceImpl();
		List<DataCollectionOut> dcOut = metadataServiceImpl.generateDataCollections(12, "T", this.op);
		assertEquals("Label fr T1", dcOut.get(0).getLabels().get(0).getContent());
	}
	

}
