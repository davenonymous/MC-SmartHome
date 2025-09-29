package com.davenonymous.smarthome.setup.dynamic;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.visualization.IVisualization;
import com.davenonymous.smarthome.api.visualization.SmartHomeVisualization;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.ModFileScanData;

import java.lang.annotation.ElementType;
import java.util.HashMap;
import java.util.Map;

public class ModVisualizations {
	private static Map<ResourceLocation, IVisualization<?, ?>> VISUALIZATIONS = new HashMap<>();
	public static IVisualization<?, ?> getById(ResourceLocation id) {
		return VISUALIZATIONS.get(id);
	}


	public static void find() {
		VISUALIZATIONS.clear();

		ModFileScanData scanData = ModList.get().getModFileById(SmartHome.MODID).getFile().getScanResult();
		var foundAnalyzerClasses = scanData.getAnnotatedBy(SmartHomeVisualization.class, ElementType.TYPE);

		foundAnalyzerClasses.forEach(annotationData -> {
			Object modIdAnnotation = annotationData.annotationData().get("modid");
			if(!(modIdAnnotation instanceof String modid)) {
				return;
			}

			if(!ModList.get().isLoaded(modid)) {
				return;
			}

			try {
				Class<?> clazz = Class.forName(annotationData.clazz().getClassName());
				IVisualization<?, ?> sensor = (IVisualization<?, ?>) clazz.getDeclaredConstructor().newInstance();
				VISUALIZATIONS.put(sensor.id(), sensor);

				SmartHome.LOGGER.info("Found visualization: {} (mod={})", sensor.id(), modid);
			} catch (Exception e) {
				SmartHome.LOGGER.error("Failed to instantiate visualization class: {}", annotationData.clazz().getClassName(), e);
			}
		});
	}

}
