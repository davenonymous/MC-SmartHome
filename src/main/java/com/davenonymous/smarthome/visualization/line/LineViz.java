package com.davenonymous.smarthome.visualization.line;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.visualization.IVisualization;
import com.davenonymous.smarthome.api.visualization.SmartHomeVisualization;
import com.davenonymous.smarthome.gui.WidgetChart;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.visualization.gauge.GaugeVizData;
import com.davenonymous.smarthome.visualization.gauge.GaugeVizSettings;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import org.knowm.xchart.DialChart;
import org.knowm.xchart.DialChartBuilder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.internal.series.MarkerSeries;
import org.knowm.xchart.style.markers.Marker;
import org.knowm.xchart.style.markers.None;

import java.awt.*;
import java.util.List;
import java.util.Map;

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
	public Widget getWidget(LineVizData data, LineVizSettings settings) {
		XYChart chart = new XYChartBuilder()
			.width(360).height(210)
			.title("Line")
			.build();

		chart.getStyler()
			.setAxisTickLabelsColor(new Color(ChatFormatting.WHITE.getColor(), false))
			.setSeriesLines(new BasicStroke[] {new BasicStroke(1.5f)})
			.setSeriesMarkers(new Marker[] {new None()})
			.setChartTitleVisible(false)
			.setPlotBorderVisible(false)
			.setLegendVisible(false)
			.setPlotBackgroundColor(new Color(1, 1, 1, 0))
			.setChartBackgroundColor(new Color(1, 1, 1, 0))
			.setChartFontColor(new Color(ChatFormatting.WHITE.getColor(), false))
			.setChartPadding(0);

		int seriesIndex = 0;
		for(Map<Long, Double> series : data.values()) {
			List<Long> xData = series.keySet().stream().toList();
			List<Double> yData = series.values().stream().toList();
			chart.addSeries("" + seriesIndex, xData, yData);
			seriesIndex++;
		}

		WidgetChart<XYChart> wigget = new WidgetChart<>(chart);
		wigget.setSize(120, 70);
		return wigget;
	}
}
