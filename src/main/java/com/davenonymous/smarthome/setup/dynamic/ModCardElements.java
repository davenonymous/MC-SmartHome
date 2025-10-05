package com.davenonymous.smarthome.setup.dynamic;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.cards.SmartHomeCardElement;
import com.davenonymous.smarthome.cards.HomeCardElementCodecRegistry;
import com.davenonymous.smarthome.cards.HomeCardElement;
import com.davenonymous.smarthome.cards.annotations.*;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.util.AnnotationHelpers;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.ModFileScanData;

import java.lang.annotation.ElementType;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class ModCardElements {
	public static Map<Class<?>, ResourceLocation> ID_BY_CLASS = new HashMap<>();
	public static Map<ResourceLocation, I18String> NAME_BY_ID = new HashMap<>();
	public static Map<ResourceLocation, ResourceLocation> ICON_BY_ID = new HashMap<>();

	public static Map<ResourceLocation, MapCodec> CODEC_BY_ID = new HashMap<>();
	public static Map<ResourceLocation, StreamCodec> STREAM_CODEC_BY_ID = new HashMap<>();

	public static Map<ResourceLocation, Method> DEFAULT_CONSTRUCTOR_BY_ID = new HashMap<>();

	public static List<Pair<ResourceLocation, I18String>> getAllSorted() {
		return NAME_BY_ID.entrySet().stream()
				.map(e -> Pair.of(e.getKey(), e.getValue()))
				.sorted(Comparator.comparing(a -> a.getSecond().get(), Comparator.naturalOrder()))
				.toList();
	}

	public static <T> T createDefault(Class<T> clazz) {
		return createDefault(ID_BY_CLASS.get(clazz));
	}

	public static <T> T createDefault(ResourceLocation id) {
		Method constructor = DEFAULT_CONSTRUCTOR_BY_ID.get(id);
		if(constructor == null) {
			throw new IllegalArgumentException("No default constructor found for card element id: " + id);
		}

		try {
			//noinspection unchecked
			return (T) constructor.invoke(null);
		} catch (Exception e) {
			throw new RuntimeException("Failed to invoke default constructor for card element id: " + id, e);
		}
	}

	public static void find() {
		ModFileScanData scanData = ModList.get().getModFileById(SmartHome.MODID).getFile().getScanResult();
		var sensorClassAnnotations = scanData.getAnnotatedBy(SmartHomeCardElement.class, ElementType.TYPE);

		sensorClassAnnotations.forEach(annotationData -> {
			Class<HomeCardElement> cardClazz = AnnotationHelpers.getAnnotatedClass(annotationData, HomeCardElement.class);

			ResourceLocation cardId = AnnotationHelpers.getSingularFieldData(cardClazz, HomeCardElementId.class, ResourceLocation.class);
			I18String cardName = AnnotationHelpers.getSingularFieldData(cardClazz, HomeCardElementName.class, I18String.class);
			MapCodec cardCodec = AnnotationHelpers.getSingularFieldData(cardClazz, HomeCardElementCodec.class, MapCodec.class);
			StreamCodec cardStreamCodec = AnnotationHelpers.getSingularFieldData(cardClazz, HomeCardElementStreamCodec.class, StreamCodec.class);
			Method defaultConstructor = AnnotationHelpers.getSingularMethod(cardClazz, HomeCardElementDefault.class, true);
			ResourceLocation iconId = AnnotationHelpers.getSingularFieldData(cardClazz, HomeCardElementIcon.class, ResourceLocation.class);

			var sensorClassSimpleName = cardClazz.getSimpleName();
			var sensorClassSnakeCaseName = snakeCase(sensorClassSimpleName);

			try {
				ID_BY_CLASS.put(cardClazz, cardId);
				NAME_BY_ID.put(cardId, cardName);
				ICON_BY_ID.put(cardId, iconId);
				CODEC_BY_ID.put(cardId, cardCodec);
				STREAM_CODEC_BY_ID.put(cardId, cardStreamCodec);
				DEFAULT_CONSTRUCTOR_BY_ID.put(cardId, defaultConstructor);

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
