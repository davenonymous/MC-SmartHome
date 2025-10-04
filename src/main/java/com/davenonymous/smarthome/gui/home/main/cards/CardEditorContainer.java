package com.davenonymous.smarthome.gui.home.main.cards;

import com.davenonymous.smarthome.gui.FontTestWidget;
import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.lib.gui.event.MouseClickEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetVBox;
import com.davenonymous.smarthome.setup.content.ModFonts;
import net.minecraft.ChatFormatting;

public class CardEditorContainer extends WidgetPanel {
	private WidgetVBox existingCardButtons;


	public CardEditorContainer() {
		super();

		existingCardButtons = new WidgetVBox();
		existingCardButtons.setPosition(6, 6);
		existingCardButtons.setWidth(300);
		existingCardButtons.setHeight(200);
		this.add(existingCardButtons);

		FontTestWidget fontTest = new FontTestWidget();
		fontTest.setPosition(30, 30);
		this.add(fontTest);

		updateCardList();
		updateWidgetSizes();
	}

	public void updateCardList() {
		existingCardButtons.clear();

		if(HomeScreen.get() == null) {
			return;
		}

		var selectedHome = HomeScreen.get().selectedHome;
		if(selectedHome == null) {
			return;
		}

		for(var card : selectedHome.cards()) {
			var button = new WidgetTextBox(card.label(), ChatFormatting.WHITE.getColor());
			button.setFont(ModFonts.BASEL);
			button.autoWidth();
			button.autoHeight();
			existingCardButtons.addContentBox(button);
		}

		var button = new WidgetTextBox("+ Add New Card", ChatFormatting.WHITE.getColor());
		button.setFont(ModFonts.NOKIA);
		button.autoWidth();
		button.autoHeight();
		button.addListener(MouseClickEvent.class, (event, widget) -> {

			return WidgetEventResult.HANDLED;
		});
		existingCardButtons.addContentBox(button);
	}

	@Override
	public void updateWidgetSizes() {
		super.updateWidgetSizes();
		existingCardButtons.adjustSizeToContent();
		existingCardButtons.setHeight(Math.max(40, existingCardButtons.getTotalRealSize()));
		existingCardButtons.updateWidgetSizes();
	}
}
