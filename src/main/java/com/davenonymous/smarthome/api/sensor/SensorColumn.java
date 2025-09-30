package com.davenonymous.smarthome.api.sensor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SensorColumn(String name, String translationKey, SensorColumnType type) {

	public static final MapCodec<SensorColumn> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.STRING.fieldOf("name").forGetter(SensorColumn::name),
		Codec.STRING.fieldOf("translation_key").forGetter(SensorColumn::translationKey),
		SensorColumnType.CODEC.fieldOf("type").forGetter(SensorColumn::type)
	).apply(instance, SensorColumn::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, SensorColumn> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, SensorColumn::name,
		ByteBufCodecs.STRING_UTF8, SensorColumn::translationKey,
		SensorColumnType.STREAM_CODEC, SensorColumn::type,
		SensorColumn::new
	);

	public static SensorColumn string(String name, String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.STRING);
	}

	public static SensorColumn bool(String name, String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.BOOLEAN);
	}

	public static SensorColumn uint(String name, String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.UNSIGNED_INTEGER);
	}

	public static SensorColumn integer(String name, String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.INTEGER);
	}

	public static SensorColumn floaty(String name, String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.FLOAT);
	}

	public static SensorColumn dobby(String name, String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.DOUBLE);
	}

	public static SensorColumn ulong(String name, String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.UNSIGNED_LONG);
	}

	public static SensorColumn longy(String name, String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.LONG);
	}

	public static SensorColumn utiny(String name, String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.UNSIGNED_TINYINT);
	}

	public static SensorColumn tiny(String name, String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.TINYINT);
	}

	public static SensorColumn ushort(String name, String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.UNSIGNED_SHORT);
	}

	public static SensorColumn shorty(String name, String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.SHORT);
	}

	public static SensorColumn timestamp(String name, String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.TIMESTAMP);
	}

	public static SensorColumn date(String name, String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.DATE);
	}

	public static SensorColumn uuid(String name, String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.UUID);
	}

	public static SensorColumn blob(String name, String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.BLOB);
	}

	public static SensorColumn json(String name, String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.JSON);
	}

	public static SensorColumn ubigint(String name, String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.UNSIGNED_LONG);
	}

}
