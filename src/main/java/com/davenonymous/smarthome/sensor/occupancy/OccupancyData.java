package com.davenonymous.smarthome.sensor.occupancy;

import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.lib.BiggerStreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record OccupancyData(List<Occupant> occupants) implements ISensorData {

	@Override
	public String displayString() {
		return "" + occupants.size();
	}

	public static final StreamCodec<RegistryFriendlyByteBuf, OccupancyData> STREAM_CODEC = StreamCodec.composite(
		Occupant.STREAM_CODEC.apply(ByteBufCodecs.list()), OccupancyData::occupants,
		OccupancyData::new
	);

	@Override
	public StreamCodec<RegistryFriendlyByteBuf, ? extends ISensorData> streamCodec() {
		return STREAM_CODEC;
	}

	public record Occupant(int id, String name, String type, String category, double x, double y, double z) {

		public static final StreamCodec<RegistryFriendlyByteBuf, Occupant> STREAM_CODEC = BiggerStreamCodec.composite(
			ByteBufCodecs.INT, Occupant::id,
			ByteBufCodecs.STRING_UTF8, Occupant::name,
			ByteBufCodecs.STRING_UTF8, Occupant::type,
			ByteBufCodecs.STRING_UTF8, Occupant::category,
			ByteBufCodecs.DOUBLE, Occupant::x,
			ByteBufCodecs.DOUBLE, Occupant::y,
			ByteBufCodecs.DOUBLE, Occupant::z,
			Occupant::new
		);
	}
}
