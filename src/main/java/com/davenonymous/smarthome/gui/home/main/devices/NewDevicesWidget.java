package com.davenonymous.smarthome.gui.home.main.devices;

import com.davenonymous.smarthome.data.FoundDevice;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetHBox;
import com.mojang.blaze3d.platform.Window;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class NewDevicesWidget extends WidgetHBox {
	public NewDevicesWidget() {
		this.setSpacing(4);
	}

	@Override
	public void updateWidgetSizes() {
		super.updateWidgetSizes();
		int childWidth = 0;
		for(var child : this.children()) {
			childWidth += child.width;
			childWidth += this.spacing;

			if(childWidth > this.width) {
				child.setVisible(false);
				continue;
			}

			child.setVisible(true);
		}
	}

	public void updateDevices(Map<HomeZone, List<FoundDevice>> deviceMap) {
		this.clear();
		if(deviceMap.isEmpty()) {
			return;
		}

		List<Pair<HomeZone, FoundDevice>> allDevices = deviceMap.entrySet().stream()
			.flatMap(entry -> entry.getValue().stream().map(device -> Pair.of(entry.getKey(), device)))
			.sorted(Comparator.comparing(pair -> I18n.get(pair.getSecond().state().getBlock().getDescriptionId()), Comparator.naturalOrder()))
			.toList();

		for(var entry : allDevices) {
			HomeZone zone = entry.getFirst();
			FoundDevice device = entry.getSecond();
			this.addContentBox(new NewDeviceEntryWidget(zone, device));
		}
	}

	@Override
	public void draw(GuiGraphics guiGraphics, Window window) {
		super.draw(guiGraphics, window);
	}
}
