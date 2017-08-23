package fr.insee.pogues.transforms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

@Service
public class XFormToURIImpl implements XFormToURI {

    @Autowired
    private StromaeService stromaeService;

    @Override
    public void transform(InputStream input, OutputStream output, Map<String, Object> params) throws Exception {
        throw new NotImplementedException();
    }

    @Override
    public String transform(InputStream input, Map<String, Object> params) throws Exception {
        throw new NotImplementedException();
    }

    @Override
    public String transform(String input, Map<String, Object> params) throws Exception {
        try {
            return stromaeService.transform(input, params);
        } catch(Exception e) {
            throw e;
        }
    }
}
