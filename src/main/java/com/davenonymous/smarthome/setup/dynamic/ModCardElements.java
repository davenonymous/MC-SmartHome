package com.davenonymous.smarthome.setup.dynamic;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.cards.SmartHomeCardElement;
import com.davenonymous.smarthome.cards.HomeCardElementCodecRegistry;
import com.davenonymous.smarthome.cards.HomeCardElement;
import com.davenonymous.smarthome.cards.annotations.HomeCardElementCodec;
import com.davenonymous.smarthome.cards.annotations.HomeCardElementId;
import com.davenonymous.smarthome.cards.annotations.HomeCardElementName;
import com.davenonymous.smarthome.cards.annotations.HomeCardElementStreamCodec;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.util.AnnotationHelpers;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.ModFileScanData;

import java.lang.annotation.ElementType;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class ModCardElements {
	public static Map<Class<?>, ResourceLocation> ID_BY_CLASS = new HashMap<>();
	public static Map<Class<?>, I18String> NAME_BY_CLASS = new HashMap<>();

	public static Map<Class<?>, MapCodec> CODEC_BY_CLASS = new HashMap<>();
	public static Map<Class<?>, StreamCodec> STREAM_CODEC_BY_CLASS = new HashMap<>();

	public static void find() {
		ModFileScanData scanData = ModList.get().getModFileById(SmartHome.MODID).getFile().getScanResult();
		var sensorClassAnnotations = scanData.getAnnotatedBy(SmartHomeCardElement.class, ElementType.TYPE);

		sensorClassAnnotations.forEach(annotationData -> {
			Class<HomeCardElement> cardClazz = AnnotationHelpers.getAnnotatedClass(annotationData, HomeCardElement.class);

			ResourceLocation cardId = AnnotationHelpers.getSingularFieldData(cardClazz, HomeCardElementId.class, ResourceLocation.class);
			I18String cardName = AnnotationHelpers.getSingularFieldData(cardClazz, HomeCardElementName.class, I18String.class);
			MapCodec cardCodec = AnnotationHelpers.getSingularFieldData(cardClazz, HomeCardElementCodec.class, MapCodec.class);
			StreamCodec cardStreamCodec = AnnotationHelpers.getSingularFieldData(cardClazz, HomeCardElementStreamCodec.class, StreamCodec.class);

			var sensorClassSimpleName = cardClazz.getSimpleName();
			var sensorClassSnakeCaseName = snakeCase(sensorClassSimpleName);

			try {
				ID_BY_CLASS.put(cardClazz, cardId);
				NAME_BY_CLASS.put(cardClazz, cardName);
				CODEC_BY_CLASS.put(cardClazz, cardCodec);
				STREAM_CODEC_BY_CLASS.put(cardClazz, cardStreamCodec);

				//noinspection unchecked
				HomeCardElementCodecRegistry.DEFERRED_HOMECARD_ELEMENT.register(sensorClassSnakeCaseName, () -> cardCodec);

				//noinspection unchecked
				HomeCardElementCodecRegistry.DEFERRED_HOMECARD_ELEMENT_DISPATCHER.register(sensorClassSnakeCaseName, () -> cardStreamCodec);

				SmartHome.LOGGER.info("Found card element: {}", cardId);
			} catch (Exception e) {
				SmartHome.LOGGER.error("Failed to instantiate card class: {}", annotationData.clazz().getClassName(), e);
			}
		});
	}

	private static String snakeCase(String input) {
		return input.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
	}
}
