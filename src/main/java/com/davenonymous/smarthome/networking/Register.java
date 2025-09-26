package com.davenonymous.smarthome.networking;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.networking.actions.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = SmartHome.MODID)
public class Register {
	@SubscribeEvent
	public static void register(final RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar("1");

		registrar.playToClient(
			HomeInfoPayload.TYPE,
			HomeInfoPayload.CODEC,
			HomeInfoPayload::handleOnClient
		);

		registrar.playToClient(
			HomeInfoListPayload.TYPE,
			HomeInfoListPayload.CODEC,
			HomeInfoListPayload::handleOnClient
		);

		registrar.playToClient(
			OpenHomeScreenPayload.TYPE,
			OpenHomeScreenPayload.CODEC,
			OpenHomeScreenPayload::handleOnClient
		);

		registrar.playToClient(
			DeviceDataPayload.TYPE,
			DeviceDataPayload.CODEC,
			DeviceDataPayload::handleOnClient
		);



		registrar.playToServer(
			SetServerItemHomeNamePayload.TYPE,
			SetServerItemHomeNamePayload.CODEC,
			SetServerItemHomeNamePayload::handleOnServer
		);

		registrar.playToServer(
			SetSelectedHomePayload.TYPE,
			SetSelectedHomePayload.CODEC,
			SetSelectedHomePayload::handleOnServer
		);

		registrar.playToServer(
			SetZoneNamePayload.TYPE,
			SetZoneNamePayload.CODEC,
			SetZoneNamePayload::handleOnServer
		);

		registrar.playToServer(
			SetRangeFinderPosition.TYPE,
			SetRangeFinderPosition.CODEC,
			SetRangeFinderPosition::handleOnServer
		);

		registrar.playToServer(
			AddNewZonePayload.TYPE,
			AddNewZonePayload.CODEC,
			AddNewZonePayload::handleOnServer
		);

		registrar.playToServer(
			MarkZoneAsDeletedPayload.TYPE,
			MarkZoneAsDeletedPayload.CODEC,
			MarkZoneAsDeletedPayload::handleOnServer
		);

		registrar.playToServer(
			AddDevicePayload.TYPE,
			AddDevicePayload.CODEC,
			AddDevicePayload::handleOnServer
		);

		registrar.playToServer(
			SetDeviceNamePayload.TYPE,
			SetDeviceNamePayload.CODEC,
			SetDeviceNamePayload::handleOnServer
		);

		registrar.playToServer(
			RequestDeviceDataPayload.TYPE,
			RequestDeviceDataPayload.CODEC,
			RequestDeviceDataPayload::handleOnServer
		);
	}
}
