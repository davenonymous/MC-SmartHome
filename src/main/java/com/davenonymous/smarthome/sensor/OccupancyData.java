package com.davenonymous.smarthome.sensor;

import com.davenonymous.smarthome.api.SensorData;
import com.davenonymous.smarthome.lib.BiggerStreamCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public class OccupancyData extends SensorData {
	private List<Occupant> occupants;

	public OccupancyData(List<Occupant> occupants) {
		this.occupants = occupants;
	}

	@Override
	public String displayString() {
		return "" + occupants.size();
	}

	public List<Occupant> occupants() {
		return occupants;
	}

	public static final MapCodec<OccupancyData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Occupant.CODEC.codec().listOf().fieldOf("occupants").forGetter(OccupancyData::occupants)
	).apply(instance, OccupancyData::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, OccupancyData> STREAM_CODEC = StreamCodec.composite(
		Occupant.STREAM_CODEC.apply(ByteBufCodecs.list()), OccupancyData::occupants,
		OccupancyData::new
	);

	@Override
	public MapCodec<? extends SensorData> type() {
		return CODEC;
	}

	@Override
	public StreamCodec<RegistryFriendlyByteBuf, ? extends SensorData> streamCodec() {
		return STREAM_CODEC;
	}

	public record Occupant(int id, String name, String type, String category, double x, double y, double z) {
		public static final MapCodec<Occupant> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.INT.fieldOf("id").forGetter(Occupant::id),
			Codec.STRING.fieldOf("name").forGetter(Occupant::name),
			Codec.STRING.fieldOf("type").forGetter(Occupant::type),
			Codec.STRING.fieldOf("category").forGetter(Occupant::category),
			Codec.DOUBLE.fieldOf("x").forGetter(Occupant::x),
			Codec.DOUBLE.fieldOf("y").forGetter(Occupant::y),
			Codec.DOUBLE.fieldOf("z").forGetter(Occupant::z)
		).apply(instance, Occupant::new));

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
