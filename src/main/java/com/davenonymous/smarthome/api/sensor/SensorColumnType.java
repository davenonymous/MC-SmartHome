package com.davenonymous.smarthome.api.sensor;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.UUID;
import java.util.function.IntFunction;

public enum SensorColumnType implements StringRepresentable {
	BOOLEAN(0, "boolean", false, "BOOLEAN"),

	DOUBLE(1, "double", true, "DOUBLE"),
	FLOAT(2, "float", true, "FLOAT"),
	TINYINT(3, "tinyint", true, "TINYINT"),
	SHORT(4, "short", true, "SMALLINT"),
	INTEGER(5, "integer", true, "INTEGER"),
	LONG(6, "long", true, "BIGINT"),
	UNSIGNED_TINYINT(7, "utinyint", true, "UTINYINT"),
	UNSIGNED_SHORT(8, "ushort", true, "USMALLINT"),
	UNSIGNED_INTEGER(9, "uinteger", true, "UINTEGER"),
	UNSIGNED_LONG(10, "ulong", true, "UBIGINT"),

	TIMESTAMP(11, "timestamp", false, "TIMESTAMP"),
	DATE(12, "date", false, "DATE"),

	BLOB(13, "blob", false, "BLOB"),

	UUID(14, "uuid", false, "UUID"),

	JSON(15, "json", false, "JSON"),
	STRING(16, "string", false, "TEXT"),
	;


	private final int id;
	private final String key;
	private final boolean isNumeric;
	private final String sqlType;

	public int id() {
		return id;
	}

	public boolean isNumeric() {
		return isNumeric;
	}

	public String key() {
		return key;
	}

	public String sqlType() {
		return sqlType;
	}

	public static final IntFunction<SensorColumnType> BY_ID = ByIdMap.continuous(
		SensorColumnType::id,
		SensorColumnType.values(),
		ByIdMap.OutOfBoundsStrategy.ZERO
	);

	@SuppressWarnings("deprecation")
	public static final EnumCodec<SensorColumnType> CODEC = StringRepresentable.fromEnum(SensorColumnType::values);

	public static final StreamCodec<ByteBuf, SensorColumnType> STREAM_CODEC =
		ByteBufCodecs.idMapper(SensorColumnType.BY_ID, SensorColumnType::id);


	SensorColumnType(int id, String key, boolean isNumeric, String sqlType) {
		this.id = id;
		this.key = key;
		this.isNumeric = isNumeric;
		this.sqlType = sqlType;
	}

	public static SensorColumnType byId(int id) {
		return BY_ID.apply(id);
	}

	public static SensorColumnType byValueClass(Class<?> clazz) {
		if(clazz == null) {
			return null;
		}

		if(clazz == Boolean.class || clazz == boolean.class) {
			return BOOLEAN;
		} else if(clazz == Double.class || clazz == double.class) {
			return DOUBLE;
		} else if(clazz == Float.class || clazz == float.class) {
			return FLOAT;
		} else if(clazz == Byte.class || clazz == byte.class) {
			return TINYINT;
		} else if(clazz == Short.class || clazz == short.class) {
			return SHORT;
		} else if(clazz == Integer.class || clazz == int.class) {
			return INTEGER;
		} else if(clazz == Long.class || clazz == long.class) {
			return LONG;
		} else if(clazz == String.class) {
			return STRING;
		} else if(clazz == UUID.class) {
			return UUID;
		}

		return null;
	}

	public SensorColumnType next() {
		return byId((id + 1) % values().length);
	}

	@Override
	public String getSerializedName() {
		return this.key;
	}
}
