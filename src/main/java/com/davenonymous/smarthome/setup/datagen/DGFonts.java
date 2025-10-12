package com.davenonymous.smarthome.setup.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.nio.file.Path;

public class DGFonts extends FontProvider {
	public DGFonts(String modid, PackOutput output, ExistingFileHelper exFileHelper) {
		super(modid, output, exFileHelper);
	}

	@Override
	protected void registerFonts() {
		add(Path.of("assets/fonts/pixel.ttf"), "pixel", 5f, 8f, 0);
		//add(Path.of("assets/fonts/AtariSmall.ttf"), "atari", 8f, 8f, 1, 8);
		add(Path.of("assets/fonts/Pixel Nano.ttf"), "nano", 5f, 8f, 0, 8);
		add(Path.of("assets/fonts/Pixelbasel.ttf"), "basel", 16f, 16f, 2);
		add(Path.of("assets/fonts/Wendy-Neue.ttf"), "wendy", 8f, 8f, 1);
		add(Path.of("assets/fonts/TinyUnicode.ttf"), "tiny", 16f, 16f, 2);
		add(Path.of("assets/fonts/Perfect DOS VGA 437.ttf"), "dos", 16f, 16f, 3);
		add(Path.of("assets/fonts/nokiafc22.ttf"), "nokia", 8f, 16f, 1);
		add(Path.of("assets/fonts/monkey.ttf"), "monkey", 12f, 16f, 2);
		add(Path.of("assets/fonts/monkey_outline.ttf"), "monkey_outline", 12f, 16f, 2);


		add(Path.of("assets/fonts/nokia-pixel-large.otf"), "nokia2", 17f, 32f, 3);
		add(Path.of("assets/fonts/nokia-pixel-extra-large.otf"), "nokia3", 20f, 32f, 3);

		add(Path.of("assets/fonts/samsung-gt-e1270-bold.otf"), "samsung", 13f, 16f, 2);

		add(Path.of("assets/fonts/state-of-the-art-receipt.otf"), "receipt", 20f, 32f, 0);
	}
}
