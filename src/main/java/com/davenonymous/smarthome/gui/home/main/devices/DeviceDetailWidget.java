package com.davenonymous.smarthome.gui.home.main.devices;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.configurable.StringInputWidget;
import com.davenonymous.smarthome.lib.gui.event.ValueChangedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.tooltip.WrappedStringTooltipComponent;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetVBox;
import com.davenonymous.smarthome.networking.actions.SetDeviceNamePayload;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.neoforged.neoforge.network.PacketDistributor;

public class DeviceDetailWidget extends WidgetVBox {
	private HomeZone zone;
	private ConfiguredDevice device;

	private StringInputWidget deviceRenameInput;

	public DeviceDetailWidget() {
		this.setPadding(8);
		this.setSpacing(4);

		deviceRenameInput = new StringInputWidget("", "[a-zA-Z0-9_ -!?+:/\\@#$%^&*()]*");
		deviceRenameInput.setDrawBackground(false);
		deviceRenameInput.nativeWidget().setTextColor(ChatFormatting.WHITE.getColor());
		deviceRenameInput.addListener(
			ValueChangedEvent.class, (event, widget) -> {
				var payload = new SetDeviceNamePayload(zone().home().id(), zone().id(), device(), deviceRenameInput.getValue());
				PacketDistributor.sendToServer(payload);
				updateWidgetSizes();
				return WidgetEventResult.HANDLED;
			});
		deviceRenameInput.setTooltipElements(WrappedStringTooltipComponent.orange(I18n.get("smarthome.gui.home.zones.detail.renameable")));
		this.addContentBox(deviceRenameInput, FlexAlign.CENTER);

	}

	public ConfiguredDevice device() {
		return device;
	}

	public HomeZone zone() {
		return zone;
	}

	public DeviceDetailWidget setDevice(HomeZone zone, ConfiguredDevice device) {
		this.zone = zone;
		this.device = device;
		if(device == null) {
			deviceRenameInput.setValue("");
		} else {
			deviceRenameInput.setValue(device.deviceId());
			deviceRenameInput.nativeWidget().scrollTo(0);
			deviceRenameInput.nativeWidget().scrollTo(device.deviceId().length() / 2);
		}
		updateWidgetSizes();
		return this;
	}

	@Override
	public void updateWidgetSizes() {
		super.updateWidgetSizes();
		deviceRenameInput.autoWidth();
		deviceRenameInput.setHeight(12);
		this.update(null);
	}


	@Override
	public void draw(GuiGraphics guiGraphics, Window window) {
		if(device == null) {
			return;
		}

		guiGraphics.blitSprite(SmartHome.sprite(GuiTheme.SpriteComponent.WINDOW_PUSHED_BACKGROUND), 0, 0, this.width, this.height);
		guiGraphics.fill(3, 3, width()-3, height()-3, 0x88000000);

		super.draw(guiGraphics, window);
	}
}
