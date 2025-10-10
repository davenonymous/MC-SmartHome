package com.davenonymous.smarthome.data;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.time.Instant;
import java.time.ZoneId;
import java.util.function.IntFunction;

public enum TimeRangeEnum implements StringRepresentable {
	LAST_15_MINUTES(0, "15m", 15),
	LAST_30_MINUTES(1, "30m", 30),
	LAST_HOUR(2, "1h", 60),
	LAST_6_HOURS(3, "6h", 360),
	LAST_12_HOURS(4, "12h", 720),
	LAST_24_HOURS(5, "24h", 1440),
	LAST_3_DAYS(6, "3d", 4320),
	LAST_7_DAYS(7, "7d", 10080),
	CUSTOM(8, "custom", 0),;

	private final int id;
	private final String key;
	private final int minutes;

	TimeRangeEnum(int id, String key, int minutes) {
		this.id = id;
		this.key = key;
		this.minutes = minutes;
	}

	public static TimeRangeEnum byId(int id) {
		return BY_ID.apply(id);
	}

	public int id() {
		return id;
	}
	public String key() {
		return key;
	}
	public Instant from() {
		var now = Instant.now().atZone(ZoneId.systemDefault()).toInstant();
		return now.minusSeconds(minutes * 60L);
	}
	public TimeRangeEnum next() {
		return byId((id + 1) % values().length);
	}

	@Override
	public String getSerializedName() {
		return this.key;
	}

	public static final IntFunction<TimeRangeEnum> BY_ID = ByIdMap.continuous(
		TimeRangeEnum::id,
		TimeRangeEnum.values(),
		ByIdMap.OutOfBoundsStrategy.ZERO
	);

	@SuppressWarnings("deprecation")
	public static final EnumCodec<TimeRangeEnum> CODEC = StringRepresentable.fromEnum(TimeRangeEnum::values);

	public static final StreamCodec<ByteBuf, TimeRangeEnum> STREAM_CODEC =
		ByteBufCodecs.idMapper(TimeRangeEnum.BY_ID, TimeRangeEnum::id);

}
