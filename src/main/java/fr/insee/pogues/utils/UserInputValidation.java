package fr.insee.pogues.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserInputValidation {
	
	private UserInputValidation() {
		throw new IllegalStateException("Utility class");
	}

	public static boolean validateSerieId(String id) {
		final Pattern pattern = Pattern.compile("^[a-z]\\d{4}$");
		final Matcher matcher = pattern.matcher(id);

		return matcher.matches();
	}
	
	public static boolean validateDataCollectionId(String id) {
		final Pattern pattern = Pattern.compile("^[a-z]\\d{4}-dc[A-Z]\\d{1,2}$");
		final Matcher matcher = pattern.matcher(id);

		return matcher.matches();
	}

}
