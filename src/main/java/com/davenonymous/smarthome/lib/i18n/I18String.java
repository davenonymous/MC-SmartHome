package com.davenonymous.smarthome.lib.i18n;

import com.davenonymous.smarthome.SmartHome;
import com.mojang.serialization.Codec;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record I18String(String key) {

	public I18String(String modId, String type, String category, String id) {
		this(modId + "." + type + "." + category + (id.isEmpty() ? "" : ("." + id)));
	}

	public I18String(String modId, String type, String category) {
		this(modId, type, category, "");
	}

	public static I18String gui(String category, String id) {
		return new I18String(SmartHome.MODID, "gui", category, id);
	}

	public static I18String data(String category, String id) {
		return new I18String(SmartHome.MODID, "data", category, id);
	}

	public static I18String config(String category, String id) {
		return new I18String(SmartHome.MODID, "config", category, id);
	}

	public static I18String configCategory(String category) {
		return new I18String(SmartHome.MODID, "configuration", category);
	}

	public String get() {
		return I18n.get(this.key);
	}

	public String get(Object... parameters) {
		return I18n.get(this.key, parameters);
	}

	public static final Codec<I18String> CODEC = Codec.STRING.xmap(I18String::new, I18String::key);
	public static final StreamCodec<RegistryFriendlyByteBuf, I18String> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, I18String::key,
		I18String::new
	);
}
