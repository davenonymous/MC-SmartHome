package com.davenonymous.smarthome.setup.content;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.lib.SpacedBitmapProvider;
import net.minecraft.client.gui.font.providers.GlyphProviderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

public class ModFonts {
	public static final EnumProxy<GlyphProviderType> SPACED_BITMAP = new EnumProxy<>(
		GlyphProviderType.class, "smarthome:spaced_bitmap", SpacedBitmapProvider.Definition.CODEC
	);

	public static final FontSpec PIXEL = new FontSpec(SmartHome.resource("pixel-ascii"), 8, 5);
	public static final FontSpec WENDY = new ModFonts.FontSpec(SmartHome.resource("wendy-ascii"), 7, 5);
	public static final FontSpec DOS = new ModFonts.FontSpec(SmartHome.resource("dos-ascii"), 6, 13);
	public static final FontSpec BASEL = new ModFonts.FontSpec(SmartHome.resource("basel-ascii"), 8, 12);
	public static final FontSpec MONKEY_OUTLINE = new ModFonts.FontSpec(SmartHome.resource("monkey_outline-ascii"), 9, 10);
	public static final FontSpec MONKEY_FILLED = new ModFonts.FontSpec(SmartHome.resource("monkey-ascii"), 9, 10);
	public static final FontSpec NANO = new ModFonts.FontSpec(SmartHome.resource("nano-ascii"), 9, 5);
	public static final FontSpec NOKIA = new ModFonts.FontSpec(SmartHome.resource("nokia-ascii"), 11, 9);
	public static final FontSpec TINY = new ModFonts.FontSpec(SmartHome.resource("tiny-ascii"), 12, 7);

	public record FontSpec(ResourceLocation id, int yOffset, int lineHeight) {
	}
}
