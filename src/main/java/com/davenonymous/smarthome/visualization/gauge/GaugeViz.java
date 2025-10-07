package com.davenonymous.smarthome.visualization.gauge;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.api.visualization.IVisualization;
import com.davenonymous.smarthome.api.visualization.SmartHomeVisualization;
import com.davenonymous.smarthome.gui.WidgetChart;
import com.davenonymous.smarthome.lib.gui.ColorHelper;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.api.sensor.sensortypes.HomeSensor;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetColorDisplay;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.visualization.annotations.VisualizationDescription;
import com.davenonymous.smarthome.visualization.annotations.VisualizationId;
import com.davenonymous.smarthome.visualization.annotations.VisualizationName;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import org.knowm.xchart.DialChart;
import org.knowm.xchart.DialChartBuilder;

import java.awt.*;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@SmartHomeVisualization
public class GaugeViz implements IVisualization<GaugeVizSettings> {
	@VisualizationId
	public static final ResourceLocation ID = SmartHome.resource("visualization/gauge");

	@VisualizationName
	@I18DataGen(lang = "en_us", string = "Gauge")
	@I18DataGen(lang = "de_de", string = "Messuhr")
	public static final I18String NAME = SmartHome.guiString("visualization.gauge", "name");

	@VisualizationDescription
	@I18DataGen(lang = "en_us", string = "Shows current value as a gauge.")
	@I18DataGen(lang = "de_de", string = "Zeigt den aktuellen Wert als Ausschlag in einer Messuhr an.")
	public static final I18String DESC = SmartHome.guiString("visualization.gauge", "description");

	@Override
	public GaugeVizSettings getDefaultSettings() {
		return new GaugeVizSettings(0.0, 100.0, GaugeVizSettings.DEFAULT_THRESHOLDS);
	}

	@Override
	public boolean requiresHistory() {
		return false;
	}

	@Override
	public Widget getWidget(Map<UUID, LinkedHashMap<Pair<Instant, Long>, ISensorData>> dataByDevice, HomeSensor<?, ?> sensor, GaugeVizSettings settings, Vec2 size) {
		if(dataByDevice.isEmpty()) {
			return new WidgetColorDisplay(ColorHelper.COLOR_ORANGE).setSize(120, 70);
		}

		var deviceId = dataByDevice.keySet().iterator().next();
		var data = dataByDevice.get(deviceId);

		Map.Entry<Pair<Instant, Long>, ISensorData> entry = data.sequencedEntrySet().getFirst();
		if(entry == null) {
			return null;
		}

		ISensorData sensorData = entry.getValue();
		if(sensor.getDefaultColumn().isEmpty()) {
			return null;
		}

		var column = sensor.getDefaultColumn().get();
		var value = sensor.valueFromData(HomeSensor.cast(sensorData), column);
		var chart = new DialChartBuilder()
			.width(360).height(210)
			.title("Gauge")
			.build();

		chart.addSeries(column.label().get(), (value - settings.min()) / settings.max(), column.label().get());

		// TODO: a lot of these need to be moved to viz settings
		chart.getStyler()
			.setAxisTickValues(new double[] {0/15d, 3/15d, 6/15d, 9/15d, 12/15d, 15/15d})
			.setAxisTickLabels(new String[] {"0", "3", "6", "9", "12", "15"})
			.setDonutThickness(.2d)
			.setAxisTickMarksStroke(new BasicStroke(2))
			.setArcAngle(120d)
			.setArrowColor(Color.WHITE)
			.setArrowArcAngle(10)
			.setArrowArcPercentage(0.1d)
			.setArrowLengthPercentage(0.9d)
			.setLabelVisible(false)
			.setAxisTitleVisible(false)
			.setChartTitleVisible(false)
			.setPlotBorderVisible(false)
			.setLegendVisible(false)
			.setPlotBackgroundColor(new Color(1, 1, 1, 0))
			.setChartBackgroundColor(new Color(1, 1, 1, 0))
			.setChartFontColor(new Color(ChatFormatting.WHITE.getColor(), false))
			.setChartPadding(0);


		WidgetChart<DialChart> wigget = new WidgetChart<>(chart);
		wigget.setSize(120, 70);
		return wigget;
	}
}
