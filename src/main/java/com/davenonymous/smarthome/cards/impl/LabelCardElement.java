package com.davenonymous.smarthome.cards.impl;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.cards.SmartHomeCardElement;
import com.davenonymous.smarthome.cards.HomeCardElement;
import com.davenonymous.smarthome.cards.annotations.HomeCardElementCodec;
import com.davenonymous.smarthome.cards.annotations.HomeCardElementId;
import com.davenonymous.smarthome.cards.annotations.HomeCardElementName;
import com.davenonymous.smarthome.cards.annotations.HomeCardElementStreamCodec;
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
public record LabelCardElement(String text, int color, boolean dropShadow) implements HomeCardElement<WidgetTextBox> {
	@HomeCardElementId
	public static final ResourceLocation ID = SmartHome.resource("card_element/text");

	@HomeCardElementName
	@I18DataGen(lang = "en_us", string = "Text")
	@I18DataGen(lang = "de_de", string = "Text")
	public static final I18String NAME = SmartHome.dataString("card_element.name", "text");

	@HomeCardElementCodec
	public static final MapCodec<LabelCardElement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.STRING.fieldOf("text").forGetter(LabelCardElement::text),
			Codec.INT.fieldOf("color").orElse(0xFFFFFF).forGetter(LabelCardElement::color),
			Codec.BOOL.fieldOf("drop_shadow").orElse(true).forGetter(LabelCardElement::dropShadow)
	).apply(instance, LabelCardElement::new));

	@HomeCardElementStreamCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, LabelCardElement> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, LabelCardElement::text,
		ByteBufCodecs.INT, LabelCardElement::color,
		ByteBufCodecs.BOOL, LabelCardElement::dropShadow,
		LabelCardElement::new
	);

	@Override
	public WidgetTextBox createWidget() {
		WidgetTextBox textBox = new WidgetTextBox(text, color);
		textBox.autoWidth();
		textBox.autoHeight();
		textBox.setDropShadow(dropShadow);
		return textBox;
	}
}
