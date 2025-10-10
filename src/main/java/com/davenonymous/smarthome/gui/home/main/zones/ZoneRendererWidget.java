package com.davenonymous.smarthome.gui.home.main.zones;

import com.davenonymous.smarthome.client.BoxRenderer;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.items.RangerFinderDataComponent;
import com.davenonymous.smarthome.lib.gui.ColorHelper;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.particles.util.BoxLineCache;
import com.davenonymous.smarthome.setup.content.ModDataComponents;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ZoneRendererWidget extends WidgetPanel {
	VoxelShape homeShape;
	BoxLineCache boxLines;
	BoxLineCache boundBoxLines;

	Map<UUID, BoxLineCache> zoneDeviceBoxes;
	Map<UUID, BoxLineCache> zoneBoxes;

	public HomeZone selectedZone = null;
	public HomeZone hoveredZone = null;
	public RangerFinderDataComponent selectedRangeFinder;

	Map<RangerFinderDataComponent, BoxLineCache> rangeFinderBoxes;

	public ZoneRendererWidget() {
		super();
	}

	public void refreshZoneList() {
		if(HomeScreen.get() == null) {
			return;
		}

		var selectedHome = HomeScreen.get().selectedHome;
		if(selectedHome == null) {
			return;
		}

		homeShape = selectedHome.normalizedShape();
		if(homeShape.isEmpty()) {
			return;
		}

		boxLines = new BoxLineCache();
		boxLines.addShape(homeShape);

		var bounds = homeShape.bounds();
		var outerBounds = bounds.inflate(4/16d).inflate(4/16d, 0, 4/16d);
		var boundShape = Shapes.create(outerBounds);
		boundBoxLines = new BoxLineCache();
		boundBoxLines.addShape(boundShape);

		zoneDeviceBoxes = new HashMap<>();
		zoneBoxes = new HashMap<>();
		for(var zone : selectedHome.zones()) {
			if(zone.isDeleted()) {
				continue;
			}
			var shape = Shapes.create(zone.bounds().move(-selectedHome.shape().bounds().minX, -selectedHome.shape().bounds().minY, -selectedHome.shape().bounds().minZ).deflate(1/16d));
			var boxLineCache = new BoxLineCache();
			boxLineCache.addShape(shape);

			var deviceLineCache = new BoxLineCache();
			for(var device : zone.devices()) {
				if(device.ignored() || !device.enabled()) {
					continue;
				}
				var deviceBlockState = HomeScreen.get().getMenu().homeWorldInfo.blockStates().get(device.pos());
				var deviceShape = deviceBlockState.getShape(Minecraft.getInstance().level, device.pos());
				if(deviceShape.isEmpty()) {
					continue;
				}
				var movedShape = deviceShape
					.move(device.pos().getX(), device.pos().getY(), device.pos().getZ())
					.move((int) -selectedHome.shape().bounds().minX, (int) -selectedHome.shape().bounds().minY, (int) -selectedHome.shape().bounds().minZ);
				deviceLineCache.addShape(movedShape);
			}

			zoneDeviceBoxes.put(zone.id(), deviceLineCache);
			zoneBoxes.put(zone.id(), boxLineCache);
		}


		var player = Minecraft.getInstance().player;
		var itemStream = player.getInventory().items.stream().limit(9);

		rangeFinderBoxes = new HashMap<>();
		var rangerFinderDataComponents = itemStream
			.filter(stack -> !stack.isEmpty() && stack.has(ModDataComponents.RANGER_FINDER_DATA_COMPONENT))
			.map(stack -> stack.get(ModDataComponents.RANGER_FINDER_DATA_COMPONENT))
			.toList();

		for(var rangeFinderData : rangerFinderDataComponents) {
			if(selectedHome.zones().stream().anyMatch(zone -> zone.bounds().equals(rangeFinderData.toAABB()))) {
				continue;
			}

			BlockPos posA = rangeFinderData.A().offset((int) -selectedHome.shape().bounds().minX, (int) -selectedHome.shape().bounds().minY, (int) -selectedHome.shape().bounds().minZ);
			BlockPos posB = rangeFinderData.B().offset((int) -selectedHome.shape().bounds().minX, (int) -selectedHome.shape().bounds().minY, (int) -selectedHome.shape().bounds().minZ);
			Vector3f vecA = new Vector3f(posA.getX(), posA.getY(), posA.getZ());
			Vector3f vecB = new Vector3f(posB.getX(), posB.getY(), posB.getZ());
			Vector3f max = new Vector3f(vecA).max(vecB).add(1, 1, 1);
			Vector3f min = new Vector3f(vecA).min(vecB);

			var shape = Shapes.create(min.x(), min.y(), min.z(), max.x(), max.y(), max.z());
			var boxLineCache = new BoxLineCache();
			boxLineCache.addShape(shape);

			rangeFinderBoxes.put(rangeFinderData, boxLineCache);
		}
	}


	@Override
	public void draw(GuiGraphics guiGraphics, Window window) {
		super.draw(guiGraphics, window);

		if(boxLines == null || boxLines.lines.isEmpty()) {
			return;
		}

		if(homeShape.isEmpty()) {
			return;
		}

		float scaleFactor = 12f;
		var bounds = homeShape.bounds();
		var center = bounds.getCenter();

		var outerBounds = bounds.inflate(4/16d).inflate(4/16d, 0, 4/16d);
		double longestSide = Math.max(outerBounds.getXsize(), outerBounds.getZsize());
		double longestHeight = outerBounds.getYsize() * Math.sqrt(2) * scaleFactor;
		double expectedMaxRadius = longestSide * Math.sqrt(2) * scaleFactor;



		float ticks = HomeScreen.get().renderTick() + HomeScreen.get().partialTicks();

		var pose = guiGraphics.pose();
		pose.pushPose();
		pose.translate((this.width()-expectedMaxRadius)/2f + 16, (this.height()-longestHeight)/2f, 0);
		pose.scale(scaleFactor, -scaleFactor, scaleFactor);
		pose.translate(0, -bounds.getYsize(), 0);
		pose.translate(0, 0, 100);

		pose.translate(center.x, center.y, center.z);

		pose.mulPose(Axis.XP.rotationDegrees(30f));
		pose.mulPose(Axis.YP.rotationDegrees((ticks*0.5f) % 360));

		pose.translate(-center.x, -center.y, -center.z);

		for(var zoneEntry : zoneBoxes.entrySet()) {
			var zoneId = zoneEntry.getKey();
			var zoneBox = zoneEntry.getValue();
			var deviceBoxLines = zoneDeviceBoxes.get(zoneId);

			if(hoveredZone != null && zoneId.equals(hoveredZone.id())) {
				int selectedColor = ChatFormatting.GREEN.getColor() | 0x80000000;
				BoxRenderer.renderBlockOutline(guiGraphics.pose(), zoneBox.lines, selectedColor, 3);
			}

			if(selectedZone != null && zoneId.equals(selectedZone.id())) {
				BoxRenderer.renderBlockOutline(guiGraphics.pose(), deviceBoxLines.lines, ColorHelper.COLOR_PURPLE, 2);

				int selectedColor = ChatFormatting.DARK_GREEN.getColor() | 0xFF000000;
				BoxRenderer.renderBlockOutline(guiGraphics.pose(), zoneBox.lines, selectedColor, 3);
			} else {
				BoxRenderer.renderBlockOutline(guiGraphics.pose(), deviceBoxLines.lines, ColorHelper.COLOR_PURPLE, 2);
			}
		}


		int color = ChatFormatting.YELLOW.getColor() | 0xDD000000;
		BoxRenderer.renderBlockOutline(guiGraphics.pose(), boxLines.lines, color, 2);

		for(var zoneEntry : rangeFinderBoxes.entrySet()) {
			var data = zoneEntry.getKey();
			var zoneBox = zoneEntry.getValue();

			int selectedColor = ChatFormatting.GOLD.getColor() | 0x20000000;
			if(this.selectedRangeFinder != null && this.selectedRangeFinder.equals(data)) {
				selectedColor = ChatFormatting.GOLD.getColor() | 0xFF000000;
			}

			if(data.toAABB() != null && HomeScreen.get().selectedHome.getZoneCrossing(data.toAABB()) != null) {
				selectedColor = ChatFormatting.RED.getColor() | 0x80000000;
			}

			BoxRenderer.renderBlockOutline(guiGraphics.pose(), zoneBox.lines, selectedColor, 3);
		}

		int boundColor = ChatFormatting.DARK_GRAY.getColor() | 0x40000000;
		BoxRenderer.renderBlockOutline(guiGraphics.pose(), boundBoxLines.lines, boundColor, 2);

		pose.popPose();
	}

}
