package com.davenonymous.smarthome.visualization.gauge;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.api.visualization.IVisualization;
import com.davenonymous.smarthome.api.visualization.SmartHomeVisualization;
import com.davenonymous.smarthome.gui.WidgetChart;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.api.sensor.sensortypes.HomeSensor;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import org.knowm.xchart.DialChart;
import org.knowm.xchart.DialChartBuilder;

import java.awt.*;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@SmartHomeVisualization(modid = SmartHome.MODID)
public class GaugeViz implements IVisualization<GaugeVizData, GaugeVizSettings> {
	public static final ResourceLocation ID = SmartHome.resource("visualization/gauge");

	@Override
	public GaugeVizSettings getDefaultSettings() {
		return new GaugeVizSettings(0.0, 100.0, GaugeVizSettings.DEFAULT_THRESHOLDS);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public Widget getWidget(LinkedHashMap<Pair<Instant, Long>, ISensorData> data, HomeSensor<?, ?> sensor, GaugeVizSettings settings) {
		Map.Entry<Pair<Instant, Long>, ISensorData> entry = data.sequencedEntrySet().getFirst();
		if(entry == null) {
			return null;
		}

		ISensorData sensorData = entry.getValue();
		if(sensor.getDefaultColumn().isEmpty()) {
			return null;
		}

		var column = sensor.getDefaultColumn().get();
		var value = sensorData.getDouble(column.name());
		var chart = new DialChartBuilder()
			.width(360).height(210)
			.title("Gauge")
			.build();

		chart.addSeries(I18n.get(column.translationKey()), (value - settings.min()) / settings.max(), I18n.get(column.translationKey()));

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
