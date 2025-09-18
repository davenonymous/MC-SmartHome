package com.davenonymous.smarthome.setup.content;

import com.davenonymous.smarthome.lib.SpacedBitmapProvider;
import net.minecraft.client.gui.font.providers.GlyphProviderType;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

public class ModFonts {
	public static final EnumProxy<GlyphProviderType> SPACED_BITMAP = new EnumProxy<>(
		GlyphProviderType.class, "smarthome:spaced_bitmap", SpacedBitmapProvider.Definition.CODEC
	);
}
