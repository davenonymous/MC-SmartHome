package com.davenonymous.smarthome.lib.gui.widgets.layout;

import com.davenonymous.smarthome.lib.gui.event.VisibilityChangedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.event.WidgetSizeChangeEvent;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public abstract class FlexSizer extends WidgetPanel {
	public enum FlexMode {
		FIXED,
		FLEX,
		CONTENT
	}

	public enum FlexAlign {
		START,
		CENTER,
		FILL,
		END
	}

	public enum FlexDirection {
		X,
		Y
	}

	Map<Integer, Integer> flexValues;
	Map<Integer, FlexMode> flexModes;
	Map<Integer, Widget> flexWidgets;
	Map<Integer, FlexAlign> flexAligns;

	// TODO: These should contain the calculated values
	Map<Integer, Integer> realBoxOffsets;
	Map<Integer, Integer> realBoxSize;

	FlexDirection flexDirection = FlexDirection.X;
	public int paddingHorizontal = 0;
	public int paddingVertical = 0;
	public int spacing = 4;

	public FlexSizer() {
		super();
		this.flexWidgets = new HashMap<>();
		this.flexValues = new HashMap<>();
		this.flexModes = new HashMap<>();
		this.flexAligns = new HashMap<>();
		this.realBoxOffsets = new HashMap<>();
		this.realBoxSize = new HashMap<>();

		this.addListener(
			WidgetSizeChangeEvent.class, ((event, widget) -> {
				update(this);
				return WidgetEventResult.CONTINUE_PROCESSING;
			})
		);
	}

	@Override
	public void clear() {
		super.clear();
		flexValues.clear();
		flexModes.clear();
		flexWidgets.clear();
		flexAligns.clear();
		realBoxOffsets.clear();
		realBoxSize.clear();
	}

	public boolean isHorizontal() {
		return flexDirection == FlexDirection.X;
	}

	public boolean isVertical() {
		return flexDirection == FlexDirection.Y;
	}

	public int getTotalRealSize() {
		int lastOffset = realBoxOffsets.get(realBoxOffsets.size()-1) + realBoxSize.get(realBoxSize.size()-1) + (realBoxOffsets.size()-1)*spacing;
		// Add horizontal or vertical padding to the end, depending on direction
		return lastOffset + (isHorizontal() ? paddingHorizontal : paddingVertical);
	}

	public FlexSizer setFlexDirection(FlexDirection direction) {
		this.flexDirection = direction;
		return this;
	}

	public FlexSizer setPaddingHorizontal(int padding) {
		this.paddingHorizontal = padding;
		return this;
	}

	public FlexSizer setPaddingVertical(int padding) {
		this.paddingVertical = padding;
		return this;
	}

	// For backward compatibility, keep setPadding as a shortcut for both
	public FlexSizer setPadding(int padding) {
		this.paddingHorizontal = padding;
		this.paddingVertical = padding;
		return this;
	}

	public FlexSizer setSpacing(int spacing) {
		this.spacing = spacing;
		return this;
	}

	public FlexSizer addFixedBox(Widget box) {
		return addBox(box, FlexMode.FIXED, FlexAlign.CENTER, isHorizontal() ? box.width : box.height);
	}

	public FlexSizer addFixedBox(Widget box, FlexAlign align) {
		return addBox(box, FlexMode.FIXED, align, isHorizontal() ? box.width : box.height);
	}

	public FlexSizer addFlexBox(Widget box, int flexWeight) {
		return addBox(box, FlexMode.FLEX, FlexAlign.CENTER, flexWeight);
	}

	public FlexSizer addFlexBox(Widget box, FlexAlign align, int flexWeight) {
		return addBox(box, FlexMode.FLEX, align, flexWeight);
	}

	public FlexSizer addContentBox(Widget box, FlexAlign align) {
		box.addListener(
			WidgetSizeChangeEvent.class, ((event, widget) -> {
				if(event.xChanged() && isHorizontal()) {
					update(widget);
				} else if(event.yChanged() && isVertical()) {
					update(widget);
				}
				return WidgetEventResult.CONTINUE_PROCESSING;
			})
		);

		return addBox(box, FlexMode.CONTENT, align,0);

	}

	public FlexSizer addContentBox(Widget box) {
		return addContentBox(box, FlexAlign.CENTER);
	}

	private FlexSizer addBox(Widget box, FlexMode mode, FlexAlign align, int value) {
		flexValues.put(flexValues.size(), value);
		flexModes.put(flexModes.size(), mode);
		flexWidgets.put(flexWidgets.size(), box);
		flexAligns.put(flexAligns.size(), align);

		update(null);
		box.y = paddingVertical;
		box.x = paddingHorizontal;

		if(isHorizontal()) {
			if(box.height <= 0) {
				box.height = this.height;
			}
		} else {
			if(box.width <= 0) {
				box.width = this.width;
			}
		}
		add(box);
		update(null);
		box.addListener(
			VisibilityChangedEvent.class, ((event, widget) -> {
				update(null);
				return WidgetEventResult.CONTINUE_PROCESSING;
			})
		);

		return this;
	}

	public FlexSizer setFlex(int boxNum, int value) {
		flexValues.put(boxNum, value);
		return this;
	}

	public FlexSizer setFlexMode(int boxNum, FlexMode mode) {
		flexModes.put(boxNum, mode);
		return this;
	}


	public FlexSizer update(@Nullable Widget trigger) {
		int totalFlexWeight = 0;
		int totalFixedPx = 0;
		int totalContentPx = 0;
		int totalPadding = (flexWidgets.size() - 1) * (isHorizontal() ? paddingHorizontal : paddingVertical);

		for(var entry : flexModes.entrySet()) {
			int columnIndex = entry.getKey();
			if(!flexWidgets.get(columnIndex).isVisible()) {
				continue;
			}
			int value = flexValues.get(columnIndex);
			switch(entry.getValue()) {
				case FIXED:
					totalFixedPx += value;
					break;
				case FLEX:
					totalFlexWeight += value;
					break;
				case CONTENT:
					var box = flexWidgets.get(columnIndex);
					totalContentPx += isHorizontal() ? box.width : box.height;
					break;
			}
		}

		int thisSize = isHorizontal() ? this.width : this.height;
		int remainingPx = thisSize - totalFixedPx - totalContentPx - totalPadding;
		int pxPerFlex = totalFlexWeight > 0 ? remainingPx / totalFlexWeight : 0;

		int offset = isHorizontal() ? paddingHorizontal : paddingVertical;
		for(var entry : flexValues.entrySet()) {
			int columnIndex = entry.getKey();
			int value = entry.getValue();
			Widget box = flexWidgets.get(columnIndex);
			if(!box.isVisible()) {
				continue;
			}

			switch(flexModes.get(columnIndex)) {
				case FIXED:
					realBoxSize.put(columnIndex, value);
					realBoxOffsets.put(columnIndex, offset);
					offset += value + spacing;
					break;
				case FLEX:
					int flexPx = value * pxPerFlex;
					realBoxSize.put(columnIndex, flexPx);
					realBoxOffsets.put(columnIndex, offset);
					offset += flexPx + spacing;
					break;
				case CONTENT:
					int contentPx = isHorizontal() ? box.width : box.height;
					realBoxSize.put(columnIndex, contentPx);
					realBoxOffsets.put(columnIndex, offset);
					offset += contentPx + spacing;
					break;
			}

			if(trigger == null || trigger != box) {
				var flexAlign = flexAligns.get(columnIndex);
				if(flexDirection == FlexDirection.X) {
					box.setX(realBoxOffsets.get(columnIndex));
					box.setWidth(realBoxSize.get(columnIndex));
					if(box.height > this.height - paddingVertical*2) {
						box.setHeight(this.height - paddingVertical*2);
					}

					switch(flexAlign) {
						case START:
							box.setY(paddingVertical);
							break;
						case CENTER:
							var availableHeight = this.height - box.height - paddingVertical*2;
							box.setY(paddingVertical + Math.round(availableHeight / 2f));
							break;
						case FILL:
							box.setY(paddingVertical);
							box.setHeight(this.height);
							break;
						case END:
							box.setY(paddingVertical + this.height - box.height);
							break;
					}
				} else {
					box.setY(realBoxOffsets.get(columnIndex));
					box.setHeight(realBoxSize.get(columnIndex));
					if(box.width > this.width - paddingHorizontal*2) {
						box.setWidth(this.width - paddingHorizontal*2);
					}
					switch(flexAlign) {
						case START:
							box.setX(paddingHorizontal);
							break;
						case CENTER:
							var availableWidth = this.width - box.width - paddingHorizontal*2;
							box.setX(paddingHorizontal + Math.round(availableWidth / 2f));
							break;
						case FILL:
							box.setX(paddingHorizontal);
							box.setWidth(this.width);
							break;
						case END:
							box.setX(paddingHorizontal + this.width - box.width);
							break;
					}
				}
			}
		}

		return this;
	}

	public void adjustSizeToContent() {
		if(isHorizontal()) {
			this.setWidth(getTotalRealSize());
		} else {
			this.setHeight(getTotalRealSize());
		}
	}
}
