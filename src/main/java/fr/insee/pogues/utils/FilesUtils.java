package fr.insee.pogues.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FilesUtils {

	public static InputStream fileToIS(File file) {
		InputStream is = null;
		try {
			is = new FileInputStream(file);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return is;
	}

	private FilesUtils() {
		throw new IllegalStateException("Utility class");
	}

}
