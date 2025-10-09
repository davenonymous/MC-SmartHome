package com.davenonymous.smarthome.lib.gui;

import com.davenonymous.smarthome.SmartHome;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.neoforged.neoforge.resource.ResourcePackLoader;
import net.neoforged.neoforgespi.language.IModInfo;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DynamicImageResources {
	private static Map<String, DynTexture> dynTextures = new HashMap<>();

	public record DynTexture(ResourceLocation resource, NativeImage image) implements AutoCloseable {
		public void unload() {
			SmartHome.LOGGER.info("Unloading dynamic texture: {}", resource);
			image.close();
			var tm = Minecraft.getInstance().getTextureManager();
			tm.release(resource);
		}

		@Override
		public void close() throws Exception {
			this.unload();
		}
	}


	public static Optional<DynTexture> uploadImage(String id, NativeImage image) {
		if(image == null) {
			return Optional.empty();
		}

		TextureManager tm = Minecraft.getInstance().getTextureManager();
		if(dynTextures.containsKey(id)) {
			SmartHome.LOGGER.info("Replacing existing dynamic texture with id: {}", id);
			//tm.release(dynTextures.get(id).resource());
//			dynTextures.get(id).image().close();
//			dynTextures.remove(id);
		} else {
			SmartHome.LOGGER.info("Uploading new dynamic texture with id: {}", id);
		}

		ResourceLocation resource = tm.register(
			"modimage_" + id, new DynamicTexture(image) {
				public void upload() {
					this.bind();
					NativeImage td = this.getPixels();
					this.getPixels().upload(0, 0, 0, 0, 0, td.getWidth(), td.getHeight(), false, false, false, false);
				}
			}
		);
		var result = new DynTexture(resource, image);
		dynTextures.put(id, result);

		return Optional.of(result);
	}

	public static Optional<DynTexture> getImage(String path, byte[] bytes) {
		try {
			var bb = MemoryUtil.memAlloc(bytes.length);
			bb.put(bytes);
			bb.rewind();

			var logo = NativeImage.read(bb);
			MemoryUtil.memFree(bb);
			return uploadImage(path, logo);
		} catch (Exception e) {
			SmartHome.LOGGER.warn("Failed to read image from {}: {}", path, e);
		}

		return Optional.empty();
	}


	public static Optional<DynTexture> getImage(Path path, InputStream inputStream) {
		try {
			var logo = NativeImage.read(inputStream);
			return uploadImage(path.toString(), logo);
		} catch (IOException e) {
			SmartHome.LOGGER.warn("Failed to read image from {}: {}", path, e);
		}

		return Optional.empty();
	}

}
