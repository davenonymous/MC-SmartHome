package com.davenonymous.smarthome.datagen;

import com.davenonymous.smarthome.sensor.energy.EnergyStorage;
import com.davenonymous.smarthome.sensor.fluid.FluidStorage;
import com.davenonymous.smarthome.sensor.occupancy.Occupancy;
import com.davenonymous.smarthome.sensor.redstone.RedstonePowered;
import com.davenonymous.smarthome.setup.content.ModBlocks;
import com.davenonymous.smarthome.setup.content.ModItems;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class DGTranslations extends LanguageProvider {
	private String modid;

	public DGTranslations(PackOutput output, String modid, String locale) {
		super(output, modid, locale);
		this.modid = modid;
	}

	@Override
	protected void addTranslations() {
		add(ModBlocks.DASHBOARD.get(), "Dashboard");
		add(ModBlocks.MINI_RACK.get(), "Mini Rack");
		add(ModItems.SERVER_ITEM.get(), "Smart Home Server");
		add(ModItems.IRDA_ITEM.get(), "IrDA Transceiver");
		add(ModItems.RANGE_FINDER_ITEM.get(), "Laser Range Finder");

		add("smarthome.gui.home.title", "Smart Home");
		add("smarthome.gui.server.name_label", "Name your smart home:");
		add("smarthome.gui.home.no_homes", "No Homes Found");
		add("smarthome.gui.home.no_homes.hint", "Build a mini rack and place a Smart Home server in it to get started.");

		add("smarthome.gui.home.sidebar.zones", "Zones");

		add("smarthome.gui.home.zones.detail.devices", "Devices");
		add("smarthome.gui.home.zones.detail.renameable", "Click to rename zone");
		add("smarthome.gui.home.zones.detail.delete", "Ctrl+Shift+Click to delete zone");
		add("smarthome.gui.home.zones.add_zones.hint", "Enter name");

		add("smarthome.gui.home.devices.label.new_devices", "New:");
		add("smarthome.gui.home.devices.label.configured_devices", "Devices:");

		add("smarthome.gui.home.devices.table.tooltip.missing_device", "Missing device");

		add("smarthome.gui.home.devices.add.renameable", "Click to rename device");
		add("smarthome.gui.home.devices.add.sensors.count", "%d sensors");
		add("smarthome.gui.home.devices.add.add_device", "Add device");
		add("smarthome.gui.home.devices.add.ignore_device", "Ignore device");
		add("smarthome.gui.home.devices.add.ignore_device.hint", "Ignored devices can still be added later from the settings menu.");

		add("smarthome.gui.home.sidebar.devices", "Devices");
		add("smarthome.gui.home.sidebar.devices.badge", "New devices found");

		add("smarthome.gui.home.sidebar.settings", "Settings");

		add("smarthome.sensors.generic_sensor", "Generic");
		add("smarthome.sensors.generic_sensor.info", "Tracks basic information that all devices provide.");

		add("smarthome.sensors.no_sensors", "-");

		add("smarthome.range_finder.invalid", "Invalid");
		add("smarthome.range_finder.intersects", "The selected Range Finder area intersects with an existing zone!");

		add(Occupancy.ID, "name", "Occupancy");
		add(Occupancy.ID, "description", "Records presence of living entities in the home zones.");

		add(RedstonePowered.ID, "name", "Redstone Level");
		add(RedstonePowered.ID, "description", "Records redstone power levels of blocks.");

		add(EnergyStorage.ID, "name", "Energy Storage");
		add(EnergyStorage.ID, "description", "Records stored forge energy.");

		add(FluidStorage.ID, "name", "Fluid Storage");
		add(FluidStorage.ID, "description", "Records stored fluids.");
	}

	public void add(ResourceLocation id, String translation) {
		add(id, "name", translation);
	}

	public void add(ResourceLocation id, String suffix, String translation) {
		if(id == null || id.getPath().isEmpty()) {
			throw new IllegalArgumentException("Node ID cannot be null or empty");
		}
		var dotted = id.getPath().replaceAll("/", ".");
		String key = id.getNamespace() + "." + dotted + "." + suffix;
		add(key, translation);
	}

	public void add(Enum<?> enumValue, String translation) {
		if(enumValue == null || translation == null || translation.isEmpty()) {
			throw new IllegalArgumentException("Enum value and translation cannot be null or empty");
		}

		String key = this.modid + ".enum." + enumValue.getClass().getSimpleName().toLowerCase(Locale.ROOT) + "." + enumValue.name().toLowerCase(Locale.ROOT);
		add(key, translation);
	}

	public void addMessage(String messageId, String translation) {
		if(messageId == null || messageId.isEmpty()) {
			throw new IllegalArgumentException("Translation key cannot be null or empty");
		}

		add(this.modid + ".message." + messageId, translation);
	}

	public void add(KeyMapping keyMapping, String translation) {
		String key = keyMapping.getName();
		if(key.isEmpty()) {
			throw new IllegalArgumentException("Key mapping translation key cannot be null or empty");
		}

		add(key, translation);
	}

	public void add(MenuType<?> container, String translation) {
		add(getContainerLanguageKey(container), translation);
	}

	public void add(@NotNull ModConfigSpec.ConfigValue<?> spec, String label, String tooltip) {
		var key = spec.getSpec().getTranslationKey();
		if(key == null || key.isEmpty()) {
			throw new IllegalArgumentException("Spec translation key cannot be null or empty");
		}

		add(key, label);
		add(key + ".tooltip", tooltip);
	}

	public static String getContainerLanguageKey(MenuType<?> container) {
		ResourceLocation rLoc = BuiltInRegistries.MENU.getKey(container);
		return "container." + rLoc.getNamespace() + "." + rLoc.getPath();
	}
}
