package com.davenonymous.smarthome.lib.gui;

import com.davenonymous.smarthome.lib.gui.configurable.BetterEditBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class NativeWidgetHelper {
	public static BetterEditBox createEditBox() {
		var editBox = new BetterEditBox(Minecraft.getInstance().font, 0, 0, Component.literal("wtf"));
		editBox.setPosition(2, 2);
		editBox.setMaxLength(256);
		editBox.setEditable(true);
		editBox.setBordered(false);
		editBox.setTextColor(0x5c5c5c);
		editBox.setTextShadow(false);
		return editBox;
	}
}
