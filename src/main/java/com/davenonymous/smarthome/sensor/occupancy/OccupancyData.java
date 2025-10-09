package com.davenonymous.smarthome.sensor.occupancy;

import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.lib.BiggerStreamCodec;
import com.davenonymous.smarthome.api.sensor.annotations.SensorDataStreamCodec;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.sensor.annotation.SensorDataColumnLabel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public record OccupancyData(int id, String name, String type, String category, double x, double y, double z) implements ISensorData {

	@SensorDataColumnLabel("id")
	@I18DataGen(lang = "en_us", string = "ID")
	@I18DataGen(lang = "de_de", string = "ID")
	public static final I18String ID = I18String.data("sensor.occupancy.column", "id");

	@SensorDataColumnLabel("name")
	@I18DataGen(lang = "en_us", string = "Name")
	@I18DataGen(lang = "de_de", string = "Name")
	public static final I18String NAME = I18String.data("sensor.occupancy.column", "name");

	@SensorDataColumnLabel("type")
	@I18DataGen(lang = "en_us", string = "Type")
	@I18DataGen(lang = "de_de", string = "Typ")
	public static final I18String TYPE = I18String.data("sensor.occupancy.column", "type");

	@SensorDataColumnLabel("category")
	@I18DataGen(lang = "en_us", string = "Category")
	@I18DataGen(lang = "de_de", string = "Kategorie")
	public static final I18String CATEGORY = I18String.data("sensor.occupancy.column", "category");

	@SensorDataColumnLabel("x")
	@I18DataGen(lang = "en_us", string = "X")
	@I18DataGen(lang = "de_de", string = "X")
	public static final I18String X = I18String.data("sensor.occupancy.column", "x");

	@SensorDataColumnLabel("y")
	@I18DataGen(lang = "en_us", string = "Y")
	@I18DataGen(lang = "de_de", string = "Y")
	public static final I18String Y = I18String.data("sensor.occupancy.column", "y");

	@SensorDataColumnLabel("z")
	@I18DataGen(lang = "en_us", string = "Z")
	@I18DataGen(lang = "de_de", string = "Z")
	public static final I18String Z = I18String.data("sensor.occupancy.column", "z");

	@Override
	public Object[] columnValues() {
		return new Object[] {id, name, type, category, x, y, z};
	}

	@SensorDataStreamCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, OccupancyData> STREAM_CODEC = BiggerStreamCodec.composite(
		ByteBufCodecs.INT, OccupancyData::id,
		ByteBufCodecs.STRING_UTF8, OccupancyData::name,
		ByteBufCodecs.STRING_UTF8, OccupancyData::type,
		ByteBufCodecs.STRING_UTF8, OccupancyData::category,
		ByteBufCodecs.DOUBLE, OccupancyData::x,
		ByteBufCodecs.DOUBLE, OccupancyData::y,
		ByteBufCodecs.DOUBLE, OccupancyData::z,
		OccupancyData::new
	);

	@Override
	public String displayString() {
		return name;
	}

	@Override
	public int bindParameters(PreparedStatement prepped, int nextParamIndex) throws SQLException {
		prepped.setInt(nextParamIndex++, id());
		prepped.setString(nextParamIndex++, name());
		prepped.setString(nextParamIndex++, type());
		prepped.setString(nextParamIndex++, category());
		prepped.setDouble(nextParamIndex++, x());
		prepped.setDouble(nextParamIndex++, y());
		prepped.setDouble(nextParamIndex++, z());
		return nextParamIndex;
	}
}
