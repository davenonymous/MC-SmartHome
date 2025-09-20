package com.davenonymous.smarthome.items;

import com.davenonymous.smarthome.lib.gui.tooltip.VBoxTooltipComponent;
import com.davenonymous.smarthome.lib.gui.tooltip.WrappedStringTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class RangeFinderItem extends Item {
	public RangeFinderItem() {
		super(new Properties());
	}

	@Override
	public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		var result = new VBoxTooltipComponent();

		result.add(WrappedStringTooltipComponent.gray("Uses a laser to determine the distance to objects."));
		result.add(WrappedStringTooltipComponent.yellow("Use to create zones for your smart home."));

		return Optional.of(result);
	}

}
