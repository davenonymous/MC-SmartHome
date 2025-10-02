package com.davenonymous.smarthome.visualization.line;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.api.visualization.IVisualization;
import com.davenonymous.smarthome.api.visualization.SmartHomeVisualization;
import com.davenonymous.smarthome.gui.WidgetChart;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.api.sensor.sensortypes.HomeSensor;
import com.davenonymous.smarthome.util.DateHelper;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.XYStyler;
import org.knowm.xchart.style.markers.Marker;
import org.knowm.xchart.style.markers.None;

import java.awt.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

@SmartHomeVisualization(modid = SmartHome.MODID)
public class LineViz implements IVisualization<LineVizData, LineVizSettings> {
	public static final ResourceLocation ID = SmartHome.resource("visualization/line");

	@Override
	public LineVizSettings getDefaultSettings() {
		return new LineVizSettings(List.of());
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public Widget getWidget(LinkedHashMap<Pair<Instant, Long>, ISensorData> data, HomeSensor<?, ?> sensor, LineVizSettings settings) {
		XYChart chart = new XYChartBuilder()
			.width(360).height(210)
			.title("Line")
			.build();


		var styler = chart.getStyler()
			.setAxisTickLabelsColor(new Color(ChatFormatting.WHITE.getColor(), false))
			.setxAxisTickLabelsFormattingFunction(val -> {
				var instant = Instant.ofEpochMilli(Math.round(val));
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());
				return formatter.format(instant);
			})
			.setyAxisTickLabelsFormattingFunction(val -> {
				var units = List.of("","k","M","G","T","P","E");
				double v = val;
				int unitIndex = 0;
				while(Math.abs(v) >= 1000.0 && unitIndex < units.size()-1) {
					v /= 1000.0;
					unitIndex++;
				}
				if(unitIndex > 0) {
					return String.format("%.1f%s", v, units.get(unitIndex));
				}
				return String.format("%.0f", v);
			})
			.setAxisTicksLineVisible(false)
			.setPlotGridLinesVisible(false);

		if(sensor.hasMin()) {
			if(!sensor.usesDynamicMin()) {
				styler.setYAxisMin(sensor.getStaticMin());
			}
		}

		if(sensor.hasMax()) {
			if(!sensor.usesDynamicMax()) {
				styler.setYAxisMax(sensor.getStaticMax());
			}
		}

		styler
			.setSeriesMarkers(new Marker[] {new None()})
			.setChartTitleVisible(false)
			.setPlotBorderVisible(false)
			.setLegendVisible(false)
			.setPlotBackgroundColor(new Color(1, 1, 1, 0))
			.setChartBackgroundColor(new Color(1, 1, 1, 0))
			.setChartFontColor(new Color(ChatFormatting.WHITE.getColor(), false))
			.setChartPadding(0);

		List<Long> xData = new ArrayList<>();
		Map<String, List<Double>> yData = new HashMap<>();
		data.forEach((date, sensorData) ->{
			xData.add(date.getFirst().toEpochMilli());
			for(var column : sensor.getColumns()) {
				if(!column.type().isNumeric()) {
					continue;
				}
				var columnName = column.name();
				var list = yData.computeIfAbsent(columnName, k -> new ArrayList<>());
				var value = sensor.valueFromData(HomeSensor.cast(sensorData), column);
				list.add(value);
			}
		});

		var seriesSettings = settings.series();
		int seriesIndex = 0;
		for(String seriesName : yData.keySet()) {
			LineVizSeriesSettings serieSetting = seriesSettings.get(seriesIndex % seriesSettings.size());

			List<Double> series = yData.get(seriesName);
			chart.addSeries(seriesName, xData, series)
				.setLineColor(new Color(serieSetting.color(), false));

			seriesIndex++;
		}

		WidgetChart<XYChart> wigget = new WidgetChart<>(chart);
		wigget.setSize(120, 70);
		return wigget;
	}
}
