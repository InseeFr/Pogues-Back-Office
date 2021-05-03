package fr.insee.pogues.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FilesUtils {

	final static Logger logger = LogManager.getLogger(FilesUtils.class);

	public static InputStream fileToIS(File file) {
		InputStream is = null;
		try {
			is = new FileInputStream(file);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return is;
	}

	private FilesUtils() {
		throw new IllegalStateException("Utility class");
	}

}
