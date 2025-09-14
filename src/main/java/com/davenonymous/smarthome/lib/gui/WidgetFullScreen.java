package com.davenonymous.smarthome.lib.gui;

import com.davenonymous.smarthome.lib.gui.event.UpdateScreenEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetCloseButton;
import net.minecraft.network.chat.Component;

public abstract class WidgetFullScreen extends WidgetScreen {
	private int lastWindowWidth;
	private int lastWindowHeight;
	private int shrinkWidth = 0;
	private double originalGuiScale = -1.0d;

	WidgetCloseButton closeButton;

	protected WidgetFullScreen(Component title) {
		this(title, -1.0d);
	}

	protected WidgetFullScreen(Component title, double forcedGuiScale) {
		super(title);

		if(forcedGuiScale > 0.0d) {
			this.originalGuiScale = this.window.getGuiScale();
			this.window.setGuiScale(forcedGuiScale);
		}

		this.width = window.getGuiScaledWidth();
		this.height = window.getGuiScaledHeight();
		this.lastWindowWidth = this.width;
		this.lastWindowHeight = this.height;
		this.closeButton = new WidgetCloseButton();
		this.closeButton.setPosition(this.width - 14, 4);
	}

	protected abstract void updateWidgetSizes();

	@Override
	public void onClose() {
		if(this.originalGuiScale > 0.0d) {
			window.setGuiScale(this.originalGuiScale);
		}

		super.onClose();
	}

	@Override
	protected GUI createGUI() {
		GUI gui = new GUI(0, 0, this.width, this.height);

		// Always scale to full screen size
		gui.addListener(
			UpdateScreenEvent.class, (event, widget) -> {
				int guiScaledWidth = this.window.getGuiScaledWidth() - this.shrinkWidth;
				int guiScaledHeight = this.window.getGuiScaledHeight();

				if(this.lastWindowWidth != guiScaledWidth || this.lastWindowHeight != guiScaledHeight) {
					this.lastWindowWidth = guiScaledWidth;
					this.lastWindowHeight = guiScaledHeight;

					this.width = this.lastWindowWidth;
					this.height = this.lastWindowHeight;
					gui.setSize(this.width, this.height);

					this.closeButton.setPosition(this.width - 14, 4);
					updateWidgetSizes();
				}
				return WidgetEventResult.CONTINUE_PROCESSING;
			}
		);

		gui.add(this.closeButton);

		return gui;
	}

	protected void setShrinkWidth(int shrinkWidth) {
		this.shrinkWidth = shrinkWidth;
	}
}
