package com.davenonymous.smarthome.visualization.line;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.api.sensor.SensorColumn;
import com.davenonymous.smarthome.api.sensor.SensorRange;
import com.davenonymous.smarthome.api.visualization.IVisualization;
import com.davenonymous.smarthome.api.visualization.SmartHomeVisualization;
import com.davenonymous.smarthome.gui.WidgetChart;
import com.davenonymous.smarthome.lib.gui.ColorHelper;
import com.davenonymous.smarthome.lib.gui.GUIHelper;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.api.sensor.sensortypes.HomeSensor;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetColorDisplay;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.visualization.VizLegendStyle;
import com.davenonymous.smarthome.visualization.annotations.VisualizationDescription;
import com.davenonymous.smarthome.visualization.annotations.VisualizationId;
import com.davenonymous.smarthome.visualization.annotations.VisualizationName;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.neoforged.fml.earlydisplay.RenderElement;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.Marker;
import org.knowm.xchart.style.markers.None;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

@SmartHomeVisualization
public class LineViz implements IVisualization<LineVizSettings> {
	@VisualizationId
	public static final ResourceLocation ID = SmartHome.resource("visualization/line");

	@VisualizationName
	@I18DataGen(lang = "en_us", string = "Line Chart")
	@I18DataGen(lang = "de_de", string = "Liniendiagramm")
	public static final I18String NAME = I18String.gui("visualization.line", "name");

	@VisualizationDescription
	@I18DataGen(lang = "en_us", string = "Shows historical data as a line chart.")
	@I18DataGen(lang = "de_de", string = "Zeigt den Datenverlauf als Liniendiagramm an.")
	public static final I18String DESC = I18String.gui("visualization.line", "description");

	@Override
	public LineVizSettings getDefaultSettings() {
		return new LineVizSettings(Map.of(), VizLegendStyle.BOTTOM);
	}

	@Override
	public Widget getWidget(int texId, Map<UUID, LinkedHashMap<Pair<Instant, Long>, ISensorData>> dataByDevice, HomeSensor<?, ?> sensor, LineVizSettings settings, Vec2 size) {
		if(dataByDevice.isEmpty()) {
			return new WidgetColorDisplay(ColorHelper.COLOR_ORANGE).setSize((int) size.x, (int) size.y);
		}

		int width = Math.max((int) size.x, 120);
		int height = Math.max((int) size.y, 70);
		double guiScale = Minecraft.getInstance().getWindow().getGuiScale();

		XYChart chart = new XYChartBuilder()
			.width(width * (int) guiScale)
			.height(height * (int) guiScale)
			.title("Line")
			.build();


		var styler = chart.getStyler()
			.setAxisTickLabelsColor(new Color(ChatFormatting.WHITE.getColor(), false))
			.setxAxisTickLabelsFormattingFunction(val -> {
				var instant = Instant.ofEpochMilli(Math.round(val));
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());
				return formatter.format(instant);
			})
			.setyAxisTickLabelsFormattingFunction(LineViz::formatKMGT)
			.setAxisTicksLineVisible(false)
			.setPlotGridLinesVisible(false);

		Set<String> seriesNameSet = new HashSet<>();
		var allTheData = new ArrayList<ISensorData>();
		for(var deviceData : dataByDevice.values()) {
			allTheData.addAll(deviceData.values());

			for(var data : deviceData.values()) {
				seriesNameSet.add(data.seriesName());
			}
		}

		List<String> seriesNames = new ArrayList<>(seriesNameSet).stream().sorted(Comparator.naturalOrder()).toList();

		SensorRange range = sensor.getRange();
		if(range.hasMin()) {
			styler.setYAxisMin(range.min(sensor, allTheData));
		}

		if(range.hasMax()) {
			styler.setYAxisMax(range.max(sensor, allTheData));
		}

		Font myFont = null;
		try {
			var fonts = Font.createFonts(Path.of("../assets/fonts/samsung-gt-e1270-bold.otf").toFile());
			myFont = fonts[0].deriveFont(13.0f * (float) guiScale * 0.5f);
		} catch (FontFormatException e) {
		} catch (IOException e) {
		}


		styler
			.setSeriesMarkers(new Marker[]{new None()})
			.setChartTitleVisible(false)
			.setPlotBorderVisible(false)
			.setLegendVisible(true)
			.setLegendBackgroundColor(new Color(1, 1, 1, 0))
			.setLegendBorderColor(new Color(1, 1, 1, 0))
			.setLegendSeriesLineLength(10)
			.setLegendPadding(20)
			.setPlotBackgroundColor(new Color(1, 1, 1, 0))
			.setChartBackgroundColor(new Color(1, 1, 1, 0))
			.setChartFontColor(new Color(ChatFormatting.WHITE.getColor(), false))
			.setChartPadding(0);

