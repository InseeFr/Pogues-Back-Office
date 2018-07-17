package fr.insee.pogues.transforms;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.xmlunit.XMLUnitException;
import org.xmlunit.diff.Diff;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by acordier on 19/07/17.
 */
public class TestXMLToDDI {

    private Transformer transformer = new XMLToDDIImpl();

    private XMLDiff xmlDiff = new XMLDiff(transformer);

    @Rule // TODO Activer à l'intégration de la liaison RMES
    public ExpectedException exception = ExpectedException.none();

    @Test // TODO Activer à l'intégration de la liaison RMES
    public void popoQpoCam2017TransformTest() {
        System.out.println("Test skipped");
    	// performDiffTest("transforms/POPO-QPO-CAM2017");
    }

    @Test // TODO Activer à l'intégration de la liaison RMES
    public void popoQpoCammeTransformTest() {
        System.out.println("Test skipped");
    	// performDiffTest("transforms/POPO-QPO-CAMME");
    }

    @Test // TODO Activer à l'intégration de la liaison RMES
    public void popoQpoCis2016TransformTest(){
        System.out.println("Test skipped");
    	// performDiffTest("transforms/POPO-QPO-CIS2016Q_E");
    }

    @Test // TODO Activer à l'intégration de la liaison RMES
    public void popoQpoCis2017TransformTest() {
        System.out.println("Test skipped");
    	// performDiffTest("transforms/POPO-QPO-CIS2017Q_E");
    }

    @Test // TODO Activer à l'intégration de la liaison RMES
    public void popoDechetsComm() {
        System.out.println("Test skipped");
//        performDiffTest("transforms/POPO-QPO-DECHETS_COMM");
    }

    @Test // TODO Activer à l'intégration de la liaison RMES
    public void popoDechets2016() {
        System.out.println("Test skipped");
//        performDiffTest("transforms/POPO-QPO-DECHETS2016");
    }

    @Test
    public void popoQpoDoc() {
        performDiffTest("transforms/POPO-QPO-DOC");
    }


    @Test // TODO Activer à l'intégration de la liaison RMES
    public void popoQpoDocEnTransformTest() {
    	System.out.println("Test skipped");
       //  performDiffTest("transforms/POPO-QPO-DOC-EN");
    }

    @Test // TODO Activer à l'intégration de la liaison RMES
    public void popoQpoFiltreSeq() {
        System.out.println("Test skipped");
//        performDiffTest("POPO-QPO-FILTRE_SEQ");
    }

    @Test
    public void transformWithNullInputException() throws Exception {
        exception.expect(NullPointerException.class);
        exception.expectMessage("Null input");
        InputStream input = null;
        OutputStream output = new OutputStream() {
            @Override
            public void write(int b) throws IOException {

            }
        };
        new XMLToDDIImpl()
                .transform(input, output, null,"test");

    }
    @Test
    public void transformWithNullOutputException() throws Exception {
        exception.expect(NullPointerException.class);
        exception.expectMessage("Null output");
        InputStream input = new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        };
        OutputStream output = null;
        new XMLToDDIImpl()
                .transform(input, output, null,"test");

    }


    private void performDiffTest(String path) {
        try {
            Diff diff = xmlDiff.getDiff(path);
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
        return String.format("Transformed output for %s should match expected DDI document:\n %s", path, diff.toString());
    }


}
