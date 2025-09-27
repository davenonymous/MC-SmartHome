package com.davenonymous.smarthome.api.visualization;

import com.davenonymous.smarthome.visualization.VisualizationDataCodecRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public interface IVisualizationData {
	StreamCodec<RegistryFriendlyByteBuf, ? extends IVisualizationData> streamCodec();

	StreamCodec<RegistryFriendlyByteBuf, IVisualizationData> STREAM_CODEC = ByteBufCodecs.registry(VisualizationDataCodecRegistry.VIZ_DATA_DISPATCHER_KEY)
		.dispatch(
			IVisualizationData::streamCodec,
			Function.identity()
	);
}
