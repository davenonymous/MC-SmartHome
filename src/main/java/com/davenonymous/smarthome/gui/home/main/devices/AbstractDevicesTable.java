package com.davenonymous.smarthome.gui.home.main.devices;

import com.davenonymous.smarthome.lib.gui.CellData;
import com.davenonymous.smarthome.lib.gui.event.MouseClickEvent;
import com.davenonymous.smarthome.lib.gui.event.MouseMoveEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTable;

public abstract class AbstractDevicesTable extends WidgetTable {
	int hoveredRow = -1;

	public AbstractDevicesTable() {
		setCellPaddingHorizontal(8);
		setCellPaddingVertical(2);

		this.addListener(MouseClickEvent.class, (event, widget) -> {
			var hovered = getHoveredWidgets();
			if(hovered.isEmpty()) {
				return WidgetEventResult.CONTINUE_PROCESSING;
			}

			var pos = getPosForWidget(hovered.getFirst());
			if(pos == null) {
				return WidgetEventResult.CONTINUE_PROCESSING;
			}

			onRowClick(pos.height, this.get(pos.width, pos.height));
			return WidgetEventResult.CONTINUE_PROCESSING;
		});

		this.addListener(MouseMoveEvent.class, (event, widget) -> {
			var hovered = getHoveredWidgets();
			if(hovered.isEmpty()) {
				if(hoveredRow != -1) {
					onRowHoverEnd(hoveredRow);
				}
				hoveredRow = -1;
				return WidgetEventResult.CONTINUE_PROCESSING;
			}

			var pos = getPosForWidget(hovered.getFirst());
			if(pos != null && pos.height != hoveredRow) {
				if(hoveredRow != -1) {
					onRowHoverEnd(hoveredRow);
				}
				hoveredRow = pos.height;
				onRowHoverStart(hoveredRow);
			}

			return WidgetEventResult.CONTINUE_PROCESSING;
		});
	}

	public abstract void onRowClick(int row, CellData cell);

	public abstract void onRowHover(int row, CellData cell, boolean isHovered);

	private void onRowHoverStart(int row) {
		for(var cell : getRow(row)) {
			onRowHover(row, cell, true);
		}
	}

	private void onRowHoverEnd(int row) {
		for(var cell : getRow(row)) {
			onRowHover(row, cell, false);
		}
	}

	@Override
	public void clear() {
		super.clear();
		hoveredRow = -1;

	}
}
