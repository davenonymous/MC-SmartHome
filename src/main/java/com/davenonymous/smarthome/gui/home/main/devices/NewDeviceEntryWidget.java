package com.davenonymous.smarthome.gui.home.main.devices;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.FoundDevice;
import com.davenonymous.smarthome.data.HomeZone;
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
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.networking.actions.devices.AddDevicePayload;
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

	@I18DataGen(lang = "en_us", string = "Click to rename")
	@I18DataGen(lang = "de_de", string = "Klicken zum Umbenennen")
	public static final I18String CLICK_TO_RENAME = I18String.gui("devices", "add.renameable");

	@I18DataGen(lang = "en_us", string = "%d sensors")
	@I18DataGen(lang = "de_de", string = "%d Sensoren")
	public static final I18String SENSORS_COUNT = I18String.gui("devices", "add.sensors.count");

	@I18DataGen(lang = "en_us", string = "Add device")
	@I18DataGen(lang = "de_de", string = "Gerät hinzufügen")
	public static final I18String ADD_DEVICE = I18String.gui("devices", "add.add_device");

	@I18DataGen(lang = "en_us", string = "Ignore device")
	@I18DataGen(lang = "de_de", string = "Gerät ignorieren")
	public static final I18String IGNORE_DEVICE = I18String.gui("devices", "add.ignore_device");

	@I18DataGen(lang = "en_us", string = "Ignored devices can still be added later from the settings menu.")
	@I18DataGen(lang = "de_de", string = "Ignorierte Geräte können später über das Einstellungsmenü hinzugefügt werden.")
	public static final I18String IGNORE_DEVICE_HINT = I18String.gui("devices", "add.ignore_device.hint");

	public NewDeviceEntryWidget(HomeZone zone, FoundDevice device) {
		this.setSize(100, 100);

		deviceName = new StringInputWidget(I18n.get(device.state().getBlock().getDescriptionId()), "[a-zA-Z0-9_ -!?+:/\\@#$%^&*()]*");
		deviceName.setDrawBackground(false);
		deviceName.nativeWidget().setTextColor(ChatFormatting.WHITE.getColor());
		deviceName.setTooltipElements(WrappedStringTooltipComponent.orange(CLICK_TO_RENAME.get()));
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

		sensorCount = new WidgetTextBox(SENSORS_COUNT.get(device.sensorIds().size()));
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
			WrappedStringTooltipComponent.white(ADD_DEVICE.get())
		);
		addButton.addListener(MouseClickEvent.class, (event, widget) -> {
			var configured = new ConfiguredDevice(device.pos(), deviceName.getValue(), device.state().getBlock().builtInRegistryHolder().getKey().location(), zone.home().settings().autoEnableNewDevices(), false);
			PacketDistributor.sendToServer(new AddDevicePayload(zone.home().id(), zone.id(), configured));
			addButton.setEnabled(false);
			return WidgetEventResult.HANDLED;
		});
		this.add(addButton);

		ignoreButton = new AddDeviceButtonWidget(HackerNoon.Solid.trashAlt);
		ignoreButton.setButtonColor(ColorHelper.COLOR_ERRORED.getRGB());
		ignoreButton.setPosition(8, this.height - ignoreButton.height - 8);
		ignoreButton.setTooltipElements(
			WrappedStringTooltipComponent.white(IGNORE_DEVICE.get()),
			WrappedStringTooltipComponent.gray(IGNORE_DEVICE_HINT.get())
		);
		ignoreButton.addListener(MouseClickEvent.class, (event, widget) -> {
			var configured = new ConfiguredDevice(device.pos(), deviceName.getValue(), device.state().getBlock().builtInRegistryHolder().getKey().location(), zone.home().settings().autoEnableNewDevices(), true);
			PacketDistributor.sendToServer(new AddDevicePayload(zone.home().id(), zone.id(), configured));
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
