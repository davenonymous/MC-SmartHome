package com.davenonymous.smarthome.content.sensor.impl.inventory;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.content.sensor.SensorColumn;
import com.davenonymous.smarthome.content.sensor.SensorRange;
import com.davenonymous.smarthome.content.sensor.annotation.SensorDescription;
import com.davenonymous.smarthome.content.sensor.annotation.SensorId;
import com.davenonymous.smarthome.content.sensor.annotation.SensorName;
import com.davenonymous.smarthome.content.sensor.annotation.SmartHomeSensor;
import com.davenonymous.smarthome.content.sensor.sensortypes.BlockSensor;
import com.davenonymous.smarthome.content.sensor.settings.SidedOnOffSettings;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@SmartHomeSensor(modid = "minecraft", data = ItemStorageData.class, settings = SidedOnOffSettings.class)
public class ItemStorage implements BlockSensor<ItemStorageData, SidedOnOffSettings> {
	@SensorId
	public static final ResourceLocation ID = SmartHome.resource("sensor/item_storage");
	public static final BlockCapability<IItemHandler, @Nullable Direction> ITEMS = Capabilities.ItemHandler.BLOCK;

	@SensorName
	@I18DataGen(lang = "en_us", string = "Stored Items")
	@I18DataGen(lang = "de_de", string = "Enthaltene Gegenstände")
	public static final I18String SENSOR_NAME = I18String.data("sensor", "item_storage");

	@SensorDescription
	@I18DataGen(lang = "en_us", string = "Measures the amount of stored items in an inventory")
	@I18DataGen(lang = "de_de", string = "Misst die Anzahl der Gegenstände in einem Inventar")
	public static final I18String SENSOR_DESCRIPTION = I18String.data("sensor", "item_storage_description");

	@Override
	public SidedOnOffSettings getDefaultSettings() {
		return SidedOnOffSettings.DEFAULT;
	}

	@Override
	public boolean isMultiSeries() {
		return true;
	}

	@Override
	public boolean isValid(Level level, BlockPos pos, BlockState state) {
		return level.getCapability(ITEMS, pos, null) != null;
	}

	@Override
	public SensorRange getRange() {
		return new SensorRange.StaticMin(0);
	}

	@Override
	public Optional<SensorColumn> getDefaultColumn() {
		return Optional.of(getColumns().get(1));
	}

	@Override
	public List<ItemStorageData> visitZoneBlock(ServerLevel level, HomeZone zone, ConfiguredDevice device, SidedOnOffSettings settings, BlockPos pos, BlockState state, BlockEntity blockEntity) {
		IItemHandler cap = level.getCapability(ITEMS, pos, settings.side().orElse(null));
		if(cap == null) {
			return null;
		}

		if(cap.getSlots() == 0) {
			return null;
		}

		Map<Item, Long> itemCount = new HashMap<>();
		for(int slot = 0; slot < cap.getSlots(); slot++) {
			ItemStack stackInSlot = cap.getStackInSlot(slot);
			if(stackInSlot.isEmpty()) {
				continue;
			}

			itemCount.put(stackInSlot.getItem(), itemCount.getOrDefault(stackInSlot.getItem(), 0L) + stackInSlot.getCount());
		}

		List<ItemStorageData> results = new ArrayList<>();
		for(Item item : itemCount.keySet()) {
			String itemName = item.getDescriptionId();
			long count = itemCount.get(item);

			results.add(new ItemStorageData(itemName, count));
		}

		return results;
	}

	@Override
	public ItemStorageData dataFromResultSet(ResultSet resultSet) throws SQLException {
		return new ItemStorageData(
			resultSet.getString(getColumns().get(0).name()),
			resultSet.getLong(getColumns().get(1).name())
		);
	}
}
