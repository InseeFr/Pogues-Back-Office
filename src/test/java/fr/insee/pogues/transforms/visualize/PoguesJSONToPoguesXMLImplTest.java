package fr.insee.pogues.transforms.visualize;

import fr.insee.pogues.transforms.XMLDiff;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xmlunit.XMLUnitException;
import org.xmlunit.diff.Diff;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PoguesJSONToPoguesXMLImplTest {

	private ModelTransformer transformer = new PoguesJSONToPoguesXMLImpl();
	private XMLDiff xmlDiff = new XMLDiff(transformer);
	private String surveyNull;
	private Map<String, Object> paramsNull;
	

	@BeforeEach
	public void beforeEach() {paramsNull = null;
		surveyNull = null;
	}

	@Test
	void transformWithNullStringInputException() throws Exception {
		InputStream inputStringNull = null;
		Throwable exception = assertThrows(
				NullPointerException.class,
				() -> transformer.transform(inputStringNull, paramsNull, surveyNull));
		assertEquals("Null input",exception.getMessage());
	}

	@Test
	void transformWithNullStreamInputException() throws Exception {
		InputStream input = null;
		Throwable exception = assertThrows(
				NullPointerException.class,
				() -> transformer.transform(input, paramsNull, surveyNull));
		assertEquals("Null input",exception.getMessage());
	}
	
	@Test
	void convertSimpleQuestionnairePoguesJSONToPoguesXML() {
		performDiffTest("transforms/PoguesJSONToPoguesXML");
	}

	private void performDiffTest(String path) {
		try {
			Diff diff = xmlDiff.getDiff(String.format("%s/in.json", path), String.format("%s/out.xml", path));
			//assertFalse(getDiffMessage(diff, path), diff.hasDifferences());
			assertFalse(diff.hasDifferences());
		} catch (XMLUnitException e) {
			e.printStackTrace();
			fail();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} catch (NullPointerException e) {
			e.printStackTrace();
			fail();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private String getDiffMessage(Diff diff, String path) {
		return String.format("Transformed output for %s should match expected XML document:\n %s", path,
				diff.toString());
	}

}
