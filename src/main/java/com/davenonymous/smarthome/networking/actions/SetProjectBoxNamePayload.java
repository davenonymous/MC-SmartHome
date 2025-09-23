package com.davenonymous.smarthome.networking.actions;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.items.ServerDataComponent;
import com.davenonymous.smarthome.items.ServerItem;
import com.davenonymous.smarthome.items.projectbox.ProjectBoxDataComponent;
import com.davenonymous.smarthome.items.projectbox.ProjectBoxItem;
import com.davenonymous.smarthome.setup.content.ModDataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public record SetProjectBoxNamePayload(String name) implements CustomPacketPayload {
	public static final Type<SetProjectBoxNamePayload> TYPE = new Type<>(SmartHome.resource("set_project_box_name"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SetProjectBoxNamePayload> CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, SetProjectBoxNamePayload::name,
		SetProjectBoxNamePayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void register(PayloadRegistrar registrar) {
		registrar.playToServer(
			TYPE,
			CODEC,
			SetProjectBoxNamePayload::handleOnServer
		);
	}

	public static void handleOnServer(SetProjectBoxNamePayload payload, IPayloadContext context) {
		var player = context.player();
		var projectBoxItem = player.getItemInHand(InteractionHand.MAIN_HAND);
		if(!(projectBoxItem.getItem() instanceof ProjectBoxItem)) {
			return;
		}

		if(!projectBoxItem.has(ModDataComponents.PROJECT_BOX_DATA_COMPONENT)) {
			projectBoxItem.set(ModDataComponents.PROJECT_BOX_DATA_COMPONENT, new ProjectBoxDataComponent());
		} else {
			ProjectBoxDataComponent data = projectBoxItem.get(ModDataComponents.PROJECT_BOX_DATA_COMPONENT);
			projectBoxItem.set(ModDataComponents.PROJECT_BOX_DATA_COMPONENT, data.withName(payload.name()));
		}

		player.setItemInHand(InteractionHand.MAIN_HAND, projectBoxItem.copy());
	}
}
