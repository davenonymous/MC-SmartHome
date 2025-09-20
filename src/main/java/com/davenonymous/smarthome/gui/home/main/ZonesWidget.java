package com.davenonymous.smarthome.gui.home.main;

import com.davenonymous.smarthome.client.BoxRenderer;
import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.lib.gui.event.MouseScrollEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.particles.util.BoxLineCache;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashMap;
import java.util.Map;

public class ZonesWidget extends WidgetPanel {
	VoxelShape homeShape;
	BoxLineCache boxLines;
	BoxLineCache boundBoxLines;
	Map<String, BoxLineCache> zoneBoxes;
	public String selectedZone = null;

	float rotX = -30;
	float rotY = -35;

	public ZonesWidget() {
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
			var shape = Shapes.create(zone.bounds().move(-selectedHome.shape().bounds().minX, -selectedHome.shape().bounds().minY, -selectedHome.shape().bounds().minZ).deflate(1/16d));
			var boxLineCache = new BoxLineCache();
			boxLineCache.addShape(shape);
			zoneBoxes.put(zone.name(), boxLineCache);
		}
	}


	@Override
	public void draw(GuiGraphics guiGraphics, Screen screen) {
		super.draw(guiGraphics, screen);

		if(boxLines == null || boxLines.lines.isEmpty()) {
			return;
		}

		if(homeShape.isEmpty()) {
			return;
		}

		var bounds = homeShape.bounds();
		var outerBounds = bounds.inflate(4/16d).inflate(4/16d, 0, 4/16d);
		double longestSide = Math.max(outerBounds.getXsize(), outerBounds.getZsize());
		double longestHeight = outerBounds.getYsize() * Math.sqrt(2);
		double expectedMaxRadius = longestSide * Math.sqrt(2);
		int zoneRenderWidth = (int) expectedMaxRadius * 16;
		int fooX = (width()) / 2;
		int fooY = (height()) / 2;
		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(fooX - zoneRenderWidth / 4f, fooY + longestHeight / 4f, 100);
		float shift = (float)expectedMaxRadius * 5f;
		guiGraphics.pose().rotateAround(Axis.XP.rotationDegrees(rotX), 0, 0, 0);
		guiGraphics.pose().rotateAround(Axis.YP.rotationDegrees(rotY), shift, 0, shift);
		guiGraphics.pose().scale(16f, -16f, 16f);

		int boundColor = ChatFormatting.DARK_GRAY.getColor() | 0x40000000;
		BoxRenderer.renderBlockOutline(guiGraphics.pose(), boundBoxLines.lines, boundColor, 2);

		int color = ChatFormatting.YELLOW.getColor() | 0xFF000000;
		BoxRenderer.renderBlockOutline(guiGraphics.pose(), boxLines.lines, color, 2);

		for(var zoneEntry : zoneBoxes.entrySet()) {
			var zoneName = zoneEntry.getKey();
			var zoneBox = zoneEntry.getValue();

			if(zoneName.equals(selectedZone)) {
				int selectedColor = ChatFormatting.GREEN.getColor() | 0x80000000;
				BoxRenderer.renderBlockOutline(guiGraphics.pose(), zoneBox.lines, selectedColor, 3);
			}
		}

		guiGraphics.pose().popPose();
	}

}
