package com.davenonymous.smarthome.setup;

import com.davenonymous.smarthome.sensor.SensorDataCodecRegistry;
import com.davenonymous.smarthome.sensor.SensorSettingsCodecRegistry;
import com.davenonymous.smarthome.setup.content.*;
import net.neoforged.bus.api.IEventBus;

public class DeferredRegistries {

	public static void register(IEventBus modbus) {
		ModArgumentTypes.ARGUMENT_TYPES.register(modbus);
		ModBlocks.BLOCKS.register(modbus);
		ModBlocks.BLOCK_ENTITIES.register(modbus);
		ModBlocks.BLOCK_TYPES.register(modbus);
		ModCreativeTabs.CREATIVE_MODE_TABS.register(modbus);
		ModContainers.CONTAINERS.register(modbus);
		ModDataComponents.DATA_COMPONENTS.register(modbus);
		ModItems.ITEMS.register(modbus);
		ModParticles.PARTICLE_TYPES.register(modbus);
		SensorSettingsCodecRegistry.DEFERRED_SENSOR_SETTINGS.register(modbus);
		SensorSettingsCodecRegistry.DEFERRED_SENSOR_SETTINGS_DISPATCHER.register(modbus);
		SensorDataCodecRegistry.DEFERRED_SENSOR_DATA.register(modbus);
		SensorDataCodecRegistry.DEFERRED_SENSOR_DATA_DISPATCHER.register(modbus);
	}
}
