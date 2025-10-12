package com.davenonymous.smarthome.content.sensor;

import com.davenonymous.smarthome.lib.i18n.I18String;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SensorColumn(int index, String name, I18String label, SensorColumnType type, boolean isGroupingColumn) {

	public SensorColumn(String name, I18String label, SensorColumnType type) {
		this(-1, name, label, type, false);
	}

	public SensorColumn withIndex(int index) {
		return new SensorColumn(index, this.name, this.label, this.type, this.isGroupingColumn);
	}

	public SensorColumn asGroupingColumn() {
		return new SensorColumn(this.index, this.name, this.label, this.type, true);
	}

	public static final MapCodec<SensorColumn> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.INT.fieldOf("index").forGetter(SensorColumn::index),
		Codec.STRING.fieldOf("name").forGetter(SensorColumn::name),
		I18String.CODEC.fieldOf("label").forGetter(SensorColumn::label),
		SensorColumnType.CODEC.fieldOf("type").forGetter(SensorColumn::type),
		Codec.BOOL.optionalFieldOf("isGroupingColumn", false).forGetter(SensorColumn::isGroupingColumn)
	).apply(instance, SensorColumn::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, SensorColumn> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, SensorColumn::index,
		ByteBufCodecs.STRING_UTF8, SensorColumn::name,
		I18String.STREAM_CODEC, SensorColumn::label,
		SensorColumnType.STREAM_CODEC, SensorColumn::type,
		ByteBufCodecs.BOOL, SensorColumn::isGroupingColumn,
		SensorColumn::new
	);

	public static SensorColumn string(String name, I18String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.STRING);
	}

	public static SensorColumn bool(String name, I18String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.BOOLEAN);
	}

	public static SensorColumn uint(String name, I18String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.UNSIGNED_INTEGER);
	}

	public static SensorColumn integer(String name, I18String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.INTEGER);
	}

	public static SensorColumn floaty(String name, I18String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.FLOAT);
	}

	public static SensorColumn dobby(String name, I18String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.DOUBLE);
	}

	public static SensorColumn ulong(String name, I18String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.UNSIGNED_LONG);
	}

	public static SensorColumn longy(String name, I18String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.LONG);
	}

	public static SensorColumn utiny(String name, I18String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.UNSIGNED_TINYINT);
	}

	public static SensorColumn tiny(String name, I18String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.TINYINT);
	}

	public static SensorColumn ushort(String name, I18String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.UNSIGNED_SHORT);
	}

	public static SensorColumn shorty(String name, I18String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.SHORT);
	}

	public static SensorColumn timestamp(String name, I18String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.TIMESTAMP);
	}

	public static SensorColumn timestampWithZone(String name, I18String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.TIMESTAMP_WITH_TIMEZONE);
	}

	public static SensorColumn date(String name, I18String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.DATE);
	}

	public static SensorColumn uuid(String name, I18String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.UUID);
	}

	public static SensorColumn blob(String name, I18String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.BLOB);
	}

	public static SensorColumn json(String name, I18String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.JSON);
	}

	public static SensorColumn ubigint(String name, I18String translationKey) {
		return new SensorColumn(name, translationKey, SensorColumnType.UNSIGNED_LONG);
	}

}
