package com.davenonymous.smarthome;

import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.theme.Vanilla;
import com.davenonymous.smarthome.setup.DeferredRegistries;
import com.davenonymous.smarthome.setup.content.ModSensors;
import com.davenonymous.smarthome.setup.content.ModVisualizations;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.font.providers.GlyphProviderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

import java.sql.*;

@Mod(SmartHome.MODID)
public class SmartHome {
	public static final String MODID = "smarthome";
	public static final Logger LOGGER = LogUtils.getLogger();

	public static GuiTheme theme = new Vanilla();

	public SmartHome(IEventBus modEventBus, ModContainer modContainer){
		DeferredRegistries.register(modEventBus);
		try {
			Class.forName("org.duckdb.DuckDBDriver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		ModSensors.find();
		ModVisualizations.find();
	}

	public static ResourceLocation sprite(GuiTheme.SpriteComponent component) {
		return theme.getSprite(component);
	}

	public static ResourceLocation resource(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}
}
