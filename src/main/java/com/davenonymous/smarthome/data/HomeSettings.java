package com.davenonymous.smarthome.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;

public class HomeSettings {
	private boolean renameBlocksToDeviceNames;
	private boolean autoIgnoreGenericOnlyDevices;

	public HomeSettings() {
		renameBlocksToDeviceNames = true;
		autoIgnoreGenericOnlyDevices = true;
	}

	public HomeSettings(boolean renameBlocksToDeviceNames, boolean autoIgnoreGenericOnlyDevices) {
		this.autoIgnoreGenericOnlyDevices = autoIgnoreGenericOnlyDevices;
		this.renameBlocksToDeviceNames = renameBlocksToDeviceNames;
	}

	public boolean autoIgnoreGenericOnlyDevices() {
		return autoIgnoreGenericOnlyDevices;
	}

	public boolean renameBlocksToDeviceNames() {
		return renameBlocksToDeviceNames;
	}

	public static final MapCodec<HomeSettings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.BOOL.optionalFieldOf("renameBlocks", true).forGetter(HomeSettings::renameBlocksToDeviceNames),
		Codec.BOOL.optionalFieldOf("autoIgnoreGenericOnlyDevices", true).forGetter(HomeSettings::autoIgnoreGenericOnlyDevices)
	).apply(instance, HomeSettings::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, HomeSettings> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.BOOL, HomeSettings::renameBlocksToDeviceNames,
		ByteBufCodecs.BOOL, HomeSettings::autoIgnoreGenericOnlyDevices,
		HomeSettings::new
	);

	@Override
	public boolean equals(Object o) {
		if(!(o instanceof HomeSettings that)) {
			return false;
		}
		return renameBlocksToDeviceNames == that.renameBlocksToDeviceNames && autoIgnoreGenericOnlyDevices == that.autoIgnoreGenericOnlyDevices;
	}

	@Override
	public int hashCode() {
		return Objects.hash(renameBlocksToDeviceNames, autoIgnoreGenericOnlyDevices);
	}
}
