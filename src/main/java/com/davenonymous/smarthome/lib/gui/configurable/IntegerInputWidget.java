package com.davenonymous.smarthome.lib.gui.configurable;

import org.jetbrains.annotations.NotNull;

public class IntegerInputWidget extends NumberWidget<Integer> {
	int min = Integer.MIN_VALUE;
	int max = Integer.MAX_VALUE;

	public IntegerInputWidget(int value) {
		super(value);
	}

	public IntegerInputWidget(int value, int min, int max) {
		super(value);
		this.min = min;
		this.max = max;
	}

	@Override
	public Integer add(Integer value, double delta) {
		if (value + delta < min) {
			return min;
		}
		if (value + delta > max) {
			return max;
		}

		return value + (int)Math.round(delta);
	}

	@Override
	public @NotNull String formatAsString(@NotNull Integer value) {
		return String.valueOf(value);
	}

	@Override
	public Integer parseValue(@NotNull String input) throws NumberFormatException {
		int parsedValue = Integer.parseInt(input);
		if (parsedValue < min) {
			return min;
		}
		if (parsedValue > max) {
			return max;
		}

		return parsedValue;
	}

	@Override
	public String mustMatchRegex() {
		return "\\d+";
	}
}
