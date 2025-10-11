package com.davenonymous.smarthome.setup.content;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.lib.SpacedBitmapProvider;
import net.minecraft.client.gui.font.providers.GlyphProviderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import java.util.HashMap;
import java.util.Map;

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

	public static final FontSpec NOKIA2 = new ModFonts.FontSpec(SmartHome.resource("nokia2-ascii"), 14, 16);
	public static final FontSpec NOKIA3 = new ModFonts.FontSpec(SmartHome.resource("nokia3-ascii"), 12, 22);
	public static final FontSpec SAMSUNG = new ModFonts.FontSpec(SmartHome.resource("samsung-ascii"), 6, 13);
	public static final FontSpec RECEIPT = new ModFonts.FontSpec(SmartHome.resource("receipt-ascii"), 18, 16);

	public static final Map<ResourceLocation, FontSpec> ALL_FONTS;
	public static final Map<String, ResourceLocation> CHOOSABLE_FONTS;

	public static final String SAFE_FONT_CHARS = "[a-zA-Z0-9äöüÄÖÜß_ .,\\-!?+:/\\@#$%^&*()]*";
	static {
		ALL_FONTS = new HashMap<>();
		ALL_FONTS.put(PIXEL.id(), PIXEL);
		ALL_FONTS.put(WENDY.id(), WENDY);
		ALL_FONTS.put(DOS.id(), DOS);
		ALL_FONTS.put(BASEL.id(), BASEL);
		ALL_FONTS.put(MONKEY_OUTLINE.id(), MONKEY_OUTLINE);
		ALL_FONTS.put(MONKEY_FILLED.id(), MONKEY_FILLED);
		ALL_FONTS.put(NANO.id(), NANO);
		ALL_FONTS.put(NOKIA.id(), NOKIA);
		ALL_FONTS.put(TINY.id(), TINY);
		ALL_FONTS.put(NOKIA2.id(), NOKIA2);
		ALL_FONTS.put(NOKIA3.id(), NOKIA3);
		ALL_FONTS.put(SAMSUNG.id(), SAMSUNG);
		ALL_FONTS.put(RECEIPT.id(), RECEIPT);

		CHOOSABLE_FONTS = new HashMap<>();
		CHOOSABLE_FONTS.put("Basel", BASEL.id());
		CHOOSABLE_FONTS.put("DOS", DOS.id());
		CHOOSABLE_FONTS.put("Nokia", NOKIA2.id());
		CHOOSABLE_FONTS.put("Nokia Small", NOKIA.id());
		CHOOSABLE_FONTS.put("Nokia Large", NOKIA3.id());
		CHOOSABLE_FONTS.put("Receipt", RECEIPT.id());
		CHOOSABLE_FONTS.put("Samsung", SAMSUNG.id());
		CHOOSABLE_FONTS.put("Tiny", TINY.id());
		CHOOSABLE_FONTS.put("Wendy", WENDY.id());
	}

	public static FontSpec getFont(ResourceLocation id) {
		return ALL_FONTS.getOrDefault(id, SAMSUNG);
	}

	public record FontSpec(ResourceLocation id, int yOffset, int lineHeight) {
	}
}
