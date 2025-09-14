package com.davenonymous.smarthome.lib.gui.widgets;

import com.davenonymous.smarthome.lib.gui.GUIHelper;
import com.davenonymous.smarthome.lib.gui.event.MouseDraggedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.event.WidgetSizeChangeEvent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.joml.Vector2f;

import java.util.HashMap;
import java.util.Map;

public class WidgetPanningPanel extends WidgetPanel {
	float panX = 0.0f;
	float panY = 0.0f;

	int minX = Integer.MAX_VALUE;
	int minY = Integer.MAX_VALUE;
	int maxX = Integer.MIN_VALUE;
	int maxY = Integer.MIN_VALUE;
	int canvasWidth = 0;
	int canvasHeight = 0;
	Map<Widget, Vector2f> floatPositions = new HashMap<>();

	public WidgetPanningPanel() {
		this.addListener(MouseDraggedEvent.class, (event, widget) -> {
			if(getGUI().isDragging() != null) {
				// If the GUI is already being dragged, ignore this event
				return WidgetEventResult.CONTINUE_PROCESSING;
			}

			// Adjust the pan based on the mouse drag
			this.panX += (float) event.dragX();
			this.panY += (float) event.dragY();

			repositionWidgets(panX, panY);
			return WidgetEventResult.CONTINUE_PROCESSING;
		});
	}

	private void repositionWidgets(float dx, float dy) {
		resetCanvasSize();
		for (Widget child : this.children) {
			Vector2f position = new Vector2f(floatPositions.computeIfAbsent(child, k -> new Vector2f())).add(dx, dy);
			child.x = (int)Math.floor(position.x);
			child.y = (int)Math.floor(position.y);
			adjustCanvasToWidget(child);
		}
	}

	@Override
	public void add(Widget widget) {
		super.add(widget);
		this.adjustCanvasToWidget(widget);
		floatPositions.put(widget, new Vector2f(widget.x, widget.y));

		widget.addListener(WidgetSizeChangeEvent.class, (event, w) -> {
			// Adjust the canvas size if the widget size changes
			if (w.x < minX || w.y < minY || w.x + w.width > maxX || w.y + w.height > maxY) {
				this.adjustCanvasToWidget(w);
			}
			floatPositions.put(w, new Vector2f(w.x, w.y));
			return WidgetEventResult.CONTINUE_PROCESSING;
		});
	}

	public void setFloatPosition(Widget widget, float x, float y) {
		Vector2f position = new Vector2f(x, y);
		widget.setX((int)Math.floor(position.x + panX));
		widget.setY((int)Math.floor(position.y + panY));
		floatPositions.put(widget, position);
		adjustCanvasToWidget(widget);
	}

	public void updateFloatPosition(Widget widget, int dx, int dy) {
		if(dx == 0 && dy == 0) {
			// No movement, no need to update
			return;
		}

		Vector2f position = new Vector2f(floatPositions.computeIfAbsent(widget, k -> new Vector2f())).add(dx, dy);
		widget.setX((int)Math.floor(position.x+panX));
		widget.setY((int)Math.floor(position.y+panY));
		floatPositions.put(widget, position);
		//adjustCanvasToWidget(widget);
	}

	private void resetCanvasSize() {
		this.minX = Integer.MAX_VALUE;
		this.minY = Integer.MAX_VALUE;
		this.maxX = Integer.MIN_VALUE;
		this.maxY = Integer.MIN_VALUE;
		this.canvasWidth = 0;
		this.canvasHeight = 0;
	}

	private void adjustCanvasToWidget(Widget widget) {
		if (widget.x < minX) this.minX = widget.x;
		if (widget.y < minY) this.minY = widget.y;
		if (widget.x + widget.width > maxX) this.maxX = widget.x + widget.width;
		if (widget.y + widget.height > maxY) this.maxY = widget.y + widget.height;

		this.canvasWidth = maxX - minX;
		this.canvasHeight = maxY - minY;
	}

	public void centerOnCanvas() {
		this.panX = (canvasWidth - this.width) / -2.0f;
		this.panY = (canvasHeight - this.height) / -2.0f;
		repositionWidgets(panX, panY);
	}

	@Override
	public void clear() {
		super.clear();
		this.resetCanvasSize();
	}

	@Override
	public void remove(Widget widget) {
		super.remove(widget);
		if (widget.x == minX || widget.y == minY || widget.x + widget.width == maxX || widget.y + widget.height == maxY) {
			// Recalculate the bounds
			this.resetCanvasSize();
			this.children.forEach(this::adjustCanvasToWidget);
		}
	}

	@Override
	public void renderExtraDebugInfo(GuiGraphics pGuiGraphics, Screen screen) {
		String visibleWidth = "Canvas Width: " + this.canvasWidth;
		String visibleHeight = "Canvas Height: " + this.canvasHeight;
		String panPosition = "Pan: x=" + this.panX + ", y=" + this.panY;

		pGuiGraphics.drawString(screen.getMinecraft().font, visibleWidth, 0, 30, 0xFF8000);
		pGuiGraphics.drawString(screen.getMinecraft().font, visibleHeight, 0, 40, 0xFF8000);
		pGuiGraphics.drawString(screen.getMinecraft().font, panPosition, 0, 50, 0xFF8000);
	}

	public void drawOnCanvas(GuiGraphics guiGraphics, Screen screen) {

	}

	@Override
	public void draw(GuiGraphics guiGraphics, Screen screen) {
		guiGraphics.enableScissor(this.x, this.y, this.x + width, this.y + height);
		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(0, 0, -5);
		GUIHelper.drawColoredCanvas(guiGraphics, this.width, this.height, 0xFF222222);
		drawOnCanvas(guiGraphics, screen);
		guiGraphics.pose().popPose();
		super.draw(guiGraphics, screen);
		guiGraphics.disableScissor();
	}
}
