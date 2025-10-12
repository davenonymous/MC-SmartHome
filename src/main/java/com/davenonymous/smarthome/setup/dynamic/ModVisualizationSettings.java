package com.davenonymous.smarthome.setup.dynamic;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.content.visualization.IVisualizationSettings;
import com.davenonymous.smarthome.content.visualization.SmartHomeVisualizationSettings;
import com.davenonymous.smarthome.util.AnnotationHelpers;
import com.davenonymous.smarthome.content.visualization.VisualizationSettingsCodecRegistry;
import com.davenonymous.smarthome.content.visualization.annotations.VisualizationSettingsCodec;
import com.davenonymous.smarthome.content.visualization.annotations.VisualizationSettingsId;
import com.davenonymous.smarthome.content.visualization.annotations.VisualizationSettingsStreamCodec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.ModFileScanData;

import java.lang.annotation.ElementType;
import java.util.HashMap;
import java.util.Map;

public class ModVisualizationSettings {
	public static final Map<Class<?>, ResourceLocation> ID_BY_CLASS = new HashMap<>();
	public static final Map<ResourceLocation, MapCodec> CODEC_BY_ID = new HashMap<>();
	public static final Map<ResourceLocation, StreamCodec> STREAMCODEC_BY_ID = new HashMap<>();

	public static void find() {
		ID_BY_CLASS.clear();
		CODEC_BY_ID.clear();
		STREAMCODEC_BY_ID.clear();

		ModFileScanData scanData = ModList.get().getModFileById(SmartHome.MODID).getFile().getScanResult();
		var foundVizSettingsClasses = scanData.getAnnotatedBy(SmartHomeVisualizationSettings.class, ElementType.TYPE);

		foundVizSettingsClasses.forEach(annotationData -> {

			Class<IVisualizationSettings> vizSettingsClass = AnnotationHelpers.getAnnotatedClass(annotationData, IVisualizationSettings.class);
			ResourceLocation vizSettingsId = AnnotationHelpers.getSingularFieldData(vizSettingsClass, VisualizationSettingsId.class, ResourceLocation.class);
			MapCodec vizSettingsCodec = AnnotationHelpers.getSingularFieldData(vizSettingsClass, VisualizationSettingsCodec.class, MapCodec.class);
			StreamCodec vizSettingsStreamCodec = AnnotationHelpers.getSingularFieldData(vizSettingsClass, VisualizationSettingsStreamCodec.class, StreamCodec.class);

			var settingsClassSimpleName = vizSettingsClass.getSimpleName();
			var settingsClassSnakeCaseName = snakeCase(settingsClassSimpleName);

			try {
				ID_BY_CLASS.put(vizSettingsClass, vizSettingsId);
				CODEC_BY_ID.put(vizSettingsId, vizSettingsCodec);
				STREAMCODEC_BY_ID.put(vizSettingsId, vizSettingsStreamCodec);

				//noinspection unchecked
				VisualizationSettingsCodecRegistry.DEFERRED_VIZ_SETTINGS.register(settingsClassSnakeCaseName, () -> vizSettingsCodec);

				//noinspection unchecked
				VisualizationSettingsCodecRegistry.DEFERRED_VIZ_SETTINGS_DISPATCHER.register(settingsClassSnakeCaseName, () -> vizSettingsStreamCodec);

				SmartHome.LOGGER.info("Found visualization settings: {}", vizSettingsId);
			} catch (Exception e) {
				SmartHome.LOGGER.error("Failed to load visualization settings: {}", annotationData.clazz().getClassName(), e);
			}
		});
	}

	private static String snakeCase(String input) {
		return input.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
	}
}
