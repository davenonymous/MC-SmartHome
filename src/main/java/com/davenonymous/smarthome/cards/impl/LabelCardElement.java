package com.davenonymous.smarthome.cards.impl;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.cards.SmartHomeCardElement;
import com.davenonymous.smarthome.cards.HomeCardElement;
import com.davenonymous.smarthome.cards.annotations.*;
import com.davenonymous.smarthome.gui.home.main.devices.NewDeviceEntryWidget;
import com.davenonymous.smarthome.lib.HackerNoon;
import com.davenonymous.smarthome.lib.gui.configurable.StringInputWidget;
import com.davenonymous.smarthome.lib.gui.tooltip.WrappedStringTooltipComponent;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.setup.content.ModFonts;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.UUID;

@SmartHomeCardElement
public record LabelCardElement(UUID id, String text, int color, boolean dropShadow) implements HomeCardElement<WidgetTextBox> {
	@HomeCardElementId
	public static final ResourceLocation ID = SmartHome.resource("card_element/text");

	@HomeCardElementName
	@I18DataGen(lang = "en_us", string = "Label")
	@I18DataGen(lang = "de_de", string = "Text")
	public static final I18String NAME = SmartHome.dataString("card_element.name", "text");

	@I18DataGen(lang = "en_us", string = "New label")
	@I18DataGen(lang = "de_de", string = "Neuer Text")
	public static final I18String DEFAULT_LABEL = SmartHome.dataString("card_element.name", "default_label");

	@HomeCardElementDefault
	public static LabelCardElement createDefault() {
		return new LabelCardElement(UUID.randomUUID(), DEFAULT_LABEL.get(), 0xFFFFFFFF, false);
	}

	@HomeCardElementIcon
	public static final ResourceLocation ICON = HackerNoon.Regular.italics;

	@HomeCardElementCodec
	public static final MapCodec<LabelCardElement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		UUIDUtil.STRING_CODEC.fieldOf("id").forGetter(LabelCardElement::id),
		Codec.STRING.fieldOf("text").forGetter(LabelCardElement::text),
		Codec.INT.fieldOf("color").orElse(0xFFFFFF).forGetter(LabelCardElement::color),
		Codec.BOOL.fieldOf("drop_shadow").orElse(true).forGetter(LabelCardElement::dropShadow)
	).apply(instance, LabelCardElement::new));

	@HomeCardElementStreamCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, LabelCardElement> STREAM_CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, LabelCardElement::id,
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

	@Override
	public List<Widget> createSettingWidgets() {
		var labelInput = new StringInputWidget(text, "[a-zA-Z0-9_ -!?+:/\\@#$%^&*()]*");
		labelInput.setDrawBackground(false);
		labelInput.nativeWidget().setTextColor(ChatFormatting.WHITE.getColor());
		return List.of(labelInput);
	}

	@Override
	public LabelCardElement loadSettings(List<Widget> settingsWidgets) {
		var labelInput = (StringInputWidget)settingsWidgets.get(0);
		return new LabelCardElement(id, labelInput.getValue(), color, dropShadow);
	}
}
