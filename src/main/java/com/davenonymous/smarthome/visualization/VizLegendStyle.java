package com.davenonymous.smarthome.visualization;

import com.davenonymous.smarthome.lib.HackerNoon;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import org.knowm.xchart.style.Styler;

import java.util.UUID;
import java.util.function.IntFunction;

public enum VizLegendStyle implements StringRepresentable {
	OFF(0, "off", null, null, HackerNoon.Regular.timesCircle),
	RIGHT(1, "right", Styler.LegendLayout.Vertical, Styler.LegendPosition.OutsideE, HackerNoon.Regular.arrowCircleRight),
	BOTTOM(2, "bottom", Styler.LegendLayout.Horizontal, Styler.LegendPosition.OutsideS, HackerNoon.Regular.arrowCircleDown),
	;


	private final int id;
	private final String key;
	private final Styler.LegendLayout layout;
	private final Styler.LegendPosition position;
	private final ResourceLocation icon;

	public int id() {
		return id;
	}

	public String key() {
		return key;
	}

	public Styler.LegendLayout layout() {
		return layout;
	}

	public Styler.LegendPosition position() {
		return position;
	}

	public ResourceLocation icon() {
		return icon;
	}

	public static final IntFunction<VizLegendStyle> BY_ID = ByIdMap.continuous(
		VizLegendStyle::id,
		VizLegendStyle.values(),
		ByIdMap.OutOfBoundsStrategy.ZERO
	);

	@SuppressWarnings("deprecation")
	public static final EnumCodec<VizLegendStyle> CODEC = StringRepresentable.fromEnum(VizLegendStyle::values);

	public static final StreamCodec<ByteBuf, VizLegendStyle> STREAM_CODEC =
		ByteBufCodecs.idMapper(VizLegendStyle.BY_ID, VizLegendStyle::id);


	VizLegendStyle(int id, String key, Styler.LegendLayout layout, Styler.LegendPosition position, ResourceLocation icon) {
		this.id = id;
		this.key = key;
		this.layout = layout;
		this.position = position;
		this.icon = icon;
	}

	public static VizLegendStyle byId(int id) {
		return BY_ID.apply(id);
	}

	public VizLegendStyle next() {
		return byId((id + 1) % values().length);
	}

	@Override
	public String getSerializedName() {
		return this.key;
	}
}
