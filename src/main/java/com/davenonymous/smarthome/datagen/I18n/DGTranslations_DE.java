package com.davenonymous.smarthome.datagen.I18n;

import com.davenonymous.smarthome.sensor.energy.EnergyStorage;
import com.davenonymous.smarthome.sensor.fluid.FluidStorage;
import com.davenonymous.smarthome.sensor.occupancy.Occupancy;
import com.davenonymous.smarthome.sensor.redstone.RedstonePowered;
import com.davenonymous.smarthome.setup.content.ModBlocks;
import com.davenonymous.smarthome.setup.content.ModItems;
import net.minecraft.data.PackOutput;

public class DGTranslations_DE extends DGBaseTranslations {
	public DGTranslations_DE(PackOutput output) {
		super(output, "de_de");
	}

	@Override
	protected void addTranslations() {
		super.addTranslations();

		add(ModBlocks.DASHBOARD.get(), "Dashboard");
		add(ModBlocks.MINI_RACK.get(), "Mini-Rack");
		add(ModItems.SERVER_ITEM.get(), "Smart Home Server");
		add(ModItems.IRDA_ITEM.get(), "IrDA Transceiver");
		add(ModItems.RANGE_FINDER_ITEM.get(), "Laser Reichweiten Sucher");

//		add("smarthome.sensors.generic_sensor", "Generisch");
//		add("smarthome.sensors.generic_sensor.info", "Nimmt grundlegende Informationen auf, die von allen Geräten bereitgestellt werden.");

		add(Occupancy.ID, "name", "Anwesenheit");
		add(Occupancy.ID, "description", "Erfasst die Anwesenheit von Lebewesen in den Hauszonen.");

		add(RedstonePowered.ID, "name", "Redstone Signal");
		add(RedstonePowered.ID, "description", "Erfasst Redstone-Signalstärken von Blöcken.");

		add(EnergyStorage.ID, "name", "Energie Speicher");
		add(EnergyStorage.ID, "description", "Erfasst gespeicherte Forge-Energie.");

		add(FluidStorage.ID, "name", "Flüssigkeits Speicher");
		add(FluidStorage.ID, "description", "Erfasst gespeicherte Flüssigkeiten.");
	}

}
