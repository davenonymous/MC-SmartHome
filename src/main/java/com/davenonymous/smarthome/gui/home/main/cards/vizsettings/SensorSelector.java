package com.davenonymous.smarthome.gui.home.main.cards.vizsettings;

import com.davenonymous.smarthome.content.sensor.sensortypes.HomeSensor;
import com.davenonymous.smarthome.gui.DashboardScreen;
import com.davenonymous.smarthome.gui.general.VerticalSelectorWidget;
import com.davenonymous.smarthome.gui.home.main.cards.CardEditorWidget;
import com.davenonymous.smarthome.lib.gui.ColorHelper;
import com.davenonymous.smarthome.lib.gui.ContentAlignment;
import com.davenonymous.smarthome.lib.gui.event.MouseClickEvent;
import com.davenonymous.smarthome.lib.gui.event.ValueChangedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.setup.dynamic.ModSensors;

public class SensorSelector extends WidgetPanel {
	HomeSensor<?, ?> selectedSensor;

	Widget[] sensorChoices;
	WidgetTextBox sensorLabel;
	VerticalSelectorWidget selector;

	public SensorSelector(HomeSensor<?, ?> selectedSensor) {
		super();
		this.selectedSensor = selectedSensor;

		updateSensorChoices();

		this.sensorLabel = new WidgetTextBox(selectedSensor.getDisplayName().get(), 0xFFFFFFFF, ColorHelper.COLOR_ORANGE);
		this.sensorLabel.autoWidth();
		this.sensorLabel.autoHeight();
		this.sensorLabel.addListener(
			MouseClickEvent.class, (event, widget) -> {
				CardEditorWidget parent = this.getParentByType(CardEditorWidget.class);
				if(parent == null) {
					return WidgetEventResult.CONTINUE_PROCESSING;
				}

				selector = VerticalSelectorWidget.openAt(parent.getMouseX(), parent.getMouseY(), ContentAlignment.TOP_LEFT, sensorChoices);
				selector.zLevel += 20;
				parent.add(selector);
				return WidgetEventResult.HANDLED;
			});
		this.add(this.sensorLabel);

		this.setSize(this.sensorLabel.width()+2, this.sensorLabel.height()+2);
	}

	public HomeSensor<?, ?> selectedSensor() {
		return selectedSensor;
	}

	private void updateSensorChoices() {
		var availableSensors = DashboardScreen.get().selectedHome.getAvailableSensors();
		this.sensorChoices = new Widget[availableSensors.size()];
		for(int iSensorIndex = 0; iSensorIndex < availableSensors.size(); iSensorIndex++) {
			var sensorId = availableSensors.get(iSensorIndex);
			var sensor = ModSensors.getById(sensorId);

			var sensorWidget = new WidgetTextBox(sensor.getDisplayName().get(), 0xFFFFFFFF);
			if(this.selectedSensor.id().equals(sensorId)) {
				sensorWidget.setTextColor(ColorHelper.COLOR_ORANGE);
			}

			sensorWidget.addListener(MouseClickEvent.class, (event, widget) -> {
				var oldSensor = this.selectedSensor;
				this.selectedSensor = sensor;
				this.sensorLabel.setText(this.selectedSensor.getDisplayName().get());
				this.sensorLabel.autoWidth();
				this.sensorLabel.autoHeight();
				CardEditorWidget parent = this.getParentByType(CardEditorWidget.class);
				if(parent != null) {
					parent.remove(selector);
					selector = null;
					this.fireEvent(new ValueChangedEvent<>(oldSensor, this.selectedSensor));
				}
				this.updateSensorChoices();
				return WidgetEventResult.HANDLED;
			});
			sensorWidget.autoWidth();
			sensorWidget.autoHeight();
			sensorChoices[iSensorIndex] = sensorWidget;
		}
	}
}
