package com.davenonymous.smarthome.config;

import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerConfig {
	public static ModConfigSpec.IntValue DISPLAY_DATA_REFRESH_RATE;
	public static ModConfigSpec.IntValue TASK_QUEUE_SIZE;
	public static ModConfigSpec.IntValue MIN_SENSOR_TICK_RATE;
	public static ModConfigSpec.ConfigValue<String> DATABASE_PATH;
	public static ModConfigSpec.BooleanValue DROP_SENSORS_IN_FAVOR_OF_TPS;
	public static ModConfigSpec.BooleanValue INSERT_SPARSE;
	public static ModConfigSpec.IntValue SPARSE_TICK_RATE;

	public static int displayDataUpdateRate = 100;
	public static int taskQueueSize = 32;
	public static int minSensorTickRate = 1;
	public static String databasePath = "smarthome.duckdb";
	public static boolean dropSensorsInFavorOfTPS = true;
	public static boolean insertSparse = true;
	public static int sparseTickRate = 200;

	public ServerConfig(ModConfigSpec.Builder builder) {
		DISPLAY_DATA_REFRESH_RATE = builder
			.comment("How often in-world displays get sent new data (in ticks). Lower values mean more frequent updates but can impact performance.")
			.translation(displayDataUpdateRateName.key())
			.defineInRange("displayRefreshRate", 100, 5, Integer.MAX_VALUE);

		TASK_QUEUE_SIZE = builder
			.comment("Size of the database task queue. Increase this if you have many sensors and tasks are getting backed up.")
			.translation(taskQueueSizeName.key())
			.worldRestart()
			.defineInRange("taskQueueSize", 32, 1, 1024);

		MIN_SENSOR_TICK_RATE = builder
			.comment("Minimum allowed sensor tick rate in ticks. This is to prevent sensors from being spammed too often.")
			.translation(minSensorTickRateName.key())
			.defineInRange("minSensorTickRate", 1, 1, 20 * 60 * 60);

		DATABASE_PATH = builder
			.comment("Path to the database file, relative to the world folder.")
			.translation(databasePathName.key())
			.worldRestart()
			.define("databasePath", "smarthome.duckdb");

		DROP_SENSORS_IN_FAVOR_OF_TPS = builder
			.comment("If true, sensors data will not be updated if the server TPS is struggling.")
			.translation(dropSensorsInFavorOfTPSName.key())
			.define("dropSensorsInFavorOfTPS", true);

		INSERT_SPARSE = builder
			.comment("If true, sensor data will be inserted in a sparse manner, skipping sensor readings that have no changes.")
			.translation(insertSparseName.key())
			.define("insertSparse", true);

		SPARSE_TICK_RATE = builder
			.comment("When inserting sparse data, this defines the minimum tick rate between two identical sensor readings to still insert the data.")
			.translation(sparseTickRateName.key())
			.defineInRange("sparseTickRate", 200, 1, 20 * 60 * 60);

	}


	public void load() {
		displayDataUpdateRate = DISPLAY_DATA_REFRESH_RATE.get();
		taskQueueSize = TASK_QUEUE_SIZE.get();
		minSensorTickRate = MIN_SENSOR_TICK_RATE.get();
		databasePath = DATABASE_PATH.get();
		dropSensorsInFavorOfTPS = DROP_SENSORS_IN_FAVOR_OF_TPS.get();
		insertSparse = INSERT_SPARSE.get();
		sparseTickRate = SPARSE_TICK_RATE.get();
	}

	@I18DataGen(lang = "en_us", string = "In-world display data update rate")
	@I18DataGen(lang = "de_de", string = "Aktualisierungsrate für Daten von In-Welt-Anzeigen")
	public static final I18String displayDataUpdateRateName = I18String.config("server", "display_data_update_rate");

	@I18DataGen(lang = "en_us", string = "How often in-world displays get sent new data ticks. Lower values mean more frequent updates for clients but can impact performance.")
	@I18DataGen(lang = "de_de", string = "Wie oft In-Welt-Anzeigen in Ticks mit neuen Daten versehen werden. Niedrigere Werte bedeuten häufigere Updates für Clients, können aber die Leistung beeinträchtigen.")
	public static final I18String displayDataUpdateRateDesc = I18String.config("server", "display_data_update_rate.tooltip");

	@I18DataGen(lang = "en_us", string = "Database task queue size")
	@I18DataGen(lang = "de_de", string = "Datenbank-Aufgaben-Warteschlangengröße")
	public static final I18String taskQueueSizeName = I18String.config("server", "task_queue_size");

	@I18DataGen(lang = "en_us", string = "Size of the database task queue. Increase this if you have many sensors and tasks are getting backed up.")
	@I18DataGen(lang = "de_de", string = "Größe der Datenbank-Aufgaben-Warteschlange. Erhöhe diesen Wert, wenn es viele Sensoren gibt und Aufgaben sich aufstauen.")
	public static final I18String taskQueueSizeDesc = I18String.config("server", "task_queue_size.tooltip");

	@I18DataGen(lang = "en_us", string = "Minimum sensor tick rate")
	@I18DataGen(lang = "de_de", string = "Minimale Sensor-Tickrate")
	public static final I18String minSensorTickRateName = I18String.config("server", "min_sensor_tick_rate");

	@I18DataGen(lang = "en_us", string = "Minimum allowed sensor tick rate in ticks. This is to prevent sensors from being spammed too often.")
	@I18DataGen(lang = "de_de", string = "Minimale erlaubte Sensor-Tickrate in Ticks. Dies soll verhindern, dass Sensoren zu oft abgefragt werden.")
	public static final I18String minSensorTickRateDesc = I18String.config("server", "min_sensor_tick_rate.tooltip");

	@I18DataGen(lang = "en_us", string = "Database path")
	@I18DataGen(lang = "de_de", string = "Datenbank-Pfad")
	public static final I18String databasePathName = I18String.config("server", "database_path");

	@I18DataGen(lang = "en_us", string = "Path to the database file, relative to the world folder.")
	@I18DataGen(lang = "de_de", string = "Pfad zur Datenbank-Datei, relativ zum Welt-Ordner.")
	public static final I18String databasePathDesc = I18String.config("server", "database_path.tooltip");


	@I18DataGen(lang = "en_us", string = "Drop sensor updates in favor of TPS")
	@I18DataGen(lang = "de_de", string = "Sensor-Updates zugunsten der TPS verwerfen")
	public static final I18String dropSensorsInFavorOfTPSName = I18String.config("server", "drop_sensors_in_favor_of_tps");

	@I18DataGen(lang = "en_us", string = "If true, sensors data will not be updated if the server TPS is struggling. This means that some sensor data might be missing in favor of server performance.")
	@I18DataGen(lang = "de_de", string = "Wenn aktiviert, werden Sensordaten nicht aktualisiert, falls die Server-Tickrate in Probleme kommen würde. Das bedeutet, dass zugunsten der Serverleistung einige Sensordaten fehlen könnten.")
	public static final I18String dropSensorsInFavorOfTPSDesc = I18String.config("server", "drop_sensors_in_favor_of_tps.tooltip");

	@I18DataGen(lang = "en_us", string = "Insert sparse data")
	@I18DataGen(lang = "de_de", string = "Daten spärlich einfügen")
	public static final I18String insertSparseName = I18String.config("server", "insert_sparse");

	@I18DataGen(lang = "en_us", string = "If true, sensor data will be inserted in a sparse manner, skipping sensor readings that have no changes.")
	@I18DataGen(lang = "de_de", string = "Wenn aktiviert, werden Sensordaten spärlich eingefügt, wobei Sensormessungen ohne Änderungen übersprungen werden.")
	public static final I18String insertSparseDesc = I18String.config("server", "insert_sparse.tooltip");

	@I18DataGen(lang = "en_us", string = "Sparse tick rate")
	@I18DataGen(lang = "de_de", string = "Spärliche Tickrate")
	public static final I18String sparseTickRateName = I18String.config("server", "sparse_tick_rate");

	@I18DataGen(lang = "en_us", string = "When inserting sparse data, this defines the minimum tick rate between two identical sensor readings to still insert the data.")
	@I18DataGen(lang = "de_de", string = "Beim Einfügen spärlicher Daten definiert dies die minimale Tickrate zwischen zwei identischen Sensormessungen, um die Daten dennoch einzufügen.")
	public static final I18String sparseTickRateDesc = I18String.config("server", "sparse_tick_rate.tooltip");
}
