package com.davenonymous.smarthome.api.visualization;

import com.davenonymous.smarthome.visualization.VisualizationSettingsCodecRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public interface IVisualizationSettings {
	MapCodec<? extends IVisualizationSettings> type();
	StreamCodec<RegistryFriendlyByteBuf, ? extends IVisualizationSettings> streamCodec();

	IVisualizationSettings getDefault();

	Codec<IVisualizationSettings> CODEC = VisualizationSettingsCodecRegistry.VIZ_SETTINGS_SERIALIZERS.byNameCodec() // Gets Codec<MapCodec<? extends ExampleObject>>
		.dispatch(
			IVisualizationSettings::type, // Get the codec from the specific object
			Function.identity() // Get the codec from the registry
    );

	StreamCodec<RegistryFriendlyByteBuf, IVisualizationSettings> STREAM_CODEC = ByteBufCodecs.registry(VisualizationSettingsCodecRegistry.VIZ_SETTINGS_DISPATCHER_KEY)
		.dispatch(
			IVisualizationSettings::streamCodec,
			Function.identity()
	);
}
