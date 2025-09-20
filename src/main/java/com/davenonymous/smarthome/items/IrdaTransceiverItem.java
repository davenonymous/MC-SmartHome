package com.davenonymous.smarthome.items;

import com.davenonymous.smarthome.api.IRackable;
import com.davenonymous.smarthome.lib.gui.tooltip.TableTooltipComponent;
import com.davenonymous.smarthome.lib.gui.tooltip.VBoxTooltipComponent;
import com.davenonymous.smarthome.lib.gui.tooltip.WrappedStringTooltipComponent;
import com.davenonymous.smarthome.setup.content.ModDataComponents;
import com.davenonymous.smarthome.setup.content.ModFonts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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
