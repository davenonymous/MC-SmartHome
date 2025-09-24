package com.davenonymous.smarthome.gui.home.main.devices;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.FoundDevice;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.data.IgnoredDevice;
import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.lib.HackerNoon;
import com.davenonymous.smarthome.lib.gui.ColorHelper;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.configurable.StringInputWidget;
import com.davenonymous.smarthome.lib.gui.event.MouseClickEvent;
import com.davenonymous.smarthome.lib.gui.event.ValueChangedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.tooltip.WrappedStringTooltipComponent;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetItemStack;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.networking.actions.AddDevicePayload;
import com.davenonymous.smarthome.networking.actions.IgnoreDevicePayload;
import com.davenonymous.smarthome.setup.content.ModFonts;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class NewDeviceEntryWidget extends WidgetPanel {
	StringInputWidget deviceName;
	WidgetTextBox zoneName;
	WidgetItemStack deviceIcon;
	WidgetTextBox sensorCount;

	AddDeviceButtonWidget addButton;
	AddDeviceButtonWidget ignoreButton;

	public NewDeviceEntryWidget(HomeZone zone, FoundDevice device) {
		this.setSize(100, 100);

		deviceName = new StringInputWidget(I18n.get(device.state().getBlock().getDescriptionId()), "[a-zA-Z0-9_ -!?+:/\\@#$%^&*()]*");
		deviceName.setDrawBackground(false);
		deviceName.nativeWidget().setTextColor(ChatFormatting.WHITE.getColor());
		deviceName.setTooltipElements(WrappedStringTooltipComponent.orange(I18n.get("smarthome.gui.home.devices.add.renameable")));
		deviceName.autoWidth();
		deviceName.setHeight(12);
		deviceName.setPosition(this.width / 2 - deviceName.width / 2, 8);
		deviceName.addListener(ValueChangedEvent.class, (event, widget) -> {
			deviceName.autoWidth();
			deviceName.nativeWidget().scrollTo(0);
			deviceName.nativeWidget().scrollTo(1);
			deviceName.nativeWidget().scrollTo(0);
			deviceName.setPosition(this.width / 2 - deviceName.width / 2, 8);
			return WidgetEventResult.HANDLED;
		});
		this.add(deviceName);

		zoneName = new WidgetTextBox(zone.name());
		zoneName.setFont(ModFonts.TINY);
		zoneName.setTextColor(0xFFAAAAAA);
		zoneName.autoWidth(this.width);
		zoneName.autoHeight();
		zoneName.setPosition(this.width / 2 - zoneName.width / 2, deviceName.y + deviceName.height + 2);
		this.add(zoneName);

		var stack = new ItemStack(device.state().getBlock().asItem());
		deviceIcon = new WidgetItemStack(stack);
		deviceIcon.setPosition(this.width / 2 - deviceIcon.width() / 2, zoneName.y + zoneName.height + 4);
		this.add(deviceIcon);

		sensorCount = new WidgetTextBox(I18n.get("smarthome.gui.home.devices.add.sensors.count", device.sensorIds().size()));
		sensorCount.setFont(ModFonts.TINY);
		sensorCount.setTextColor(0xFFAAAAAA);
		sensorCount.autoWidth(this.width);
		sensorCount.autoHeight();
		sensorCount.setPosition(this.width / 2 - sensorCount.width / 2, deviceIcon.y + deviceIcon.height() + 4);
		this.add(sensorCount);

		addButton = new AddDeviceButtonWidget(HackerNoon.Solid.plus);
		addButton.setButtonColor(ColorHelper.COLOR_GREEN);
		addButton.setPosition(this.width - addButton.width - 8, this.height - addButton.height - 8);
		addButton.setTooltipElements(
			WrappedStringTooltipComponent.white(I18n.get("smarthome.gui.home.devices.add.add_device"))
		);
		addButton.addListener(MouseClickEvent.class, (event, widget) -> {
			var configured = new ConfiguredDevice(device.pos(), deviceName.getValue(), device.state().getBlock().builtInRegistryHolder().getKey().location());
			PacketDistributor.sendToServer(new AddDevicePayload(zone.home().id(), zone.id(), configured));
			addButton.setEnabled(false);
			return WidgetEventResult.HANDLED;
		});
		this.add(addButton);

		ignoreButton = new AddDeviceButtonWidget(HackerNoon.Solid.trashAlt);
		ignoreButton.setButtonColor(ColorHelper.COLOR_ERRORED.getRGB());
		ignoreButton.setPosition(8, this.height - ignoreButton.height - 8);
		ignoreButton.setTooltipElements(
			WrappedStringTooltipComponent.white(I18n.get("smarthome.gui.home.devices.add.ignore_device")),
			WrappedStringTooltipComponent.gray(I18n.get("smarthome.gui.home.devices.add.ignore_device.hint"))
		);
		ignoreButton.addListener(MouseClickEvent.class, (event, widget) -> {
			PacketDistributor.sendToServer(new IgnoreDevicePayload(zone.home().id(), zone.id(), new IgnoredDevice(device.pos(), device.state())));
			ignoreButton.setEnabled(false);
			return WidgetEventResult.HANDLED;
		});
		this.add(ignoreButton);
	}

	@Override
	public void updateWidgetSizes() {
		super.updateWidgetSizes();

	}

	@Override
	public void draw(GuiGraphics guiGraphics, Window window) {
		//guiGraphics.blitSprite(SmartHome.sprite(GuiTheme.SpriteComponent.BUTTON_R3), 0, 0, this.width, this.height);
		guiGraphics.blitSprite(SmartHome.sprite(GuiTheme.SpriteComponent.WINDOW_PUSHED_BACKGROUND), 0, 0, this.width, this.height);
		guiGraphics.fill(3, 3, width()-3, height()-3, 0x88000000);
		super.draw(guiGraphics, window);
	}
}
