package com.davenonymous.smarthome.datagen.I18n;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.setup.dynamic.ModTranslations;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public abstract class DGBaseTranslations extends ModTranslations {
	protected String modid;

	public DGBaseTranslations(PackOutput output, String locale) {
		super(output, SmartHome.MODID, locale);
		this.modid = SmartHome.MODID;
	}

	public void add(I18String label, String translation) {
		add(label.key(), translation);
	}

	public void add(ResourceLocation id, String translation) {
		add(id, "name", translation);
	}

	public void add(ResourceLocation id, String suffix, String translation) {
		if(id == null || id.getPath().isEmpty()) {
			throw new IllegalArgumentException("Node ID cannot be null or empty");
		}
		var dotted = id.getPath().replaceAll("/", ".");
		String key = id.getNamespace() + "." + dotted + "." + suffix;
		add(key, translation);
	}

	public void add(Enum<?> enumValue, String translation) {
		if(enumValue == null || translation == null || translation.isEmpty()) {
			throw new IllegalArgumentException("Enum value and translation cannot be null or empty");
		}

		String key = this.modid + ".enum." + enumValue.getClass().getSimpleName().toLowerCase(Locale.ROOT) + "." + enumValue.name().toLowerCase(Locale.ROOT);
		add(key, translation);
	}

	public void addMessage(String messageId, String translation) {
		if(messageId == null || messageId.isEmpty()) {
			throw new IllegalArgumentException("Translation key cannot be null or empty");
		}

		add(this.modid + ".message." + messageId, translation);
	}

	public void add(KeyMapping keyMapping, String translation) {
		String key = keyMapping.getName();
		if(key.isEmpty()) {
			throw new IllegalArgumentException("Key mapping translation key cannot be null or empty");
		}

		add(key, translation);
	}

	public void add(MenuType<?> container, String translation) {
		add(getContainerLanguageKey(container), translation);
	}

	public void add(@NotNull ModConfigSpec.ConfigValue<?> spec, String label, String tooltip) {
		var key = spec.getSpec().getTranslationKey();
		if(key == null || key.isEmpty()) {
			throw new IllegalArgumentException("Spec translation key cannot be null or empty");
		}

		add(key, label);
		add(key + ".tooltip", tooltip);
	}

	public static String getContainerLanguageKey(MenuType<?> container) {
		ResourceLocation rLoc = BuiltInRegistries.MENU.getKey(container);
		return "container." + rLoc.getNamespace() + "." + rLoc.getPath();
	}
}
