package com.davenonymous.smarthome.visualization.line;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.sensor.sensortypes.HomeSensor;
import com.davenonymous.smarthome.api.visualization.IVisualizationSettings;
import com.davenonymous.smarthome.api.visualization.SmartHomeVisualizationSettings;
import com.davenonymous.smarthome.gui.DashboardScreen;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.visualization.VizLegendStyle;
import com.davenonymous.smarthome.visualization.annotations.VisualizationSettingsCodec;
import com.davenonymous.smarthome.visualization.annotations.VisualizationSettingsId;
import com.davenonymous.smarthome.visualization.annotations.VisualizationSettingsStreamCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

@SmartHomeVisualizationSettings
public record LineVizSettings(Map<UUID, Map<String, LineVizColumnSettings>> series, VizLegendStyle legendStyle) implements IVisualizationSettings {
	@VisualizationSettingsId
	public static final ResourceLocation ID = SmartHome.resource("visualization_settings/line");

	@VisualizationSettingsCodec
	public static final MapCodec<LineVizSettings> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
		Codec.unboundedMap(UUIDUtil.STRING_CODEC, Codec.unboundedMap(Codec.STRING, LineVizColumnSettings.CODEC.codec())).optionalFieldOf("series", Map.of()).forGetter(LineVizSettings::series),
		VizLegendStyle.CODEC.optionalFieldOf("legend", VizLegendStyle.BOTTOM).forGetter(LineVizSettings::legendStyle)
	).apply(inst, LineVizSettings::new));

	@VisualizationSettingsStreamCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, LineVizSettings> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.map(HashMap::new, UUIDUtil.STREAM_CODEC, ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, LineVizColumnSettings.STREAM_CODEC)), LineVizSettings::series,
		VizLegendStyle.STREAM_CODEC, LineVizSettings::legendStyle,
		LineVizSettings::new
	);

	@Override
	public List<Widget> createSettingsWidgets(HomeSensor<?, ?> sensor, List<UUID> devices) {
		List<Widget> result = new ArrayList<>();

		// TODO: Make legend style configurable

		for(UUID deviceId : devices) {
			var optDevice = DashboardScreen.get().selectedHome.getDevice(deviceId);
			if(optDevice.isEmpty()) {
				continue;
			}
			var deviceInfo = optDevice.get();
			var zone = deviceInfo.getFirst();
			var device = deviceInfo.getSecond();

			var settingsWidget = new SeriesSettingsWidget(device, sensor, series.get(deviceId));
			result.add(settingsWidget);

		}
		return result;
	}
}
