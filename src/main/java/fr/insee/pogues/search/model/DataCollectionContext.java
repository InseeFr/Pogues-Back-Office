package fr.insee.pogues.search.model;

public class DataCollectionContext {

    private String dataCollectionId;

    private String serieId;
    
    private String operationId;

	public String getDataCollectionId() {
		return dataCollectionId;
	}

	public void setDataCollectionId(String dataCollectionId) {
		this.dataCollectionId = dataCollectionId;
	}
	
	public String getSerieId() {
		return serieId;
	}

	public void setSerieId(String serieId) {
		this.serieId = serieId;
	}

	public String getOperationId() {
		return operationId;
	}

	public void setOperationId(String operationId) {
		this.operationId = operationId;
	}
    
   
}
