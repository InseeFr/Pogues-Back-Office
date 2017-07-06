package fr.insee.pogues.api.remote.metadata.service;

public class TestWS {

	public static void main(String[] args) {
		
		RepositoryQuestionnairesService service = new RepositoryQuestionnairesServiceImpl();
		try {
			String ddi = service.getDDIQuestionnaireByID("d3ed6638-583f-4ce0-b677-e31e6df89921");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
