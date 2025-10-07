package com.davenonymous.smarthome.setup.dynamic;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.visualization.IVisualization;
import com.davenonymous.smarthome.api.visualization.SmartHomeVisualization;
import com.davenonymous.smarthome.cards.HomeCardElement;
import com.davenonymous.smarthome.cards.annotations.HomeCardElementId;
import com.davenonymous.smarthome.cards.annotations.HomeCardElementName;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.util.AnnotationHelpers;
import com.davenonymous.smarthome.visualization.annotations.VisualizationDescription;
import com.davenonymous.smarthome.visualization.annotations.VisualizationId;
import com.davenonymous.smarthome.visualization.annotations.VisualizationName;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.ModFileScanData;

import java.lang.annotation.ElementType;
import java.util.HashMap;
import java.util.Map;

public class ModVisualizations {
	private static final Map<ResourceLocation, IVisualization<?>> INSTANCE_BY_ID = new HashMap<>();
	public static final Map<Class<?>, ResourceLocation> ID_BY_CLASS = new HashMap<>();
	public static final Map<ResourceLocation, I18String> NAME_BY_ID = new HashMap<>();
	public static final Map<ResourceLocation, I18String> DESC_BY_ID = new HashMap<>();

	public static IVisualization<?> getById(ResourceLocation id) {
		return INSTANCE_BY_ID.get(id);
	}

	public static Map<ResourceLocation, IVisualization<?>> getAll() {
		return INSTANCE_BY_ID;
	}

	public static void find() {
		INSTANCE_BY_ID.clear();
		ID_BY_CLASS.clear();
		NAME_BY_ID.clear();
		DESC_BY_ID.clear();

		ModFileScanData scanData = ModList.get().getModFileById(SmartHome.MODID).getFile().getScanResult();
		var foundVizClasses = scanData.getAnnotatedBy(SmartHomeVisualization.class, ElementType.TYPE);

		foundVizClasses.forEach(annotationData -> {

			//noinspection rawtypes
			Class<IVisualization> vizClass = AnnotationHelpers.getAnnotatedClass(annotationData, IVisualization.class);
			ResourceLocation vizId = AnnotationHelpers.getSingularFieldData(vizClass, VisualizationId.class, ResourceLocation.class);
			I18String vizName = AnnotationHelpers.getSingularFieldData(vizClass, VisualizationName.class, I18String.class);
			I18String vizDescription = AnnotationHelpers.getSingularFieldData(vizClass, VisualizationDescription.class, I18String.class);

			try {
				//noinspection rawtypes
				IVisualization visualization = vizClass.getDeclaredConstructor().newInstance();
				INSTANCE_BY_ID.put(vizId, visualization);

				ID_BY_CLASS.put(vizClass, vizId);
				NAME_BY_ID.put(vizId, vizName);
				DESC_BY_ID.put(vizId, vizDescription);

				SmartHome.LOGGER.info("Found visualization: {}", visualization.getType());
			} catch (Exception e) {
				SmartHome.LOGGER.error("Failed to instantiate visualization class: {}", annotationData.clazz().getClassName(), e);
			}
		});
	}

}
