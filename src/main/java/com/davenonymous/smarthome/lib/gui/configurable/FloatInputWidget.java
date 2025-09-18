package com.davenonymous.smarthome.lib.gui.configurable;

import org.jetbrains.annotations.NotNull;

public class FloatInputWidget extends NumberWidget<Float> {
	private float min = Float.MIN_VALUE;
	private float max = Float.MAX_VALUE;

	public FloatInputWidget(float value) {
		super(value);
	}

	public FloatInputWidget(float value, float min, float max) {
		super(value);
		this.min = min;
		this.max = max;
	}

	@Override
	public Float add(Float value, double delta) {
		float adjustedDelta = (float)delta * 0.01f;
		if (value + adjustedDelta < min) {
			return min;
		}
		if (value + adjustedDelta > max) {
			return max;
		}

		return value + adjustedDelta;
	}

	@Override
	public @NotNull String formatAsString(@NotNull Float value) {
		return String.format("%.2f", value);
	}

	@Override
	public Float parseValue(@NotNull String input) throws NumberFormatException {
		float parsedValue = Float.parseFloat(input);
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
		return "-?\\d*\\.?\\d+f?";
	}
}
