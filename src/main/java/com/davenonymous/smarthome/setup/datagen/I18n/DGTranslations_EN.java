package com.davenonymous.smarthome.setup.datagen.I18n;

import com.davenonymous.smarthome.content.sensor.impl.energy.EnergyStorage;
import com.davenonymous.smarthome.content.sensor.impl.fluid.FluidStorage;
import com.davenonymous.smarthome.content.sensor.impl.occupancy.Occupancy;
import com.davenonymous.smarthome.content.sensor.impl.redstone.RedstoneSignal;
import com.davenonymous.smarthome.setup.content.ModBlocks;
import com.davenonymous.smarthome.setup.content.ModItems;
import net.minecraft.data.PackOutput;

public class DGTranslations_EN extends DGBaseTranslations {
	public DGTranslations_EN(PackOutput output) {
		super(output, "en_us");
	}

	@Override
	protected void addTranslations() {
		super.addTranslations();

		add(ModBlocks.DASHBOARD.get(), "Dashboard");
		add(ModBlocks.MINI_RACK.get(), "Mini Rack");
		add(ModBlocks.PROJECTOR.get(), "Projector");
		add(ModItems.SERVER_ITEM.get(), "Smart Home Server");
		add(ModItems.IRDA_ITEM.get(), "IrDA Transceiver");
		add(ModItems.RANGE_FINDER_ITEM.get(), "Laser Range Finder");

//		add("smarthome.sensors.generic_sensor", "Generic");
//		add("smarthome.sensors.generic_sensor.info", "Tracks basic information that all devices provide.");

		add(Occupancy.ID, "name", "Occupancy");
		add(Occupancy.ID, "description", "Records presence of living entities in the home zones.");

		add(RedstoneSignal.ID, "name", "Redstone Level");
		add(RedstoneSignal.ID, "description", "Records redstone power levels of blocks.");

		add(EnergyStorage.ID, "name", "Energy Storage");
		add(EnergyStorage.ID, "description", "Records stored forge energy.");

		add(FluidStorage.ID, "name", "Fluid Storage");
		add(FluidStorage.ID, "description", "Records stored fluids.");
	}

}
