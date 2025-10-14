package com.davenonymous.smarthome.content.sensor;

import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.internal.series.Series;

import java.util.Map;

public interface VisualizationCustomizer {
	<C extends Chart<?, S>, S extends Series> void customizeVisualization(Class<C> chartType, Chart<?, S> chart, Map<String, S> series);
}
