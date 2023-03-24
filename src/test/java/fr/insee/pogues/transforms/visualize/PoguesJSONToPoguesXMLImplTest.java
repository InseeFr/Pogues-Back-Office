package fr.insee.pogues.transforms.visualize;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.xmlunit.XMLUnitException;
import org.xmlunit.diff.Diff;

import fr.insee.pogues.transforms.Transformer;
import fr.insee.pogues.transforms.XMLDiff;

class PoguesJSONToPoguesXMLImplTest {

	private Transformer transformer = new PoguesJSONToPoguesXMLImpl();	
	private XMLDiff xmlDiff = new XMLDiff(transformer);
	private String surveyNull;
	private Map<String, Object> paramsNull;
	

	@BeforeEach
	public void beforeEach() {
		System.setProperty("javax.xml.bind.context.factory", "org.eclipse.persistence.jaxb.JAXBContextFactory");
		paramsNull = null;
		surveyNull = null;
	}

	@Test
	void transformWithNullStringInputException() throws Exception {
		String inputStringNull = null;
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
	void transformWithNullStreamOutputException() throws Exception {
		InputStream input = new InputStream() {
			@Override
			public int read() throws IOException {
				return 0;
			}
		};
		OutputStream output = null;
		Throwable exception = assertThrows(
				NullPointerException.class,
				() -> transformer.transform(input, output, paramsNull, surveyNull));
		assertEquals("Null output",exception.getMessage());
	}
	
	@Test
	@Disabled("File has to be updated")
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
