package com.davenonymous.smarthome.api.visualization;

import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.api.sensor.sensortypes.HomeSensor;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

import java.time.Instant;
import java.util.LinkedHashMap;

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

	default boolean requiresHistory() {
		return true;
	}

	S getDefaultSettings();

	// Function<DuckDBConnection, D> dataFetcher(HomeZone zone, ConfiguredDevice device, ISensor<?, ?> sensor);

	Widget getWidget(LinkedHashMap<Pair<Instant, Long>, ISensorData> data, HomeSensor<?, ?> sensor, S vizSettings);
}
