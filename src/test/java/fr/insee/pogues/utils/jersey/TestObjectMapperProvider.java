package fr.insee.pogues.utils.jersey;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Assert;
import org.junit.Test;

public class TestObjectMapperProvider {

    ObjectMapperProvider provider = new ObjectMapperProvider();

    @Test
    public void configureObjectMapper(){
        ObjectMapper mapper = provider.getContext(getClass());
        Assert.assertFalse(mapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        Assert.assertFalse(mapper.isEnabled(SerializationFeature.FAIL_ON_EMPTY_BEANS));
    }
}
