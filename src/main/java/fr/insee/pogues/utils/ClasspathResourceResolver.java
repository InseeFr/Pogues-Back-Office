package fr.insee.pogues.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.xmlgraphics.io.Resource;
import org.apache.xmlgraphics.io.ResourceResolver;
import org.springframework.core.io.ResourceLoader;

import java.io.*;
import java.net.URI;

@Slf4j
public class ClasspathResourceResolver implements ResourceResolver {
	@Override
	public Resource getResource(URI uri) {
		String uriString = uri.toString();
		log.debug("Resolved resource in with uri :"+uriString);
		if(uriString.startsWith(ResourceLoader.CLASSPATH_URL_PREFIX)) {
			String substring = uriString.substring(ResourceLoader.CLASSPATH_URL_PREFIX.length());
			InputStream resourceAsStream = ClasspathResourceResolver.class.getResourceAsStream(substring);
			return (new Resource(resourceAsStream));
		} else {
			return new Resource(ClasspathResourceResolver.class.getResourceAsStream(uriString));
		}
	}

	@Override
	public OutputStream getOutputStream(URI uri) throws IOException {
		return new FileOutputStream(new File(uri));
	}
}