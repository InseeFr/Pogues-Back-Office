package fr.insee.pogues.transforms.visualize.eno;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.insee.pogues.api.remote.eno.transforms.EnoClient;

import static fr.insee.pogues.transforms.visualize.ModelMapper.inputStream2String;
import static fr.insee.pogues.transforms.visualize.ModelMapper.string2BOAS;

@Service
public class DDIToLunaticJSONImpl implements DDIToLunaticJSON {

	@Autowired
	private EnoClient enoClient;

	@Override
	public ByteArrayOutputStream transform(InputStream inputStream, Map<String, Object> params, String surveyName) throws Exception {
		String inputAsString = inputStream2String(inputStream);
		String outputAsString = transform(inputAsString, params);
		return string2BOAS(outputAsString);
	}

	private String transform(String inputAsString, Map<String, Object> params) throws Exception {
		return enoClient.getDDITOLunaticJSON(inputAsString, params);
	}

}
