package com.davenonymous.smarthome.cards;

import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.setup.dynamic.ModCardElements;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.Function;

public interface HomeCardElement<T extends Widget> {

	default ResourceLocation getId() {
		return ModCardElements.ID_BY_CLASS.get(this.getClass());
	}

	default I18String getDisplayName() {
		return ModCardElements.NAME_BY_CLASS.get(this.getClass());
	}

	default StreamCodec<RegistryFriendlyByteBuf, ? extends HomeCardElement<?>> streamCodec() {
		//noinspection unchecked
		return ModCardElements.STREAM_CODEC_BY_CLASS.get(this.getClass());
	}

	default MapCodec<? extends HomeCardElement<?>> codec() {
		//noinspection unchecked
		return ModCardElements.CODEC_BY_CLASS.get(this.getClass());
	}

	default List<Widget> createSettingWidgets() {
		return List.of();
	}

	T createWidget();

	Codec<HomeCardElement<?>> CODEC = HomeCardElementCodecRegistry.HOMECARD_ELEMENT_SERIALIZERS.byNameCodec() // Gets Codec<MapCodec<? extends ExampleObject>>
		.dispatch(
			HomeCardElement::codec, // Get the codec from the specific object
			Function.identity() // Get the codec from the registry
		);

	StreamCodec<RegistryFriendlyByteBuf, HomeCardElement<?>> STREAM_CODEC = ByteBufCodecs.registry(HomeCardElementCodecRegistry.HOMECARD_ELEMENT_DISPATCHER_KEY)
		.dispatch(
			HomeCardElement::streamCodec,
			Function.identity()
		);
}
