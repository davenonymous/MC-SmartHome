package com.davenonymous.smarthome.gui.home.main.zones;

import com.davenonymous.smarthome.client.BoxRenderer;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.items.RangerFinderDataComponent;
import com.davenonymous.smarthome.lib.gui.event.MouseScrollEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.particles.util.BoxLineCache;
import com.davenonymous.smarthome.setup.content.ModDataComponents;
import com.mojang.blaze3d.platform.Window;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ZoneRendererWidget extends WidgetPanel {
	VoxelShape homeShape;
	BoxLineCache boxLines;
	BoxLineCache boundBoxLines;

	Map<UUID, BoxLineCache> zoneBoxes;

	public HomeZone hoveredZone = null;
	public RangerFinderDataComponent selectedRangeFinder;

	Map<RangerFinderDataComponent, BoxLineCache> rangeFinderBoxes;

	float rotX = -30;
	float rotY = -35;

	public ZoneRendererWidget() {
		// TODO: this wants to be mouse drag instead of scrolling
		this.addListener(MouseScrollEvent.class, (event, widget) -> {
			if(!this.isHovered()) {
				return WidgetEventResult.CONTINUE_PROCESSING;
			}

			if(getGUI().isShiftDown()) {
				rotX += (float) (event.rawScrollValue * 4d);
				rotX = Math.max(-90, Math.min(90, rotX));
			} else {
				rotY += (float) (event.rawScrollValue * 4d);
				rotY = rotY % 360;
			}
			return WidgetEventResult.HANDLED;
		});
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

		zoneBoxes = new HashMap<>();
		for(var zone : selectedHome.zones()) {
			if(zone.isDeleted()) {
				continue;
			}
			var shape = Shapes.create(zone.bounds().move(-selectedHome.shape().bounds().minX, -selectedHome.shape().bounds().minY, -selectedHome.shape().bounds().minZ).deflate(1/16d));
			var boxLineCache = new BoxLineCache();
			boxLineCache.addShape(shape);
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

		float scaleFactor = 8f;
		var bounds = homeShape.bounds();
		var outerBounds = bounds.inflate(4/16d).inflate(4/16d, 0, 4/16d);
		double longestSide = Math.max(outerBounds.getXsize(), outerBounds.getZsize());
		double longestHeight = outerBounds.getYsize() * Math.sqrt(2);
		double expectedMaxRadius = longestSide * Math.sqrt(2);
		int zoneRenderWidth = (int) expectedMaxRadius * 16;
		int fooX = (width()) / 2;
		int fooY = (height()) / 2;
		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(fooX - zoneRenderWidth / 4f, fooY + longestHeight / 4f, 20);
		float shift = (float)expectedMaxRadius * 5f;
		guiGraphics.pose().rotateAround(Axis.XP.rotationDegrees(rotX), 0, 0, 0);
		guiGraphics.pose().rotateAround(Axis.YP.rotationDegrees(rotY), shift, 0, shift);
		guiGraphics.pose().scale(scaleFactor, -scaleFactor, scaleFactor);

		int boundColor = ChatFormatting.DARK_GRAY.getColor() | 0x40000000;
		BoxRenderer.renderBlockOutline(guiGraphics.pose(), boundBoxLines.lines, boundColor, 2);

		int color = ChatFormatting.YELLOW.getColor() | 0xFF000000;
		BoxRenderer.renderBlockOutline(guiGraphics.pose(), boxLines.lines, color, 2);

		if(this.hoveredZone != null) {
			for(var zoneEntry : zoneBoxes.entrySet()) {
				var zoneId = zoneEntry.getKey();
				var zoneBox = zoneEntry.getValue();

				if(zoneId.equals(hoveredZone.id())) {
					int selectedColor = ChatFormatting.GREEN.getColor() | 0x80000000;
					BoxRenderer.renderBlockOutline(guiGraphics.pose(), zoneBox.lines, selectedColor, 3);
				}
			}
		}

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

		guiGraphics.pose().popPose();
	}

}
