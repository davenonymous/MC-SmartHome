package com.davenonymous.smarthome.cards.impl;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.cards.HomeCardElement;
import com.davenonymous.smarthome.cards.SmartHomeCardElement;
import com.davenonymous.smarthome.cards.annotations.*;
import com.davenonymous.smarthome.lib.HackerNoon;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetSprite;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

@SmartHomeCardElement
public record SpriteCardElement(ResourceLocation sprite, int color, float scale) implements HomeCardElement<WidgetSprite> {
	@HomeCardElementId
	public static final ResourceLocation ID = SmartHome.resource("card_element/sprite");

	@HomeCardElementName
	@I18DataGen(lang = "en_us", string = "Icon")
	@I18DataGen(lang = "de_de", string = "Symbol")
	public static final I18String NAME = SmartHome.dataString("card_element.name", "sprite");

	@HomeCardElementIcon
	public static final ResourceLocation ICON = HackerNoon.Regular.image;

	@HomeCardElementDefault
	public static SpriteCardElement createDefault() {
		return new SpriteCardElement(HackerNoon.Regular.star, 0xFFFFFFFF, 0.5f);
	}

	@HomeCardElementCodec
	public static final MapCodec<SpriteCardElement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("text").forGetter(SpriteCardElement::sprite),
			Codec.INT.optionalFieldOf("color", 0xFFFFFF).forGetter(SpriteCardElement::color),
			Codec.FLOAT.optionalFieldOf("scale",1.0f).forGetter(SpriteCardElement::scale)
	).apply(instance, SpriteCardElement::new));

	@HomeCardElementStreamCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, SpriteCardElement> STREAM_CODEC = StreamCodec.composite(
		ResourceLocation.STREAM_CODEC, SpriteCardElement::sprite,
		ByteBufCodecs.INT, SpriteCardElement::color,
		ByteBufCodecs.FLOAT, SpriteCardElement::scale,
		SpriteCardElement::new
	);

	@Override
	public WidgetSprite createWidget() {
		WidgetSprite spriteBox = new WidgetSprite(sprite, color);
		if(scale != 1.0f) spriteBox.setScale(scale);
		return spriteBox;
	}
}
