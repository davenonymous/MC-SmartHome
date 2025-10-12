package com.davenonymous.smarthome.content.items;

import com.davenonymous.smarthome.lib.gui.tooltip.VBoxTooltipComponent;
import com.davenonymous.smarthome.lib.gui.tooltip.WrappedStringTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class IrdaTransceiverItem extends Item implements IRackable {
	public IrdaTransceiverItem() {
		super(new Properties());
	}

	@Override
	public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		var result = new VBoxTooltipComponent();

		result.add(WrappedStringTooltipComponent.gray("Provides Infrared communication capabilities to the Mini-Rack."));
		result.add(WrappedStringTooltipComponent.yellow("Connects to all devices in the same zone."));

		return Optional.of(result);
	}
}
