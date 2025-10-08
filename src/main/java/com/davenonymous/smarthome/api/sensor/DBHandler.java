package com.davenonymous.smarthome.api.sensor;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.api.sensor.sensortypes.HomeSensor;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import org.duckdb.DuckDBConnection;
import org.slf4j.Logger;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DBHandler<D extends ISensorData, T extends HomeSensor<D, ?>> {
	public static final Logger LOGGER = LogUtils.getLogger();

	T sensor;

	public DBHandler(T sensor) {
		this.sensor = sensor;
	}

	private String getTableName() {
		var noSlashPath = sensor.id().getPath().replace('/', '_');
		return sensor.id().getNamespace() + "_" + noSlashPath;
	}

	public Function<DuckDBConnection, D> getLatestValue(UUID deviceId) {
		return connection -> {
			String statement =
				"SELECT " +
					sensor.getColumns().stream().map(SensorColumn::name).collect(Collectors.joining(", ")) +
					" FROM " + getTableName() +
					" WHERE device = ?" +
					" ORDER BY instant DESC" +
					" LIMIT 1";

			try {
				PreparedStatement prepped = connection.prepareStatement(statement);
				prepped.setObject(1, deviceId);

				var resultSet = prepped.executeQuery();
				if(resultSet.next()) {
					return sensor.dataFromResultSet(resultSet);
				}
			} catch (SQLException e) {
				LOGGER.error("Failed to query latest sensor data for device {}", deviceId, e);
			}

			return null;
		};
	}

	public Function<DuckDBConnection, LinkedHashMap<Pair<Instant, Long>, ?>> getValues(UUID deviceId, long startTick, long endTick) {
		return connection -> {
			String numericColumns = sensor.getColumns().stream()
				.filter(sensorColumn -> sensorColumn.type().isNumeric())
				.map(column -> "'" + column.name() + "'")
				.collect(Collectors.joining(", "));

			String otherColumns = sensor.getColumns().stream()
				.filter(sensorColumn -> !sensorColumn.type().isNumeric())
				.map(column -> "'" + column.name() + "'")
				.collect(Collectors.joining(", "));

			String statement = "select * from avgVizData(" +
				"tblName := '"+getTableName()+"', " +
			  	"deviceId := '"+deviceId+"', " +
			  	"numericCols := ["+numericColumns+"]," +
				"otherCols := ["+otherColumns+"]" +
			");";

			// SmartHome.LOGGER.info("Executing sensor data query: {}", statement);
			LinkedHashMap<Pair<Instant, Long>, D> values = new LinkedHashMap<>();
			try {
				PreparedStatement prepped = connection.prepareStatement(statement);
				var resultSet = prepped.executeQuery();
				while(resultSet.next()) {
					Instant instant = resultSet.getTimestamp("instant").toInstant();
					long tick = resultSet.getLong("tick");

					D data = sensor.dataFromResultSet(resultSet);

					values.put(Pair.of(instant, tick), data);
				}
				// SmartHome.LOGGER.info("Queried {} sensor data points for device {}", values.size(), deviceId);
			} catch (SQLException e) {
				SmartHome.LOGGER.error("Failed to query sensor data for device {}", deviceId, e);
			}

			return values;
		};
	}

	// TODO: Add query methods
	public Consumer<DuckDBConnection> insertValues(long ticks, UUID homeId, UUID zoneId, UUID deviceId, ISensorData data) {
		return db -> {
			try {
				String statement =
					"INSERT INTO " + getTableName() +
					" VALUES (CURRENT_TIMESTAMP, ?, ?, ?, ?, " + // instant, tick, home, zone, device
					sensor.getColumns().stream().map(c -> "?").collect(Collectors.joining(", ")) +
					")";

				PreparedStatement prepped = db.prepareStatement(statement);

				int paramIndex = 1;
				prepped.setLong(paramIndex++, ticks);
				prepped.setObject(paramIndex++, homeId);
				prepped.setObject(paramIndex++, zoneId);
				prepped.setObject(paramIndex++, deviceId);

				data.bindParameters(prepped, paramIndex);

				prepped.execute();
				prepped.close();
			} catch (SQLException e) {
				LOGGER.error("Failed to record redstone data for device {}", deviceId, e);
			}
		};
	}

	// TODO: add indexes
	public void createTable(DuckDBConnection db) throws SQLException {
		Statement stmt = db.createStatement();

		StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
		sb.append(sensor.getTableName().getNamespace()).append("_").append(sensor.getTableName().getPath());

		List<SensorColumn> columns = new ArrayList<>();

		columns.add(SensorColumn.timestamp("instant", INSTANT_COLUMN_NAME));
		columns.add(SensorColumn.ubigint("tick", TICK_COLUMN_NAME));
		columns.add(SensorColumn.uuid("home", HOME_COLUMN_NAME));
		columns.add(SensorColumn.uuid("zone", ZONE_COLUMN_NAME));
		columns.add(SensorColumn.uuid("device", DEVICE_COLUMN_NAME));
		columns.addAll(sensor.getColumns());

		String columnSpecs = columns.stream()
			.map(c -> c.name() + " " + c.type().sqlType())
			.collect(Collectors.joining(","));

		sb.append("(").append(columnSpecs).append(")");

		stmt.execute(sb.toString());
		stmt.close();
	}


	@I18DataGen(lang = "en_us", string = "Instant")
	@I18DataGen(lang = "de_de", string = "Zeitpunkt")
	public static final I18String INSTANT_COLUMN_NAME = SmartHome.dataString("columns", "instant");

	@I18DataGen(lang = "en_us", string = "Tick")
	@I18DataGen(lang = "de_de", string = "Tick")
	public static final I18String TICK_COLUMN_NAME = SmartHome.dataString("columns", "tick");

	@I18DataGen(lang = "en_us", string = "Smart Home ID")
	@I18DataGen(lang = "de_de", string = "Smart Home ID")
	public static final I18String HOME_COLUMN_NAME = SmartHome.dataString("columns", "home");

	@I18DataGen(lang = "en_us", string = "Zone ID")
	@I18DataGen(lang = "de_de", string = "Zonen ID")
	public static final I18String ZONE_COLUMN_NAME = SmartHome.dataString("columns", "zone");

	@I18DataGen(lang = "en_us", string = "Device ID")
	@I18DataGen(lang = "de_de", string = "Geräte ID")
	public static final I18String DEVICE_COLUMN_NAME = SmartHome.dataString("columns", "device");
}
