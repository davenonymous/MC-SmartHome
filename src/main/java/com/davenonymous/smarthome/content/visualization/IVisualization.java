package com.davenonymous.smarthome.content.visualization;

import com.davenonymous.smarthome.content.sensor.ISensorData;
import com.davenonymous.smarthome.content.sensor.sensortypes.HomeSensor;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.setup.dynamic.ModVisualizations;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IVisualization<S extends IVisualizationSettings> {
	default ResourceLocation getType() {
		return ModVisualizations.ID_BY_CLASS.get(this.getClass());
	}

	default I18String getDisplayName() {
		return ModVisualizations.NAME_BY_ID.get(this.getType());
	}

	default I18String getDescription() {
		return ModVisualizations.DESC_BY_ID.get(this.getType());
	}

	default boolean requiresHistory() {
		return true;
	}

	S getDefaultSettings();

	Widget getWidget(int texId, Map<UUID, LinkedHashMap<Pair<Instant, Long>, ISensorData>> data, HomeSensor<?, ?> sensor, S vizSettings, Vec2 size);

	default S loadSettings(List<Widget> settingsWidgets) {
		return getDefaultSettings();
	}
}
