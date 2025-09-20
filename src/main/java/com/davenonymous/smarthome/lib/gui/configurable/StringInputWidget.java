package com.davenonymous.smarthome.lib.gui.configurable;

import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StringInputWidget extends EditBoxWidget<String> {
	String filterRegex = null;

	public StringInputWidget(String value, String filterRegex) {
		super(value);
		this.filterRegex = filterRegex;
		this.setDrawBackground(true);
		this.setBackgroundColor(0xFF202020);
		this.nativeWidget().setPosition(3, 4);
		this.setHeight(16);

		if(this.filterRegex != null) {
			this.nativeWidget.setFilter(input -> input.matches(this.filterRegex));
		}
	}

	public StringInputWidget autoWidth() {
		int textWidth = Minecraft.getInstance().font.width(getValue());
		this.setWidth(textWidth + 6);
		return this;
	}

	public StringInputWidget(String value) {
		this(value, null);
	}

	@Override
	public @NotNull String formatAsString(@NotNull String value) {
		return value;
	}

	@Override
	public @Nullable String parseValue(@NotNull String input) {
		return input;
	}

	@Override
	public @Nullable String mustMatchRegex() {
		return filterRegex; // Matches the format "namespace:path"
	}
}
