package com.davenonymous.smarthome.lib.gui.configurable;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetWithChoiceValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;

import java.util.Locale;

public class EnumInputWidget<T extends Enum<?>> extends WidgetWithChoiceValue<T> {
	private final Class<T> enumClass;
	private int textColor = 0xFF5c5c5c;

	public EnumInputWidget(Class<T> enumClass, T value) {
		super();
		this.enumClass = enumClass;
		this.setHeight(10);
		this.addChoice(enumClass.getEnumConstants());

		this.addClickListener();
	}

	@Override
	public void draw(GuiGraphics pGuiGraphics, Screen screen) {
		String translationKey = SmartHome.MODID + ".enum." + enumClass.getSimpleName().toLowerCase(Locale.ROOT) + "." + getValue().name().toLowerCase(Locale.ROOT);
		if(I18n.exists(translationKey)) {
			pGuiGraphics.drawString(Minecraft.getInstance().font, I18n.get(translationKey), 0, 0, textColor, false);
		} else {
			pGuiGraphics.drawString(Minecraft.getInstance().font, getValue().name(), 0, 0, textColor, false);
		}
	}
}
