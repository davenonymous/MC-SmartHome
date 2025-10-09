package com.davenonymous.smarthome.config;

import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {
	public static ModConfigSpec.BooleanValue DISABLE_IN_WORLD_DISPLAYS;
	public static ModConfigSpec.IntValue IN_WORLD_DISPLAY_REFRESH_RATE;

	public static boolean disableInWorldDisplays = true;
	public static int displayRefreshRate = 200;

	@I18DataGen(lang = "en_us", string = "Disable in-world displays")
	@I18DataGen(lang = "de_de", string = "Anzeigen außerhalb GUIs deaktivieren")
	public static final I18String disableInWorldDisplaysName = I18String.config("client", "disable_in_world_displays");

	@I18DataGen(lang = "en_us", string = "Disable in-world displays if you have performance issues or problems with shaders.")
	@I18DataGen(lang = "de_de", string = "Deaktiviere In-Welt-Anzeigen, wenn es Leistungs- oder Shaderprobleme gibt.")
	public static final I18String disableInWorldDisplaysDesc = I18String.config("client", "disable_in_world_displays.tooltip");


	@I18DataGen(lang = "en_us", string = "In-world display refresh rate")
	@I18DataGen(lang = "de_de", string = "Aktualisierungsrate für In-Welt-Anzeigen")
	public static final I18String displayRefreshRateName = I18String.config("client", "display_refresh_rate");

	@I18DataGen(lang = "en_us", string = "How often in-world displays refresh in ticks. Lower values mean more frequent updates but can impact performance.")
	@I18DataGen(lang = "de_de", string = "Wie oft In-Welt-Anzeigen in Ticks aktualisiert werden. Niedrigere Werte bedeuten häufigere Updates, können aber die Leistung beeinträchtigen.")
	public static final I18String displayRefreshRateDesc = I18String.config("client", "display_refresh_rate.tooltip");

	public ClientConfig(ModConfigSpec.Builder builder) {
		// builder.push("client");

		DISABLE_IN_WORLD_DISPLAYS = builder
			.comment("Set to true to disable in-world displays")
			.translation(disableInWorldDisplaysName.key())
			.define("disableInWorldDisplays", false);

		IN_WORLD_DISPLAY_REFRESH_RATE = builder
			.comment("How often in-world displays refresh in ticks. Lower values mean more frequent updates but can impact performance.")
			.translation(displayRefreshRateName.key())
			.defineInRange("displayRefreshRate", 100, 5, Integer.MAX_VALUE);

		// builder.pop();
	}

	public void load() {
		disableInWorldDisplays = DISABLE_IN_WORLD_DISPLAYS.get();
		displayRefreshRate = IN_WORLD_DISPLAY_REFRESH_RATE.get();
	}
}
