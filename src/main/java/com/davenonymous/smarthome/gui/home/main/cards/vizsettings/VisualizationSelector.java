package com.davenonymous.smarthome.gui.home.main.cards.vizsettings;

import com.davenonymous.smarthome.api.visualization.IVisualization;
import com.davenonymous.smarthome.gui.general.VerticalSelectorWidget;
import com.davenonymous.smarthome.gui.home.main.cards.CardEditorWidget;
import com.davenonymous.smarthome.lib.gui.ColorHelper;
import com.davenonymous.smarthome.lib.gui.ContentAlignment;
import com.davenonymous.smarthome.lib.gui.event.MouseClickEvent;
import com.davenonymous.smarthome.lib.gui.event.ValueChangedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.tooltip.WrappedStringTooltipComponent;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.setup.dynamic.ModVisualizations;
import net.minecraft.client.resources.language.I18n;

import java.util.Comparator;

public class VisualizationSelector extends WidgetPanel {
	IVisualization<?> selectedVisualization;

	Widget[] vizChoices;
	WidgetTextBox vizLabel;
	VerticalSelectorWidget selector;

	public VisualizationSelector(IVisualization<?> selectedVisualization) {
		super();
		this.selectedVisualization = selectedVisualization;

		updateVisualizationChoices();

		this.vizLabel = new WidgetTextBox(selectedVisualization.getDisplayName().get(), 0xFFFFFFFF);
		this.vizLabel.autoWidth();
		this.vizLabel.autoHeight();
		this.vizLabel.addListener(
			MouseClickEvent.class, (event, widget) -> {
				CardEditorWidget parent = this.getParentByType(CardEditorWidget.class);
				if(parent == null) {
					return WidgetEventResult.CONTINUE_PROCESSING;
				}

				selector = VerticalSelectorWidget.openAt(parent.getMouseX(), parent.getMouseY(), ContentAlignment.TOP_LEFT, vizChoices);
				selector.zLevel += 20;
				parent.add(selector);
				return WidgetEventResult.HANDLED;
			});
		this.add(this.vizLabel);

		this.setSize(this.vizLabel.width()+2, this.vizLabel.height()+2);
	}

	public IVisualization<?> selectedVisualization() {
		return selectedVisualization;
	}

	private void updateVisualizationChoices() {

		var availableVizs = ModVisualizations.getAll();
		this.vizChoices = new Widget[availableVizs.keySet().size()];
		var vizList = availableVizs.keySet().stream().sorted(Comparator.comparing(id -> I18n.get(availableVizs.get(id).getDisplayName().get()), Comparator.naturalOrder())).toList();
		int iVizIndex = 0;
		for(var vizId : vizList) {
			var viz = availableVizs.get(vizId);

			var vizWidget = new WidgetTextBox(viz.getDisplayName().get(), 0xFFFFFFFF);
			if(this.selectedVisualization.getType().equals(vizId)) {
				vizWidget.setTextColor(ColorHelper.COLOR_ORANGE);
			}

			vizWidget.setTooltipElements(WrappedStringTooltipComponent.orange(viz.getDescription().get()));

			vizWidget.addListener(MouseClickEvent.class, (event, widget) -> {
				var oldSensor = this.selectedVisualization;
				this.selectedVisualization = viz;
				this.vizLabel.setText(I18n.get(this.selectedVisualization.getDisplayName().get()));
				this.vizLabel.autoWidth();
				this.vizLabel.autoHeight();
				CardEditorWidget parent = this.getParentByType(CardEditorWidget.class);
				if(parent != null) {
					parent.remove(selector);
					selector = null;
					this.fireEvent(new ValueChangedEvent<>(oldSensor, this.selectedVisualization));
				}
				this.updateVisualizationChoices();
				return WidgetEventResult.HANDLED;
			});
			vizWidget.autoWidth();
			vizWidget.autoHeight();
			vizChoices[iVizIndex++] = vizWidget;
		}
	}
}
