package com.davenonymous.smarthome.data;

import com.davenonymous.smarthome.util.MoreCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public record TimeRange(TimeRangeEnum mode, Instant customFrom, Instant customTo) {
	public TimeRange(TimeRangeEnum mode) {
		this(mode, Instant.EPOCH, Instant.EPOCH);
	}

	public TimeRange(CompoundTag nbt) {
		this(
			TimeRangeEnum.byId(nbt.getInt("Mode")),
			nbt.contains("CustomFrom") ? Instant.ofEpochMilli(nbt.getLong("CustomFrom")) : Instant.EPOCH,
			nbt.contains("CustomTo") ? Instant.ofEpochMilli(nbt.getLong("CustomTo")) : Instant.EPOCH
		);
	}

	public Instant from() {
		return mode.from();
	}

	public Instant to() {
		return Instant.now();
	}

	@Override
	public @NotNull String toString() {
		return "TimeRange{from=" + from() + ", to=" + to() + "}";
	}

	public CompoundTag writeToNBT() {
		CompoundTag nbt = new CompoundTag();
		nbt.putInt("Mode", mode.id());
		return nbt;
	}

	public static final MapCodec<TimeRange> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
			TimeRangeEnum.CODEC.fieldOf("mode").forGetter(TimeRange::mode),
			MoreCodecs.INSTANT_CODEC.optionalFieldOf("customFrom", Instant.EPOCH).forGetter(TimeRange::from),
			MoreCodecs.INSTANT_CODEC.optionalFieldOf("customTo", Instant.EPOCH).forGetter(TimeRange::to)
		).apply(instance, TimeRange::new)
	);

	public static final StreamCodec<ByteBuf, TimeRange> STREAM_CODEC = StreamCodec.composite(
		TimeRangeEnum.STREAM_CODEC, TimeRange::mode,
		MoreCodecs.INSTANT_STREAM_CODEC, TimeRange::customFrom,
		MoreCodecs.INSTANT_STREAM_CODEC, TimeRange::customTo,
		TimeRange::new
	);

}
