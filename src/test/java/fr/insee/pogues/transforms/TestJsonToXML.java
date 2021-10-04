package fr.insee.pogues.transforms;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.xmlunit.XMLUnitException;
import org.xmlunit.diff.Diff;

import fr.insee.pogues.transforms.visualize.PoguesJSONToPoguesXMLImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class TestJsonToXML {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	private Transformer transformer = new PoguesJSONToPoguesXMLImpl();
	private XMLDiff xmlDiff = new XMLDiff(transformer);

	@BeforeEach
	public void beforeEach() {
		System.setProperty("javax.xml.bind.context.factory", "org.eclipse.persistence.jaxb.JAXBContextFactory");
	}

	@org.junit.jupiter.api.Test
	public void fake157() {
		// performDiffTest("transforms/pogues-to-xml");
	}

	@org.junit.jupiter.api.Test
	public void transformWithNullStringInputException() throws Exception {
		exception.expect(NullPointerException.class);
		exception.expectMessage("Null input");
		String input = null;
		String survey = null;
		Map<String, Object> params = null;
		transformer.transform(input, params, survey);
	}

	@org.junit.jupiter.api.Test
	public void transformWithNullStreamInputException() throws Exception {
		exception.expect(NullPointerException.class);
		exception.expectMessage("Null input");
		InputStream input = null;
		Map<String, Object> params = null;
		String survey = null;
		transformer.transform(input, params, survey);
	}

	@Test
	public void transformWithNullStreamOutputException() throws Exception {
		exception.expect(NullPointerException.class);
		exception.expectMessage("Null output");
		InputStream input = new InputStream() {
			@Override
			public int read() throws IOException {
				return 0;
			}
		};
		OutputStream output = null;
		Map<String, Object> params = null;
		String survey = null;
		transformer.transform(input, output, params, survey);
	}

	private void performDiffTest(String path) {
		try {
			Diff diff = xmlDiff.getDiff(String.format("%s/in.json", path), String.format("%s/out.xml", path));
			Assert.assertFalse(getDiffMessage(diff, path), diff.hasDifferences());
		} catch (XMLUnitException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (NullPointerException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private String getDiffMessage(Diff diff, String path) {
		return String.format("Transformed output for %s should match expected XML document:\n %s", path,
				diff.toString());
	}

}
