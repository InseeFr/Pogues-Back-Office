package fr.insee.pogues.transforms;

import fr.insee.pogues.api.remote.eno.transforms.EnoClient;
import fr.insee.pogues.conversion.XMLToJSONTranslator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class XpathToVtlImpl implements XpathToVtl {

    @Autowired
    private EnoClient enoClient;

    final static Logger logger = LogManager.getLogger(XpathToVtlImpl.class);


    @Override
    public String transform(String input) throws Exception {
        try{
            return enoClient.getXpathToVtl(input);
        } catch (Exception e) {
            throw new Exception(String.format("%s:%s", getClass().getName(), e.getMessage()));
        }
    }
}
