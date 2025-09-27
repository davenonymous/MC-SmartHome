package com.davenonymous.smarthome.visualization.gauge;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.visualization.IVisualization;
import com.davenonymous.smarthome.api.visualization.SmartHomeVisualization;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import net.minecraft.resources.ResourceLocation;

@SmartHomeVisualization(modid = SmartHome.MODID)
public class GaugeViz implements IVisualization<GaugeVizData, GaugeVizSettings> {
	public static final ResourceLocation ID = SmartHome.resource("visualization/gauge");

	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public Widget getWidget(GaugeVizData data, GaugeVizSettings settings) {
		return new WidgetTextBox("" + data.value());
	}
}
