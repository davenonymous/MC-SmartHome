package com.davenonymous.smarthome.data;

import com.davenonymous.smarthome.cards.HomeCardElement;
import com.davenonymous.smarthome.gui.home.main.cards.HomeCardWidget;
import com.davenonymous.smarthome.lib.HackerNoon;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.util.MoreCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record HomeCard(UUID id, String label, ResourceLocation icon, Vec2 size, Map<Vec2, HomeCardElement<?>> elements, List<EntityId> entities) {

	public HomeCard(String label) {
		this(UUID.randomUUID(), label, HackerNoon.Solid.star, new Vec2(144, 72), Map.of(), List.of());
	}

	public HomeCardWidget createWidget() {
		return new HomeCardWidget(this);
	}

	public int width() {
		return (int)size.x;
	}

	public int height() {
		return (int)size.y;
	}

	public HomeCard withSize(Vec2 newSize) {
		return new HomeCard(id, label, icon, newSize, elements, entities);
	}

	public HomeCard withLabel(String newLabel) {
		return new HomeCard(id, newLabel, icon, size, elements, entities);
	}

	public HomeCard withIcon(ResourceLocation newIcon) {
		return new HomeCard(id, label, newIcon, size, elements, entities);
	}

	public static final MapCodec<HomeCard> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			UUIDUtil.CODEC.fieldOf("id").forGetter(HomeCard::id),
			Codec.STRING.fieldOf("label").forGetter(HomeCard::label),
			ResourceLocation.CODEC.fieldOf("icon").forGetter(HomeCard::icon),
			MoreCodecs.VEC2_CODEC.codec().optionalFieldOf("size", new Vec2(144, 72)).forGetter(HomeCard::size),
			Codec.unboundedMap(MoreCodecs.VEC2_CODEC.codec(), HomeCardElement.CODEC).optionalFieldOf("elements", Map.of()).forGetter(HomeCard::elements),
			EntityId.LIST_CODEC.optionalFieldOf("entities", List.of()).forGetter(HomeCard::entities)
	).apply(instance, HomeCard::new));

	public static final Codec<List<HomeCard>> LIST_CODEC = Codec.list(CODEC.codec());

	public static final StreamCodec<RegistryFriendlyByteBuf, HomeCard> STREAM_CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC, HomeCard::id,
			ByteBufCodecs.STRING_UTF8, HomeCard::label,
			ResourceLocation.STREAM_CODEC, HomeCard::icon,
			MoreCodecs.VEC2_STREAM_CODEC, HomeCard::size,
			ByteBufCodecs.map(HashMap::new, MoreCodecs.VEC2_STREAM_CODEC, HomeCardElement.STREAM_CODEC), HomeCard::elements,
			EntityId.STREAM_CODEC.apply(ByteBufCodecs.list()), HomeCard::entities,
			HomeCard::new
	);
}
