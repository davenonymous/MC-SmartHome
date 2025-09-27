package com.davenonymous.smarthome.items;

import com.davenonymous.smarthome.api.items.IServer;
import com.davenonymous.smarthome.lib.gui.tooltip.TableTooltipComponent;
import com.davenonymous.smarthome.lib.gui.tooltip.VBoxTooltipComponent;
import com.davenonymous.smarthome.lib.gui.tooltip.WrappedStringTooltipComponent;
import com.davenonymous.smarthome.setup.content.ModDataComponents;
import com.davenonymous.smarthome.setup.content.ModFonts;
import net.minecraft.client.Minecraft;
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

public class ServerItem extends Item implements IServer {
	public ServerItem() {
		super(new Item.Properties());
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		return 1;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		var itemStack = player.getItemInHand(hand);
		if(hand == InteractionHand.OFF_HAND) {
			// Client-side only, no need to check server-side conditions
			return InteractionResultHolder.pass(itemStack);
		}

		if(!(itemStack.getItem() instanceof ServerItem)) {
			return InteractionResultHolder.pass(itemStack);
		}

		if(!itemStack.has(ModDataComponents.SERVER_DATA_COMPONENT)) {
			var data = new ServerDataComponent(player);
			itemStack.set(ModDataComponents.SERVER_DATA_COMPONENT, data);
		}

		if(level.isClientSide()) {
			// Client-side only, no need to check server-side conditions
			return InteractionResultHolder.pass(itemStack);
		}

		var provider = new SimpleMenuProvider((id, inventory, player1) -> new ServerContainer(id, player1.getInventory(), player1), Component.empty());
		player.openMenu(provider);

		return InteractionResultHolder.success(itemStack);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return false;
	}

	@Override
	public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		var result = new VBoxTooltipComponent();

		if(stack.has(ModDataComponents.SERVER_DATA_COMPONENT)) {
			ServerDataComponent data = stack.get(ModDataComponents.SERVER_DATA_COMPONENT);
			result.add(WrappedStringTooltipComponent.cyan(data.name()));
			if(Minecraft.getInstance().options.advancedItemTooltips) {
				var table = new TableTooltipComponent();
				table.addRow(
					WrappedStringTooltipComponent.white("Name: ").setFont(ModFonts.TINY),
					WrappedStringTooltipComponent.gray(data.name()).setFont(ModFonts.TINY)
				);
				table.addRow(
					WrappedStringTooltipComponent.white("Home: ").setFont(ModFonts.TINY),
					WrappedStringTooltipComponent.gray(data.id().toString()).setFont(ModFonts.TINY)
				);
				table.addRow(
					WrappedStringTooltipComponent.white("Owner: ").setFont(ModFonts.TINY),
					WrappedStringTooltipComponent.gray(data.owner().toString()).setFont(ModFonts.TINY)
				);
				result.add(table);
			}
		}

		return Optional.of(result);
	}
}
