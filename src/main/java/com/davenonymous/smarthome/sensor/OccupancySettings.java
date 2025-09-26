package com.davenonymous.smarthome.sensor;

import com.davenonymous.smarthome.api.SensorSettings;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class OccupancySettings extends SensorSettings {
	public static final OccupancySettings UNIT = new OccupancySettings();
	public static final MapCodec<OccupancySettings> CODEC = MapCodec.unit(OccupancySettings::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, OccupancySettings> STREAM_CODEC = StreamCodec.unit(UNIT);

	@Override
	public MapCodec<? extends SensorSettings> type() {
		return CODEC;
	}

	@Override
	public StreamCodec<RegistryFriendlyByteBuf, ? extends SensorSettings> streamCodec() {
		return STREAM_CODEC;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) || obj == UNIT;
	}
}
