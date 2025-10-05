package com.davenonymous.smarthome.gui.general;

import com.davenonymous.smarthome.gui.events.ScaleHandleResizedEvent;
import com.davenonymous.smarthome.lib.HackerNoon;
import com.davenonymous.smarthome.lib.gui.event.*;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetSprite;

public class ScaleHandle extends WidgetSprite {
	private Widget attachedTo;

	public ScaleHandle(Widget attachedTo) {
		super(HackerNoon.Regular.expand);
		this.attachedTo = attachedTo;

		this.setColor(0xFFAAAAAA, 0xFFFFFFFF);

		this.addListener(
			MouseEnterEvent.class, (event, widget) -> {
				this.setScale(1.5f);
				this.setPosition(
					this.attachedTo.x() + this.attachedTo.width - this.width / 2,
					this.attachedTo.y() + this.attachedTo.height - this.height / 2
				);
				return WidgetEventResult.HANDLED;
			});

		this.addListener(
			MouseExitEvent.class, (event, widget) -> {
				this.setScale(1f);
				this.setPosition(
					this.attachedTo.x() + this.attachedTo.width - this.width / 2,
					this.attachedTo.y() + this.attachedTo.height - this.height / 2
				);
				return WidgetEventResult.HANDLED;
			});

		this.addListener(
			MouseReleasedEvent.class, (event, widget) -> {
				if(!this.isHovered()) {
					return WidgetEventResult.CONTINUE_PROCESSING;
				}
				this.setScale(1f);
				this.setPosition(
					this.attachedTo.x() + this.attachedTo.width - this.width / 2,
					this.attachedTo.y() + this.attachedTo.height - this.height / 2
				);
				this.fireEvent(new ScaleHandleResizedEvent(this, this.attachedTo));
				return WidgetEventResult.HANDLED;
			});

		this.addListener(
			MouseDraggedEvent.class, (event, widget) -> {
				if(getGUI().isDragging() != null) {
					// If the GUI is already being dragged, ignore this event
					return WidgetEventResult.CONTINUE_PROCESSING;
				}

				if(this.attachedTo == null) {
					return WidgetEventResult.CONTINUE_PROCESSING;
				}

				int newScaleHandleX = Math.round(getMouseX() - this.width / 2f);
				int newScaleHandleY = Math.round(getMouseY() - this.height / 2f);
				this.attachedTo.setWidth(this.attachedTo.width + newScaleHandleX);
				this.attachedTo.setHeight(this.attachedTo.height + newScaleHandleY);

				this.setPosition(
					this.attachedTo.x() + this.attachedTo.width - this.width / 2,
					this.attachedTo.y() + this.attachedTo.height - this.height / 2
				);
				return WidgetEventResult.CONTINUE_PROCESSING;
			});
	}

	@Override
	public void updateWidgetSizes() {
		super.updateWidgetSizes();
		this.setPosition(
			attachedTo.x() + attachedTo.width - this.width / 2,
			attachedTo.y() + attachedTo.height - this.height / 2
		);
	}
}
