package com.davenonymous.smarthome.data;

import com.davenonymous.smarthome.items.projectbox.ProjectBoxDataComponent;
import com.davenonymous.smarthome.util.MoreCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public record PlacedProjectBox(BlockPos pos, Vec3 worldPos, Direction facing, ProjectBoxDataComponent data) {


	public static final MapCodec<PlacedProjectBox> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockPos.CODEC.fieldOf("pos").forGetter(PlacedProjectBox::pos),
		Vec3.CODEC.fieldOf("worldPos").forGetter(PlacedProjectBox::worldPos),
		Direction.CODEC.fieldOf("facing").forGetter(PlacedProjectBox::facing),
		ProjectBoxDataComponent.CODEC.fieldOf("data").forGetter(PlacedProjectBox::data)
	).apply(instance, PlacedProjectBox::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, PlacedProjectBox> STREAM_CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC, PlacedProjectBox::pos,
		MoreCodecs.VEC3_STREAM_CODEC, PlacedProjectBox::worldPos,
		Direction.STREAM_CODEC, PlacedProjectBox::facing,
		ProjectBoxDataComponent.STREAM_CODEC, PlacedProjectBox::data,
		PlacedProjectBox::new
	);
}
