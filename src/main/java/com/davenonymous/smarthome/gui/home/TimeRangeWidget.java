package com.davenonymous.smarthome.gui.home;

import com.davenonymous.smarthome.data.TimeRange;
import com.davenonymous.smarthome.data.TimeRangeEnum;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanelWithValue;

public class TimeRangeWidget extends WidgetPanelWithValue<TimeRange> {


	public TimeRangeWidget() {
		super(new TimeRange(TimeRangeEnum.LAST_6_HOURS));

	}
}
