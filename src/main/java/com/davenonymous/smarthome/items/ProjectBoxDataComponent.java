package com.davenonymous.smarthome.items;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record ProjectBoxDataComponent(ItemStack microController, List<ItemStack> peripherals) {
	public ProjectBoxDataComponent {
		if(microController == null) {
			microController = ItemStack.EMPTY;
		}
		if(peripherals == null) {
			peripherals = new ArrayList<>(List.of(ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY));
		} else if(peripherals.size() < 4) {
			List<ItemStack> filled = new ArrayList<>(peripherals);
			while(filled.size() < 4) {
				filled.add(ItemStack.EMPTY);
			}
			peripherals = filled;
		} else if(peripherals.size() > 4) {
			peripherals = peripherals.subList(0, 4);
		}
	}

	public static final MapCodec<ProjectBoxDataComponent> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ItemStack.CODEC.fieldOf("microController").forGetter(ProjectBoxDataComponent::microController),
		ItemStack.CODEC.listOf().fieldOf("peripherals").forGetter(ProjectBoxDataComponent::peripherals)
	).apply(instance, ProjectBoxDataComponent::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ProjectBoxDataComponent> STREAM_CODEC = StreamCodec.composite(
		ItemStack.STREAM_CODEC, ProjectBoxDataComponent::microController,
		ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), ProjectBoxDataComponent::peripherals,
		ProjectBoxDataComponent::new
	);
}
