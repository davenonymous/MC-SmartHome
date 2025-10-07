package com.davenonymous.smarthome.api.visualization;

import com.davenonymous.smarthome.api.sensor.sensortypes.HomeSensor;
import com.davenonymous.smarthome.cards.HomeCardElement;
import com.davenonymous.smarthome.lib.gui.CircularPointedArrayList;
import com.davenonymous.smarthome.lib.gui.ColorHelper;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.setup.dynamic.ModVisualizationSettings;
import com.davenonymous.smarthome.visualization.VisualizationSettingsCodecRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public interface IVisualizationSettings {
	default ResourceLocation getType() {
		return ModVisualizationSettings.ID_BY_CLASS.get(this.getClass());
	}

	default MapCodec<? extends IVisualizationSettings> codec() {
		//noinspection unchecked
		return ModVisualizationSettings.CODEC_BY_ID.get(this.getType());
	}

	default StreamCodec<RegistryFriendlyByteBuf, ? extends IVisualizationSettings> streamCodec() {
		//noinspection unchecked
		return ModVisualizationSettings.STREAMCODEC_BY_ID.get(this.getType());
	}

	List<Widget> createSettingsWidgets(HomeSensor<?, ?> sensor, List<UUID> devices);

	Codec<IVisualizationSettings> CODEC = VisualizationSettingsCodecRegistry.VIZ_SETTINGS_SERIALIZERS.byNameCodec() // Gets Codec<MapCodec<? extends ExampleObject>>
		.dispatch(
			IVisualizationSettings::codec, // Get the codec from the specific object
			Function.identity() // Get the codec from the registry
    );

	StreamCodec<RegistryFriendlyByteBuf, IVisualizationSettings> STREAM_CODEC = ByteBufCodecs.registry(VisualizationSettingsCodecRegistry.VIZ_SETTINGS_DISPATCHER_KEY)
		.dispatch(
			IVisualizationSettings::streamCodec,
			Function.identity()
	);

	static CircularPointedArrayList<Integer> defaultColors() {
		return new CircularPointedArrayList<>(
			ColorHelper.COLOR_ORANGE, ColorHelper.COLOR_PURPLE, ColorHelper.COLOR_CYAN, ColorHelper.COLOR_GREEN, ChatFormatting.BLUE.getColor()
		);
	};
}
