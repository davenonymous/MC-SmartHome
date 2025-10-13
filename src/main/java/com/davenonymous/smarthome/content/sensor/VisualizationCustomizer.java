package com.davenonymous.smarthome.content.sensor;

import org.knowm.xchart.internal.chartpart.Chart;

import java.util.Map;

public interface VisualizationCustomizer {
	void customizeVisualization(Chart<?, ?> chart, Map<String, String> seriesNamesToColumnNames);
}
