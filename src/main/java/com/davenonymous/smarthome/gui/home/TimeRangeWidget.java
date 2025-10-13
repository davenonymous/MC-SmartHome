package com.davenonymous.smarthome.gui.home;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.data.TimeRangeEnum;
import com.davenonymous.smarthome.gui.general.VerticalSelectorPopupWidget;
import com.davenonymous.smarthome.lib.gui.ColorHelper;
import com.davenonymous.smarthome.lib.gui.ContentAlignment;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.event.GuiDataUpdatedEvent;
import com.davenonymous.smarthome.lib.gui.event.MouseClickEvent;
import com.davenonymous.smarthome.lib.gui.event.ValueChangedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanelWithValue;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;

public class TimeRangeWidget extends WidgetPanelWithValue<TimeRangeEnum> {

	VerticalSelectorPopupWidget selector;

	public TimeRangeWidget() {
		this(TimeRangeEnum.LAST_6_HOURS);
	}

	public TimeRangeWidget(TimeRangeEnum timeRange) {
		super(timeRange);

		this.addListener(ValueChangedEvent.class, (event, widget) -> {
			this.updateContent();
			return WidgetEventResult.CONTINUE_PROCESSING;
		});

		this.addListener(GuiDataUpdatedEvent.class, (event, widget) -> {
			this.updateContent();
			return WidgetEventResult.CONTINUE_PROCESSING;
		});

		this.updateContent();
	}

	private void updateContent() {
		this.clear();

		var self = this;
		WidgetTextBox label = new WidgetTextBox(getValue().label().get(), ChatFormatting.DARK_GRAY.getColor(), ColorHelper.COLOR_ORANGE) {
			@Override
			public boolean isHovered() {
				return super.isHovered() || self.isHovered();
			}
		};
		label.autoWidth();
		label.autoHeight();
		label.setPosition((int) ((120 - label.width)/2f), 5);
		this.add(label);

		this.addListener(MouseClickEvent.class, (event, widget) -> {
			if(selector != null && getGUI().children().contains(selector)) {
				getGUI().remove(selector);
				selector = null;
			}

			selector = VerticalSelectorPopupWidget.openAt(getActualX() + getMouseX(), getActualY() + getMouseY() + 32, ContentAlignment.BOTTOM_CENTER,
				createChoiceWidget(TimeRangeEnum.LAST_15_MINUTES),
				createChoiceWidget(TimeRangeEnum.LAST_30_MINUTES),
				createChoiceWidget(TimeRangeEnum.LAST_HOUR),
				createChoiceWidget(TimeRangeEnum.LAST_6_HOURS),
				createChoiceWidget(TimeRangeEnum.LAST_12_HOURS),
				createChoiceWidget(TimeRangeEnum.LAST_24_HOURS),
				createChoiceWidget(TimeRangeEnum.LAST_3_DAYS),
				createChoiceWidget(TimeRangeEnum.LAST_7_DAYS)
			);
			selector.zLevel += 20;
			getGUI().add(selector);
			return WidgetEventResult.HANDLED;
		});

		this.setSize(Math.max(120, label.width() + 8), label.height() + 8);
	}

	private WidgetTextBox createChoiceWidget(TimeRangeEnum range) {
		WidgetTextBox label = new WidgetTextBox(range.label().get(), ChatFormatting.GRAY.getColor(), ColorHelper.COLOR_ORANGE);
		label.autoWidth();
		label.autoHeight();
		label.addListener(MouseClickEvent.class, (event, widget) -> {
			var oldValue = this.getValue();
			this.setValue(range);
			if(selector != null && getGUI().children().contains(selector)) {
				getGUI().remove(selector);
				selector = null;
			}
			this.fireEvent(new ValueChangedEvent<>(oldValue, this.getValue()));
			return WidgetEventResult.HANDLED;
		});
		return label;
	}

	@Override
	public void draw(GuiGraphics guiGraphics, Window window) {
		guiGraphics.blitSprite(SmartHome.sprite(GuiTheme.SpriteComponent.BUTTON_R2), 0, 0, this.width, this.height);
		if(this.isHovered()) {
			guiGraphics.fill(1, 1, this.width-1, this.height-1, 0x80000000);
		}
		super.draw(guiGraphics, window);
	}
}
