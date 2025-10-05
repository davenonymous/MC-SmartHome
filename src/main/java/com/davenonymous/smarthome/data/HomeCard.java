package com.davenonymous.smarthome.data;

import com.davenonymous.smarthome.cards.HomeCardElement;
import com.davenonymous.smarthome.gui.home.main.cards.HomeCardWidget;
import com.davenonymous.smarthome.lib.HackerNoon;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.util.MoreCodecs;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public record HomeCard(UUID id, String label, ResourceLocation icon, Vec2 size, Map<UUID, Pair<Vec2, HomeCardElement<?>>> elements, List<EntityId> entities) {

	public HomeCard(String label) {
		this(UUID.randomUUID(), label, HackerNoon.Solid.star, new Vec2(144, 72), Map.of(), List.of());
	}

	public HomeCardWidget createWidget(boolean editing) {
		return new HomeCardWidget(this, editing);
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

	public HomeCard setElementPosition(UUID elementId, Vec2 newPosition) {
		if(!elements.containsKey(elementId)) {
			return this;
		}

		var newElements = new HashMap<>(this.elements);
		var pair = this.elements.get(elementId);
		newElements.put(elementId, Pair.of(newPosition, pair.getSecond()));

		return new HomeCard(id, label, icon, size, newElements, entities);
	}

	public HomeCard withElement(Vec2 position, HomeCardElement<?> element) {
		var newElements = new HashMap<>(this.elements);
		newElements.put(element.id(), Pair.of(position, element));
		return new HomeCard(id, label, icon, size, newElements, entities);
	}

	public HomeCard withoutElement(HomeCardElement<?> element) {
		var newElements = new HashMap<>(this.elements);
		newElements.remove(element.id());
		return new HomeCard(id, label, icon, size, newElements, entities);
	}

	public HomeCard withEntity(EntityId entity) {
		if(entities.contains(entity)) {
			return this;
		}

		var newEntities = new ArrayList<>(this.entities);
		newEntities.add(entity);
		return new HomeCard(id, label, icon, size, elements, newEntities);
	}

	public HomeCard withoutEntity(EntityId entity) {
		if(!entities.contains(entity)) {
			return this;
		}

		var newEntities = new ArrayList<>(this.entities);
		newEntities.remove(entity);
		return new HomeCard(id, label, icon, size, elements, newEntities);
	}

	public static final Codec<Pair<Vec2, HomeCardElement<?>>> ELEMENT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
		MoreCodecs.VEC2_CODEC.codec().fieldOf("position").forGetter(Pair::getFirst),
		HomeCardElement.CODEC.fieldOf("element").forGetter(Pair::getSecond)
	).apply(instance, Pair::of));

	public static final StreamCodec<RegistryFriendlyByteBuf, Pair<Vec2, HomeCardElement<?>>> ELEMENT_STREAM_CODEC = StreamCodec.composite(
		MoreCodecs.VEC2_STREAM_CODEC, Pair::getFirst,
		HomeCardElement.STREAM_CODEC, Pair::getSecond,
		Pair::new
	);

	public static final MapCodec<HomeCard> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			UUIDUtil.STRING_CODEC.fieldOf("id").forGetter(HomeCard::id),
			Codec.STRING.fieldOf("label").forGetter(HomeCard::label),
			ResourceLocation.CODEC.fieldOf("icon").forGetter(HomeCard::icon),
			MoreCodecs.VEC2_CODEC.codec().optionalFieldOf("size", new Vec2(144, 72)).forGetter(HomeCard::size),
			Codec.unboundedMap(UUIDUtil.STRING_CODEC, ELEMENT_CODEC).optionalFieldOf("elements", Map.of()).forGetter(HomeCard::elements),
			EntityId.LIST_CODEC.optionalFieldOf("entities", List.of()).forGetter(HomeCard::entities)
	).apply(instance, HomeCard::new));



	public static final Codec<List<HomeCard>> LIST_CODEC = Codec.list(CODEC.codec());

	public static final StreamCodec<RegistryFriendlyByteBuf, HomeCard> STREAM_CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC, HomeCard::id,
			ByteBufCodecs.STRING_UTF8, HomeCard::label,
			ResourceLocation.STREAM_CODEC, HomeCard::icon,
			MoreCodecs.VEC2_STREAM_CODEC, HomeCard::size,
			ByteBufCodecs.map(HashMap::new, UUIDUtil.STREAM_CODEC, ELEMENT_STREAM_CODEC), HomeCard::elements,
			EntityId.STREAM_CODEC.apply(ByteBufCodecs.list()), HomeCard::entities,
			HomeCard::new
	);
}
