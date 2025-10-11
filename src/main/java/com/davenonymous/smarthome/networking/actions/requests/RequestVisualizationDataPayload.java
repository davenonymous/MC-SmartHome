package com.davenonymous.smarthome.networking.actions.requests;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.api.sensor.sensortypes.HomeSensor;
import com.davenonymous.smarthome.api.visualization.IVisualizationSettings;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.TimeRangeEnum;
import com.davenonymous.smarthome.networking.data.VisualizationDataPayload;
import com.davenonymous.smarthome.setup.dynamic.ModSensors;
import com.davenonymous.smarthome.setup.dynamic.annotations.Packet;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketCodec;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketHandler;
import com.davenonymous.smarthome.setup.dynamic.base.LibPacketPayload;
import com.davenonymous.smarthome.watcher.VizQueryDatabaseTask;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.duckdb.DuckDBConnection;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.function.Function;

@Packet
public record RequestVisualizationDataPayload(ConfiguredDevice device, ResourceLocation sensorId, ResourceLocation vizId, IVisualizationSettings settings, TimeRangeEnum timeRange)  implements LibPacketPayload {
	@PacketCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, RequestVisualizationDataPayload> CODEC = StreamCodec.composite(
		ConfiguredDevice.STREAM_CODEC, RequestVisualizationDataPayload::device,
		ResourceLocation.STREAM_CODEC, RequestVisualizationDataPayload::sensorId,
		ResourceLocation.STREAM_CODEC, RequestVisualizationDataPayload::vizId,
		IVisualizationSettings.STREAM_CODEC, RequestVisualizationDataPayload::settings,
		TimeRangeEnum.STREAM_CODEC, RequestVisualizationDataPayload::timeRange,
		RequestVisualizationDataPayload::new
	);

	@PacketHandler(PacketHandler.Receiver.Server)
	public static void handleOnServer(RequestVisualizationDataPayload payload, IPayloadContext context) {
		ServerPlayer player = (ServerPlayer) context.player();
		ServerLevel level = (ServerLevel) player.level();

		var device = payload.device();
		HomeSensor<?, ?> sensor = ModSensors.getById(payload.sensorId);

		var dbHandler = ModSensors.DB_HANDLERS.get(sensor.id());
		Function<DuckDBConnection, LinkedHashMap<Pair<Instant, Long>, ?>> dbFunction = dbHandler.getValues(device.id(), payload.timeRange());

		VizQueryDatabaseTask.execute(dbFunction).thenAccept((vizData) -> {
			if(vizData == null) {
				SmartHome.LOGGER.warn("Failed to get viz data for player='{}' device='{}' sensor='{}' viz='{}'", player.getGameProfile().getName(), device.id(), sensor.id(), payload.vizId());
				return;
			}
			if(vizData.isEmpty()) {
				SmartHome.LOGGER.info("No viz data for player='{}' device='{}' sensor='{}' viz='{}'", player.getGameProfile().getName(), device.id(), sensor.id(), payload.vizId());
				return;
			}

			//noinspection unchecked
			var replyPayload = new VisualizationDataPayload(device, payload.sensorId, payload.vizId, (LinkedHashMap<Pair<Instant, Long>, ISensorData>) vizData);
			context.reply(replyPayload);
		});
	}
}
