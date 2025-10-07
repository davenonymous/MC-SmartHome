package com.davenonymous.smarthome;

import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.theme.Vanilla;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.setup.DeferredRegistries;
import com.davenonymous.smarthome.setup.dynamic.ModCardElements;
import com.davenonymous.smarthome.setup.dynamic.ModSensors;
import com.davenonymous.smarthome.setup.dynamic.ModVisualizations;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(SmartHome.MODID)
public class SmartHome {
	public static final String MODID = "smarthome";
	public static final Logger LOGGER = LogUtils.getLogger();

	public static GuiTheme theme = new Vanilla();

	public SmartHome(IEventBus modEventBus, ModContainer modContainer){
		ModVisualizations.find();
		ModSensors.find();
		ModCardElements.find();

		DeferredRegistries.register(modEventBus);
		try {
			Class.forName("org.duckdb.DuckDBDriver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
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

	public static I18String guiString(String category, String id) {
		return new I18String(MODID, "gui", category, id);
	}

	public static I18String dataString(String category, String id) {
		return new I18String(MODID, "data", category, id);
	}
}
