package fr.insee.pogues.transforms.visualize.eno;

import fr.insee.pogues.api.remote.eno.transforms.EnoClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

import static fr.insee.pogues.utils.IOStreamsUtils.inputStream2String;
import static fr.insee.pogues.utils.IOStreamsUtils.string2BOAS;

/**
 * Created by I6VWID 25/01/19.
 */
@Service
@Slf4j
public class PoguesXMLToDDIImpl implements PoguesXMLToDDI {

	@Autowired
	EnoClient enoClient;

	@Override
	public ByteArrayOutputStream transform(InputStream inputStream, Map<String, Object> params, String surveyName) throws Exception {
		String inputAsString = inputStream2String(inputStream);
		String outputAsString = transform(inputAsString);
		return string2BOAS(outputAsString);
	}

	private String transform(String inputAsString) throws Exception {
		return enoClient.getXMLPoguesToDDI(inputAsString);
	}


}
