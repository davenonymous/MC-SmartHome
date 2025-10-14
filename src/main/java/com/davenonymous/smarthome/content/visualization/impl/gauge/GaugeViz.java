package com.davenonymous.smarthome.content.visualization.impl.gauge;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.content.sensor.ISensorData;
import com.davenonymous.smarthome.content.sensor.SensorRange;
import com.davenonymous.smarthome.content.visualization.IVisualization;
import com.davenonymous.smarthome.content.visualization.SmartHomeVisualization;
import com.davenonymous.smarthome.gui.WidgetChart;
import com.davenonymous.smarthome.lib.gui.ColorHelper;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.content.sensor.sensortypes.HomeSensor;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetColorDisplay;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.content.visualization.annotations.VisualizationDescription;
import com.davenonymous.smarthome.content.visualization.annotations.VisualizationId;
import com.davenonymous.smarthome.content.visualization.annotations.VisualizationName;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import org.knowm.xchart.DialChart;
import org.knowm.xchart.DialChartBuilder;
import org.knowm.xchart.style.DialStyler;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
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
	public static final I18String NAME = I18String.gui("visualization.gauge", "name");

	@VisualizationDescription
	@I18DataGen(lang = "en_us", string = "Shows current value as a gauge.")
	@I18DataGen(lang = "de_de", string = "Zeigt den aktuellen Wert als Ausschlag in einer Messuhr an.")
	public static final I18String DESC = I18String.gui("visualization.gauge", "description");

	@Override
	public GaugeVizSettings getDefaultSettings() {
		return new GaugeVizSettings(0.0, 100.0, GaugeVizSettings.DEFAULT_THRESHOLDS);
	}

	@Override
	public boolean requiresHistory() {
		return false;
	}

	@Override
	public Widget getWidget(int texId, Map<UUID, LinkedHashMap<Pair<Instant, Long>, ISensorData>> dataByDevice, HomeSensor<?, ?> sensor, GaugeVizSettings settings, Vec2 size) {
		if(dataByDevice.isEmpty()) {
			return new WidgetColorDisplay(ColorHelper.COLOR_ORANGE).setSize((int) size.x, (int) size.y);
		}

		int width = Math.max((int) size.x, 120);
		int height = Math.max((int) size.y, 70);
		double guiScale = Minecraft.getInstance().getWindow().getGuiScale();

		DialChart chart = new DialChartBuilder()
			.width(width * (int) guiScale)
			.height(height * (int) guiScale)
			.title("Gauge")
			.build();

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
		SensorRange range = sensor.getRange();
		DialStyler styler = chart.getStyler();

		var allTheData = new ArrayList<ISensorData>();
		for(var deviceData : dataByDevice.values()) {
			allTheData.addAll(deviceData.values());
		}

		int sectionCount = 5; // TODO: Move to settings
		if(sectionCount < 1) {
			sectionCount = 1;
		}

		if(!range.hasMax()) {
			return null;
		}

		double maxValue = range.max(sensor, allTheData);
		double valuePerSection = maxValue / sectionCount;
		double[] sectionBoundaries = new double[sectionCount+1];
		String[] sectionLabels = new String[sectionCount+1];
		for(int i = 0; i <= sectionCount; i++) {
			sectionBoundaries[i] = i * valuePerSection / maxValue;
			sectionLabels[i] = String.format("%.0f", i * valuePerSection);
		}
		styler.setAxisTickValues(sectionBoundaries);
		styler.setAxisTickLabels(sectionLabels);

		// TODO: Add low is good/bad toggle
		styler.setLowerColor(new Color(ColorHelper.COLOR_GREEN, true));
		styler.setUpperColor(ColorHelper.COLOR_ERRORED);
		styler.setMiddleColor(new Color(ChatFormatting.YELLOW.getColor() | 0xFF000000));
		styler.setAxisTickMarksColor(new Color(0,0,0,0.5f));

		// TODO: Maybe try to draw our own arrow, a sprite based one would fit the style better
		styler.setArrowColor(new Color(0.1f,0.1f,0.1f,0.8f));
		chart.addSeries(column.label().get(), value / maxValue, "" + value);

		Font myFont = null;
		Font myFontBig = null;
		try {
			var fonts = Font.createFonts(Path.of("../assets/fonts/samsung-gt-e1270-bold.otf").toFile());
			myFont = fonts[0].deriveFont(13.0f * (float) guiScale * 0.5f);
			myFontBig = fonts[0].deriveFont(26.0f * (float) guiScale * 0.5f);
		} catch (FontFormatException e) {
		} catch (IOException e) {
		}
		if(myFont != null) {
			styler.setAxisTitleFont(myFont);
			styler.setAnnotationTextFont(myFont);
			styler.setLegendFont(myFont);
			styler.setLabelFont(myFontBig);
		}

		// TODO: a lot of these need to be moved to viz settings
		styler
//			.setAxisTickValues(new double[] {0/15d, 3/15d, 6/15d, 9/15d, 12/15d, 15/15d})
//			.setAxisTickLabels(new String[] {"0", "3", "6", "9", "12", "15"})
			.setDonutThickness(.2d)
			.setAxisTickMarksStroke(new BasicStroke(2))
			.setArcAngle(150d)
			//.setArrowColor(Color.WHITE)
			.setArrowArcAngle(10)
			.setArrowArcPercentage(0.15d)
			.setArrowLengthPercentage(1.1d)
			.setLabelVisible(true)
			.setAxisTitleVisible(false)
			.setChartTitleVisible(false)
			.setPlotBorderVisible(false)
			.setLegendVisible(false)
			.setPlotBackgroundColor(new Color(1, 1, 1, 0))
			.setChartBackgroundColor(new Color(1, 1, 1, 0))
			.setChartFontColor(new Color(ChatFormatting.WHITE.getColor(), false))
			.setChartPadding(0);


		WidgetChart<DialChart> wigget = new WidgetChart<>(texId, chart);
		wigget.setSize(width, height);
		return wigget;
	}
}
