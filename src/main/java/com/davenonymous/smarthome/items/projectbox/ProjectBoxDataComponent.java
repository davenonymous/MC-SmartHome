package com.davenonymous.smarthome.items.projectbox;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.setup.content.ModDataComponents;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record ProjectBoxDataComponent(String name, ItemStack microController, List<ItemStack> peripherals) {
	public ProjectBoxDataComponent() {
		this("", ItemStack.EMPTY, new ArrayList<>(List.of()));
	}

	public ProjectBoxDataComponent {
		if(microController == null) {
			microController = ItemStack.EMPTY;
		}
		if(peripherals == null) {
			peripherals = new ArrayList<>(List.of());
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

	public ProjectBoxInventoryHandler getInventoryHandler(ItemStack projectBoxStack) {
		return new ProjectBoxInventoryHandler((slot, stack) -> {
			SmartHome.LOGGER.info("Requesting inventory change for slot " + slot);
			ProjectBoxDataComponent data = projectBoxStack.get(ModDataComponents.PROJECT_BOX_DATA_COMPONENT);
			if(data == null) {
				data = new ProjectBoxDataComponent();
			}

			if(slot == 0) {
				data = data.withMicroController(stack.copy());
			} else if(slot >= 1 && slot <= 4) {
				data = data.withPeripheral(slot - 1, stack.copy());
			}
			projectBoxStack.set(ModDataComponents.PROJECT_BOX_DATA_COMPONENT, data);
			SmartHome.LOGGER.info("Inventory change applied");
		}, this);
	}

	public ProjectBoxDataComponent withName(String name) {
		return new ProjectBoxDataComponent(name, microController, peripherals);
	}

	private ProjectBoxDataComponent withMicroController(ItemStack microController) {
		return new ProjectBoxDataComponent(name, microController, peripherals);
	}

	private ProjectBoxDataComponent withPeripheral(int index, ItemStack peripheral) {
		List<ItemStack> newPeripherals = new ArrayList<>(peripherals.stream().map(ItemStack::copy).toList());
		newPeripherals.set(index, peripheral.copy());
		return new ProjectBoxDataComponent(name, microController, newPeripherals);
	}

	public Component placementErrorMessage() {
		if(name == null || name.isBlank()) {
			return Component.translatable("smarthome.message.project_box.no_name");
		}
		if(microController.isEmpty()) {
			return Component.translatable("smarthome.message.project_box.no_microcontroller");
		}
		if(peripherals.stream().allMatch(ItemStack::isEmpty)) {
			return Component.translatable("smarthome.message.project_box.no_peripherals");
		}
		return null;
	}

	public static final MapCodec<ProjectBoxDataComponent> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.STRING.fieldOf("name").forGetter(ProjectBoxDataComponent::name),
		ItemStack.OPTIONAL_CODEC.fieldOf("microController").forGetter(ProjectBoxDataComponent::microController),
		ItemStack.OPTIONAL_CODEC.listOf().fieldOf("peripherals").forGetter(ProjectBoxDataComponent::peripherals)
	).apply(instance, ProjectBoxDataComponent::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ProjectBoxDataComponent> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, ProjectBoxDataComponent::name,
		ItemStack.OPTIONAL_STREAM_CODEC, ProjectBoxDataComponent::microController,
		ItemStack.OPTIONAL_LIST_STREAM_CODEC, ProjectBoxDataComponent::peripherals,
		ProjectBoxDataComponent::new
	);
}