		var legendStyle = settings.legendStyle();
		if(legendStyle == null || legendStyle == VizLegendStyle.OFF) {
			styler.setLegendVisible(false);
		} else {
			styler.setLegendLayout(legendStyle.layout()).setLegendPosition(legendStyle.position());
		}

		if(myFont != null) {
			styler.setAnnotationTextFont(myFont);
			styler.setLegendFont(myFont);
			styler.setAxisTickLabelsFont(myFont);
		}

		// Device -> Column -> Settings
		Map<UUID, Map<String, LineVizColumnSettings>> columnSettingsList = settings.series();

		int seriesCount = 0;
		for(UUID deviceId : columnSettingsList.keySet()) {
			LinkedHashMap<Pair<Instant, Long>, ISensorData> data = dataByDevice.get(deviceId);
			Map<String, LineVizColumnSettings> columnSettings = columnSettingsList.get(deviceId);
			if(data == null || data.isEmpty()) {
				continue;
			}

			// Series -> Timestamps
			Map<String, List<Long>> seriesToTimestamps = new HashMap<>();

			// Series -> Column -> Values
			Map<String, Map<String, List<Double>>> seriesToColumnToValues = new HashMap<>();
			for(var entry : data.sequencedEntrySet()) {
				var date = entry.getKey();
				var sensorData = entry.getValue();
				var xData = seriesToTimestamps.computeIfAbsent(sensorData.seriesName(), k -> new ArrayList<>());
				xData.add(date.getFirst().toEpochMilli());

				for(var columnName : columnSettings.keySet()) {
					LineVizColumnSettings vizSettings = columnSettings.get(columnName);
					if(!vizSettings.enabled()) {
						continue;
					}

					SensorColumn column = sensor.getColumn(columnName);
					if(column == null) {
						continue;
					}
					if(!column.type().isNumeric()) {
						continue;
					}

					Map<String, List<Double>> columnToValues = seriesToColumnToValues.computeIfAbsent(sensorData.seriesName(), k -> new HashMap<>());
					List<Double> values = columnToValues.computeIfAbsent(columnName, k -> new ArrayList<>());
					double value = sensor.valueFromData(HomeSensor.cast(sensorData), column);
					values.add(value);
				}
			}

			int seriesIndex = 0;
			for(String seriesName : seriesToColumnToValues.keySet()) {
				Map<String, List<Double>> columnToValues = seriesToColumnToValues.get(seriesName);
				for(String columnName : columnToValues.keySet()) {
					LineVizColumnSettings columnSetting = columnSettings.get(columnName);
					int columnColorRGB = columnSetting.color();
					Vector3f columnColorHSV = GUIHelper.RGBtoHSV(columnColorRGB);

					// Shift color hue based on series index, so that multiple series from same device are still distinguishable
					float hueShift = (seriesIndex * 0.06f) % 1.0f;
					int shiftedColor = RenderElement.hsvToRGB(
						Math.abs((columnColorHSV.x() + hueShift) % 1.0f),
						columnColorHSV.y(),
						columnColorHSV.z()
					);

					//				var latestValue = yData.get(columnName).getLast();
					//				var latestDate = xData.getLast();
					//				var screenX = chart.getScreenXFromChart(latestDate);
					//				var screenY = chart.getScreenYFromChart(latestValue);
					String fullSeriesName = seriesName.isBlank() ? columnSetting.label() : seriesName + " - " + columnSetting.label();
					var timestamps = seriesToTimestamps.get(seriesName);
					var series = chart
						.addSeries(fullSeriesName, timestamps, columnToValues.get(columnName))
						.setLineColor(new Color(shiftedColor, false));

					series.setLineStyle(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{3.0f, 3.0f}, 0));

					seriesCount++;
				}

				seriesIndex++;
			}
		}

		if(seriesCount == 0) {
			return new WidgetColorDisplay(ColorHelper.COLOR_ORANGE).setSize(width, height);
		}
		WidgetChart<XYChart> wigget = new WidgetChart<>(texId, chart);
		wigget.setSize(width, height);
		return wigget;
	}

	public static @NotNull String formatKMGT(double val) {
		var units = List.of("", "k", "M", "G", "T", "P", "E");
		double v = val;
		int unitIndex = 0;
		while(Math.abs(v) >= 1000.0 && unitIndex < units.size() - 1) {
			v /= 1000.0;
			unitIndex++;
		}
		if(unitIndex > 0) {
			return String.format("%.1f%s", v, units.get(unitIndex));
		}
		return String.format("%.0f", v);
	}

	@Override
	public LineVizSettings loadSettings(List<Widget> settingsWidgets) {
		Map<UUID, Map<String, LineVizColumnSettings>> series = new HashMap<>();
		for(var widget : settingsWidgets) {
			if(!(widget instanceof SeriesSettingsWidget seriesWidget)) {
				continue;
			}

			var device = seriesWidget.device;
			series.put(device.id(), seriesWidget.currentSettings());
		}

		return new LineVizSettings(series, VizLegendStyle.BOTTOM);
	}
}
