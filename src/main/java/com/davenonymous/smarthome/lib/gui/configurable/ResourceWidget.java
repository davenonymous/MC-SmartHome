package com.davenonymous.smarthome.lib.gui.configurable;

import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResourceWidget extends EditBoxWidget<ResourceLocation> {


	public ResourceWidget() {
		super(null);

	}

	public ResourceWidget(ResourceLocation value) {
		super(value);

	}

	@Override
	public @NotNull String formatAsString(ResourceLocation value) {
		if(value == null) {
			return "";
		}

		return value.toString();
	}

	@Override
	public @Nullable ResourceLocation parseValue(String input) throws NumberFormatException {
		if(input == null || input.isEmpty()) {
			return null;
		}

		try {
			return ResourceLocation.parse(input);
		} catch (ResourceLocationException exception) {
		}

		return null;
	}

	@Override
	public @Nullable String mustMatchRegex() {
		return "[a-z][a-z0-9_]*:?[a-z0-9_/]+$"; // Matches the format "namespace:path"
	}
}
