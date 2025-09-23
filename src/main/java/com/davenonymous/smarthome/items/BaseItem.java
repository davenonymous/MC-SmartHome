package com.davenonymous.smarthome.items;

import com.davenonymous.smarthome.lib.gui.tooltip.VBoxTooltipComponent;
import com.davenonymous.smarthome.lib.gui.tooltip.WrappedStringTooltipComponent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class BaseItem extends Item {
	public BaseItem(Properties properties) {
		super(properties);
	}

	public BaseItem() {
		super(new Properties());
	}

	@Override
	public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		var result = new VBoxTooltipComponent();

		int lineNum = 0;
		String key = this.getDescriptionId() + ".description";
		while(I18n.exists(key)) {
			if(lineNum == 1) {
				result.add(WrappedStringTooltipComponent.yellow(I18n.get(key)));
			} else {
				result.add(WrappedStringTooltipComponent.gray(I18n.get(key)));
			}

			lineNum++;
			key = this.getDescriptionId() + ".description." + lineNum;
		}

		return Optional.of(result);
	}
}
