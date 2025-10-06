package com.davenonymous.smarthome.gui.home.main.cards;

import com.davenonymous.smarthome.data.HomeCard;
import com.davenonymous.smarthome.gui.events.CardSelectedEvent;
import com.davenonymous.smarthome.lib.gui.event.GuiDataUpdatedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CardEditorContainer extends WidgetPanel {
	private CardListWidget existingCardButtons;
	private AddNewCardButtonWidget addNewCardButton;
	private int padding = 6;

	private Map<UUID, CardEditorWidget> cardEditors;
	private CardEditorWidget activeEditor = null;

	public CardEditorContainer() {
		super();

		cardEditors = new HashMap<>();
		existingCardButtons = new CardListWidget();
		existingCardButtons.setPosition(padding, padding);
		existingCardButtons.addListener(CardSelectedEvent.class, (event, widget) -> {
			var editor = cardEditors.computeIfAbsent(event.card().id(), id -> new CardEditorWidget(event.card().createWidget(true)));
			this.setActiveEditor(editor);
			return WidgetEventResult.HANDLED;
		});
		this.add(existingCardButtons);

		addNewCardButton = new AddNewCardButtonWidget();
		this.add(addNewCardButton);

		updateCardList();
		updateWidgetSizes();


		this.addListener(GuiDataUpdatedEvent.class, (event, widget) -> {
			updateCardList();
			updateWidgetSizes();
			return WidgetEventResult.HANDLED;
		});
	}

	private CardEditorContainer setActiveEditor(CardEditorWidget editor) {
		if(this.activeEditor != null) {
			this.activeEditor.setVisible(false);
		}

		this.activeEditor = editor;
		if(editor == null) {
			return this;
		}

		this.activeEditor.setVisible(true);
		if(!this.children().contains(this.activeEditor)) {
			this.add(this.activeEditor);
		}
		this.activeEditor.setPosition(existingCardButtons.width + padding * 2, padding);
		this.activeEditor.setWidth(this.width - existingCardButtons.width - padding * 3);
		this.activeEditor.setHeight(this.height - padding * 2);
		this.activeEditor.updateWidgetSizes();
		return this;
	}

	public HomeCard getSelectedCard() {
		return existingCardButtons.getSelectedCard();
	}

	public void updateCardList() {
		existingCardButtons.updateCardList();
	}

	@Override
	public void updateWidgetSizes() {
		super.updateWidgetSizes();
		existingCardButtons.setHeight(Math.max(40, existingCardButtons.getTotalRealSize()));
		existingCardButtons.setWidth(existingCardButtons.getOrthogonalSize());
		existingCardButtons.updateWidgetSizes();

		addNewCardButton.setPosition(padding, height - addNewCardButton.height - padding);
	}
}
