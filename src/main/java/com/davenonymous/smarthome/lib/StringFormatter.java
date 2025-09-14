package com.davenonymous.smarthome.lib;

public class StringFormatter {
	public static String convertCamelCaseToSnake(String input) {
		return input
			.replaceAll("([A-Z])(?=[A-Z])", "$1_")
			.replaceAll("([a-z])([A-Z])", "$1_$2")
			.toLowerCase();
	}

	public static String uppercaseFirstLetter(String input) {
		if (input == null || input.isEmpty()) {
			return input;
		}
		return input.substring(0, 1).toUpperCase() + input.substring(1);
	}
}
