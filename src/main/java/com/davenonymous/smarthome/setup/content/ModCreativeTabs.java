package com.davenonymous.smarthome.setup.content;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.gui.DashboardScreen;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(
		Registries.CREATIVE_MODE_TAB,
		SmartHome.MODID
	);

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SMART_HOME_TAB = CREATIVE_MODE_TABS.register(
		SmartHome.MODID,
		() -> CreativeModeTab.builder()
			.title(Component.translatable(DashboardScreen.TITLE.key()))
			.withTabsBefore(CreativeModeTabs.COMBAT)
			.icon(() -> new ItemStack(ModBlocks.DASHBOARD.get()))
			.displayItems((parameters, output) -> {
				output.accept(ModItems.WALL_DASHBOARD_ITEM.get());
				output.accept(ModItems.MINI_RACK_ITEM.get());
				output.accept(ModItems.PROJECTOR_ITEM.get());
				output.accept(ModItems.SERVER_ITEM.get());
				output.accept(ModItems.IRDA_ITEM.get());
				output.accept(ModItems.RANGE_FINDER_ITEM.get());
			})
			.build()
	);

}
