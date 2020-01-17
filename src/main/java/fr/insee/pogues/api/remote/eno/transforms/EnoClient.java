package fr.insee.pogues.api.remote.eno.transforms;

public interface EnoClient {
	/**
	 * 
	 * Call the Eno API to convert ddi 3.2 data in ddi 3.3 
	 * 
	 * @param ddi
	 * @return 
	 * @throws Exception
	 */
	String getDDI33FromDDI32 (String ddi) throws Exception;

}
