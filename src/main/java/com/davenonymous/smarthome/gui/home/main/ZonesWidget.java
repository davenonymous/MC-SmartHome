package com.davenonymous.smarthome.gui.home.main;

import com.davenonymous.smarthome.client.BoxRenderer;
import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.lib.gui.event.GuiDataUpdatedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.particles.util.BoxLineCache;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

public class ZonesWidget extends WidgetPanel {
	VoxelShape homeShape;
	BoxLineCache boxLines;
	BoxLineCache boundBoxLines;
	List<BoxLineCache> zoneBoxes;


	public ZonesWidget() {
		this.addListener(GuiDataUpdatedEvent.class, (event, widget) -> {
			refreshZoneList();
			return WidgetEventResult.CONTINUE_PROCESSING;
		});

		refreshZoneList();
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
		var boundShape = Shapes.create(bounds.inflate(4/16d).inflate(4/16d, 0, 4/16d));
		boundBoxLines = new BoxLineCache();
		boundBoxLines.addShape(boundShape);

		zoneBoxes = new ArrayList<>();
		for(var zone : selectedHome.zones()) {
			var shape = Shapes.create(zone.bounds().move(-selectedHome.shape().bounds().minX, -selectedHome.shape().bounds().minY, -selectedHome.shape().bounds().minZ).deflate(1/16d));
			var boxLineCache = new BoxLineCache();
			boxLineCache.addShape(shape);
			zoneBoxes.add(boxLineCache);
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

		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(this.width() / 2f, this.height() / 2f, 0f);
		guiGraphics.pose().rotateAround(Axis.XP.rotationDegrees(-30), 0, 0, 0);
		guiGraphics.pose().rotateAround(Axis.YP.rotationDegrees(-35), 0, 0, 0);
		guiGraphics.pose().scale(16f, -16f, 16f);



		int boundColor = ChatFormatting.DARK_GRAY.getColor() | 0x40000000;
		BoxRenderer.renderBlockOutline(guiGraphics.pose(), boundBoxLines.lines, boundColor, 2);

		int color = ChatFormatting.YELLOW.getColor() | 0xFF000000;
		BoxRenderer.renderBlockOutline(guiGraphics.pose(), boxLines.lines, color, 2);

		var selectedHome = HomeScreen.get().selectedHome;
		if (selectedHome != null) {
			for (int i = 0; i < selectedHome.zones().size(); i++) {
				var zone = selectedHome.zones().get(i);
				var zoneBox = zoneBoxes.get(i);

				int zoneColor = ChatFormatting.GREEN.getColor() | 0x80000000;
				//BoxRenderer.renderBlockOutline(guiGraphics.pose(), zoneBox.lines, zoneColor, 1);
			}
		}

		guiGraphics.pose().popPose();
	}

}
