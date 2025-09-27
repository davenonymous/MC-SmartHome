package com.davenonymous.smarthome.api.visualization;

import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import net.minecraft.resources.ResourceLocation;

public interface IVisualization<D extends IVisualizationData, S extends IVisualizationSettings> {
	ResourceLocation id();
	default String nameTranslationKey() {
		var dotted = id().getPath().replaceAll("/", ".");
		return id().getNamespace() + "." + dotted + ".name";
	}

	default String descriptionTranslationKey() {
		var dotted = id().getPath().replaceAll("/", ".");
		return id().getNamespace() + "." + dotted + ".description";
	}

	S getDefaultSettings();

	// Function<DuckDBConnection, D> dataFetcher(HomeZone zone, ConfiguredDevice device, ISensor<?, ?> sensor);

	Widget getWidget(D data, S vizSettings);
}
