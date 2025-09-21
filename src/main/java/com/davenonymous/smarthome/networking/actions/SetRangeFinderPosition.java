package com.davenonymous.smarthome.networking.actions;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.items.RangeFinderItem;
import com.davenonymous.smarthome.items.RangerFinderDataComponent;
import com.davenonymous.smarthome.items.ServerDataComponent;
import com.davenonymous.smarthome.items.ServerItem;
import com.davenonymous.smarthome.setup.content.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetRangeFinderPosition(BlockPos pos, boolean isSecond) implements CustomPacketPayload {
	public static final Type<SetRangeFinderPosition> TYPE = new Type<>(SmartHome.resource("set_range_finder_position"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SetRangeFinderPosition> CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC, SetRangeFinderPosition::pos,
		ByteBufCodecs.BOOL, SetRangeFinderPosition::isSecond,
		SetRangeFinderPosition::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handleOnServer(SetRangeFinderPosition payload, IPayloadContext context) {
		var player = context.player();
		var rangeFinderStack = player.getItemInHand(InteractionHand.MAIN_HAND);
		if(!(rangeFinderStack.getItem() instanceof RangeFinderItem)) {
			return;
		}

		RangerFinderDataComponent data;
		if(!rangeFinderStack.has(ModDataComponents.RANGER_FINDER_DATA_COMPONENT)) {
			data = new RangerFinderDataComponent(player);
		} else {
			data = rangeFinderStack.get(ModDataComponents.RANGER_FINDER_DATA_COMPONENT);
		}

		var pos = payload.pos();
		if(payload.isSecond) {
			SmartHome.LOGGER.info("Range finder second position set to {}, {}, {}", pos.getX(), pos.getY(), pos.getZ());
			data = data.withB(pos);
		} else {
			SmartHome.LOGGER.info("Range finder first position set to  {}, {}, {}", pos.getX(), pos.getY(), pos.getZ());
			data = data.withA(pos);
		}

		rangeFinderStack.set(ModDataComponents.RANGER_FINDER_DATA_COMPONENT, data);
		player.setItemInHand(InteractionHand.MAIN_HAND, rangeFinderStack.copy());
	}
}
