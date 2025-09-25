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
	private boolean autoEnableNewDevices;

	public HomeSettings() {
		renameBlocksToDeviceNames = true;
		autoIgnoreGenericOnlyDevices = true;
		autoEnableNewDevices = true;
	}

	public HomeSettings(boolean renameBlocksToDeviceNames, boolean autoIgnoreGenericOnlyDevices, boolean autoEnableNewDevices) {
		this.autoIgnoreGenericOnlyDevices = autoIgnoreGenericOnlyDevices;
		this.renameBlocksToDeviceNames = renameBlocksToDeviceNames;
		this.autoEnableNewDevices = autoEnableNewDevices;
	}

	public boolean autoIgnoreGenericOnlyDevices() {
		return autoIgnoreGenericOnlyDevices;
	}

	public boolean renameBlocksToDeviceNames() {
		return renameBlocksToDeviceNames;
	}

	public boolean autoEnableNewDevices() {
		return autoEnableNewDevices;
	}

	public static final MapCodec<HomeSettings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.BOOL.optionalFieldOf("renameBlocks", true).forGetter(HomeSettings::renameBlocksToDeviceNames),
		Codec.BOOL.optionalFieldOf("autoIgnoreGenericOnlyDevices", true).forGetter(HomeSettings::autoIgnoreGenericOnlyDevices),
		Codec.BOOL.optionalFieldOf("autoEnableNewDevices", true).forGetter(HomeSettings::autoEnableNewDevices)
	).apply(instance, HomeSettings::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, HomeSettings> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.BOOL, HomeSettings::renameBlocksToDeviceNames,
		ByteBufCodecs.BOOL, HomeSettings::autoIgnoreGenericOnlyDevices,
		ByteBufCodecs.BOOL, HomeSettings::autoEnableNewDevices,
		HomeSettings::new
	);

	@Override
	public boolean equals(Object o) {
		if(!(o instanceof HomeSettings that)) {
			return false;
		}
		return renameBlocksToDeviceNames == that.renameBlocksToDeviceNames && autoIgnoreGenericOnlyDevices == that.autoIgnoreGenericOnlyDevices && autoEnableNewDevices == that.autoEnableNewDevices;
	}

	@Override
	public int hashCode() {
		return Objects.hash(renameBlocksToDeviceNames, autoIgnoreGenericOnlyDevices, autoEnableNewDevices);
	}
}
