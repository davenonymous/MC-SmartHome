package com.davenonymous.smarthome.data;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.time.Instant;
import java.time.ZoneId;
import java.util.function.IntFunction;


public enum TimeRangeEnum implements StringRepresentable {
	LAST_15_MINUTES(0, "15m", 15, Labels.LAST_15M),
	LAST_30_MINUTES(1, "30m", 30, Labels.LAST_30M),
	LAST_HOUR(2, "1h", 60, Labels.LAST_1H),
	LAST_6_HOURS(3, "6h", 360, Labels.LAST_6H),
	LAST_12_HOURS(4, "12h", 720, Labels.LAST_12H),
	LAST_24_HOURS(5, "24h", 1440, Labels.LAST_24H),
	LAST_3_DAYS(6, "3d", 4320, Labels.LAST_3D),
	LAST_7_DAYS(7, "7d", 10080, Labels.LAST_7D);
	//CUSTOM(8, "custom", 0, Labels.CUSTOM_LABEL);

	public static class Labels {
		@I18DataGen(lang = "en_us", string = "Last 15 minutes")
		@I18DataGen(lang = "de_de", string = "Letzte 15 Minuten")
		public static final I18String LAST_15M = I18String.data("enum.time_range", "15m");

		@I18DataGen(lang = "en_us", string = "Last 30 minutes")
		@I18DataGen(lang = "de_de", string = "Letzte 30 Minuten")
		public static final I18String LAST_30M = I18String.data("enum.time_range", "30m");

		@I18DataGen(lang = "en_us", string = "Last hour")
		@I18DataGen(lang = "de_de", string = "Letzte Stunde")
		public static final I18String LAST_1H = I18String.data("enum.time_range", "1h");

		@I18DataGen(lang = "en_us", string = "Last 6 hours")
		@I18DataGen(lang = "de_de", string = "Letzte 6 Stunden")
		public static final I18String LAST_6H = I18String.data("enum.time_range", "6h");

		@I18DataGen(lang = "en_us", string = "Last 12 hours")
		@I18DataGen(lang = "de_de", string = "Letzte 12 Stunden")
		public static final I18String LAST_12H = I18String.data("enum.time_range", "12h");

		@I18DataGen(lang = "en_us", string = "Last 24 hours")
		@I18DataGen(lang = "de_de", string = "Letzte 24 Stunden")
		public static final I18String LAST_24H = I18String.data("enum.time_range", "24h");

		@I18DataGen(lang = "en_us", string = "Last 3 days")
		@I18DataGen(lang = "de_de", string = "Letzte 3 Tage")
		public static final I18String LAST_3D = I18String.data("enum.time_range", "3d");

		@I18DataGen(lang = "en_us", string = "Last 7 days")
		@I18DataGen(lang = "de_de", string = "Letzte 7 Tage")
		public static final I18String LAST_7D = I18String.data("enum.time_range", "7d");

		@I18DataGen(lang = "en_us", string = "Custom")
		@I18DataGen(lang = "de_de", string = "Genau")
		public static final I18String CUSTOM_LABEL = I18String.data("enum.time_range", "custom");
	}


	private final int id;
	private final String key;
	private final int minutes;
	private final I18String translation;

	TimeRangeEnum(int id, String key, int minutes, I18String translation) {
		this.id = id;
		this.key = key;
		this.minutes = minutes;
		this.translation = translation;
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
	public int minutes() {
		return minutes;
	}
	public I18String label() {
		return translation;
	}

	public Instant from() {
		var now = Instant.now().atZone(ZoneId.systemDefault()).toInstant();
		return now.minusSeconds(minutes * 60L);
	}

	public Instant to() {
		return Instant.now().atZone(ZoneId.systemDefault()).toInstant();
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
