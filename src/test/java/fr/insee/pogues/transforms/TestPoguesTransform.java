package fr.insee.pogues.transforms;

import org.junit.Assert;
import org.junit.Test;
import org.xmlunit.XMLUnitException;
import org.xmlunit.diff.Diff;

import java.io.IOException;

/**
 * Created by acordier on 19/07/17.
 */
public class TestPoguesTransform {

    @Test
    public void popoQpoCam2017TransformTest() {
        performDiffTest("transforms/POPO-QPO-CAM2017");
    }

    @Test
    public void popoQpoCammeTransformTest() {
        performDiffTest("transforms/POPO-QPO-CAMME");
    }

    @Test
    public void popoQpoCis2016TransformTest(){
        performDiffTest("transforms/POPO-QPO-CIS2016Q_E");
    }

    @Test
    public void popoQpoCis2017TransformTest() {
        performDiffTest("transforms/POPO-QPO-CIS2017Q_E");
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


    @Test
    public void popoQpoDocEnTransformTest() {
        performDiffTest("transforms/POPO-QPO-DOC-EN");
    }

    @Test // TODO Activer à l'intégration de la liaison RMES
    public void popoQpoFiltreSeq() {
        System.out.println("Test skipped");
//        performDiffTest("POPO-QPO-FILTRE_SEQ");
    }


    private void performDiffTest(String path) {
        try {
            Diff diff = XmlDiff.getDiff(path);
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
