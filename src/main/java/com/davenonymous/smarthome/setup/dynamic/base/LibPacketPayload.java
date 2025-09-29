package com.davenonymous.smarthome.setup.dynamic.base;

import com.davenonymous.smarthome.setup.dynamic.ModPackets;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface LibPacketPayload extends CustomPacketPayload {
	@Override
	default Type<? extends CustomPacketPayload> type() {
		return ModPackets.TYPE_BY_CLASS.get(this.getClass().getName());
	}
}
