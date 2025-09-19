package com.davenonymous.smarthome.lib.gui;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.neoforged.neoforge.common.util.Size2i;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SpriteSizeCache {
	private static Map<ResourceLocation, Size2i> spriteSizeCache = new HashMap<>();

	public static Size2i getSpriteSize(ResourceLocation id) {
		if(spriteSizeCache.containsKey(id)) {
			return spriteSizeCache.get(id);
		}

		var spriteLocation = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "textures/gui/sprites/" + id.getPath() + ".png");
		Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(spriteLocation);
		if(resource.isEmpty()) {
			var size = new Size2i(16, 16);
			spriteSizeCache.put(id, size);
			return size;
		}

		NativeImage nativeimage;
		try (InputStream inputstream = resource.get().open()) {
			nativeimage = NativeImage.read(inputstream);
		} catch (Exception e) {
			var size = new Size2i(16, 16);
			spriteSizeCache.put(id, size);
			return size;
		}

		int width = nativeimage.getWidth();
		int height = nativeimage.getHeight();
		nativeimage.close();

		var size = new Size2i(width, height);
		spriteSizeCache.put(id, size);
		return size;
	}
}
