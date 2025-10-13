package com.davenonymous.smarthome.content.sensor.impl.inventory;

import com.davenonymous.smarthome.content.sensor.ISensorData;
import com.davenonymous.smarthome.content.sensor.annotation.SensorDataColumnLabel;
import com.davenonymous.smarthome.content.sensor.annotation.SensorDataStreamCodec;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.watcher.GroupBy;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public record ItemStorageData(@GroupBy String itemId, long count) implements ISensorData {

	@SensorDataColumnLabel("itemId")
	@I18DataGen(lang = "en_us", string = "Item")
	@I18DataGen(lang = "de_de", string = "Gegenstand")
	public static final I18String ITEM_ID = I18String.data("sensor.item_storage.column", "item");

	@SensorDataColumnLabel("count")
	@I18DataGen(lang = "en_us", string = "Count")
	@I18DataGen(lang = "de_de", string = "Anzahl")
	public static final I18String ITEM_STORED = I18String.data("sensor.item_storage.column", "count");


	@Override
	public Object[] columnValues() {
		return new Object[] {itemId, count};
	}

	@Override
	public String displayString() {
		return itemId() + ": " + count;
	}

	@Override
	public String seriesName() {
		return I18n.get(itemId);
	}

	@Override
	public int bindParameters(PreparedStatement prepped, int nextParamIndex) throws SQLException {
		prepped.setString(nextParamIndex++, itemId());
		prepped.setLong(nextParamIndex++, count());
		return nextParamIndex;
	}

	@SensorDataStreamCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, ItemStorageData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, ItemStorageData::itemId,
		ByteBufCodecs.VAR_LONG, ItemStorageData::count,
		ItemStorageData::new
	);
}
