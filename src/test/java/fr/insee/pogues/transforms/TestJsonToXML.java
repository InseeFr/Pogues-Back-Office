package fr.insee.pogues.transforms;

import org.junit.Assert;
import org.junit.Test;
import org.xmlunit.XMLUnitException;
import org.xmlunit.diff.Diff;

import java.io.IOException;

public class TestJsonToXML {

    private Transformer transformer = new JSONToXMLImpl();

    private XMLDiff xmlDiff = new XMLDiff(transformer);

    @Test
    public void fake157(){
        performDiffTest("transforms/pogues-to-xml");
    }

    private void performDiffTest(String path) {
        try {
            Diff diff = xmlDiff.getDiff(
                    String.format("%s/in.json", path),
                    String.format("%s/out.xml", path)
            );
            Assert.assertFalse(getDiffMessage(diff, path), diff.hasDifferences());
        } catch (XMLUnitException e) {
            e.printStackTrace();
            Assert.fail();
        } catch (IOException e){
            e.printStackTrace();
            Assert.fail();
        } catch (NullPointerException e){
            e.printStackTrace();
            Assert.fail();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private String getDiffMessage(Diff diff, String path) {
        return String.format("Transformed output for %s should match expected XML document:\n %s", path, diff.toString());
    }

}
