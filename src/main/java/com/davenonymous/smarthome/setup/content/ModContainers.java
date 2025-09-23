package com.davenonymous.smarthome.setup.content;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.blocks.minirack.MiniRackContainer;
import com.davenonymous.smarthome.gui.MiniRackScreen;
import com.davenonymous.smarthome.gui.ServerScreen;
import com.davenonymous.smarthome.items.projectbox.ProjectBoxContainer;
import com.davenonymous.smarthome.items.projectbox.ProjectBoxScreen;
import com.davenonymous.smarthome.items.ServerContainer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@EventBusSubscriber(modid = SmartHome.MODID)
public class ModContainers {
	public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, SmartHome.MODID);

	public static final Supplier<MenuType<MiniRackContainer>> MINI_RACK_CONTAINER = CONTAINERS.register(
		"mini_rack", resourceLocation -> IMenuTypeExtension.create(
			(i, inventory, registryFriendlyByteBuf) -> new MiniRackContainer(i, registryFriendlyByteBuf.readBlockPos(), inventory, inventory.player)
		)
	);

	public static final Supplier<MenuType<ServerContainer>> SERVER_CONTAINER = CONTAINERS.register(
		"server", resourceLocation -> IMenuTypeExtension.create(
			(i, inventory, registryFriendlyByteBuf) -> new ServerContainer(i, inventory, inventory.player)
		)
	);

	public static final Supplier<MenuType<ProjectBoxContainer>> PROJECT_BOX_CONTAINER = CONTAINERS.register(
		"pipegoggles", resourceLocation -> IMenuTypeExtension.create(
			(i, inventory, registryFriendlyByteBuf) -> new ProjectBoxContainer(i, inventory, inventory.player)
		)
	);

	@SubscribeEvent
	public static void attachScreens(RegisterMenuScreensEvent event) {
		event.register(MINI_RACK_CONTAINER.get(), MiniRackScreen::new);
		event.register(SERVER_CONTAINER.get(), ServerScreen::new);
		event.register(PROJECT_BOX_CONTAINER.get(), ProjectBoxScreen::new);
	}
}
