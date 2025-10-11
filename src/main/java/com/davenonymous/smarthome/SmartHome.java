package com.davenonymous.smarthome;

import com.davenonymous.smarthome.config.ModConfigs;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.theme.Vanilla;
import com.davenonymous.smarthome.setup.DeferredRegistries;
import com.davenonymous.smarthome.setup.dynamic.ModCardElements;
import com.davenonymous.smarthome.setup.dynamic.ModSensors;
import com.davenonymous.smarthome.setup.dynamic.ModVisualizationSettings;
import com.davenonymous.smarthome.setup.dynamic.ModVisualizations;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(SmartHome.MODID)
public class SmartHome {
	public static final String MODID = "smarthome";
	public static final Logger LOGGER = LogUtils.getLogger();

	public static ModContainer CONTAINER;
	public static GuiTheme theme = new Vanilla();
	public static boolean uiRunning = false;

	public SmartHome(IEventBus modEventBus, ModContainer modContainer){
		CONTAINER = modContainer;

		ModVisualizationSettings.find();
		ModVisualizations.find();
		ModSensors.find();
		ModCardElements.find();

		DeferredRegistries.register(modEventBus);
		try {
			Class.forName("org.duckdb.DuckDBDriver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		modContainer.registerConfig(ModConfig.Type.SERVER, ModConfigs.SERVER_SPEC);
		modContainer.registerConfig(ModConfig.Type.CLIENT, ModConfigs.CLIENT_SPEC);
	}

	public static ResourceLocation sprite(GuiTheme.SpriteComponent component) {
		return theme.getSprite(component);
	}

	public static int color(GuiTheme.ColorComponent component) {
		return theme.getColor(component);
	}


	public static ResourceLocation resource(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}
}
