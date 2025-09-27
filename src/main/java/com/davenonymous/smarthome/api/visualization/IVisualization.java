package com.davenonymous.smarthome.api.visualization;

import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import net.minecraft.resources.ResourceLocation;
import org.duckdb.DuckDBConnection;

import java.util.function.Function;

public interface IVisualization<D extends IVisualizationData, S extends IVisualizationSettings> {
	ResourceLocation id();
	String translationKey();

	Function<DuckDBConnection, D> dataFetcher(HomeZone zone, ConfiguredDevice device);

	Widget getWidget(D data, S settings);
}
