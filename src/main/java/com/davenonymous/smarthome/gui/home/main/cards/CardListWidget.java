package com.davenonymous.smarthome.gui.home.main.cards;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.data.HomeCard;
import com.davenonymous.smarthome.gui.DashboardScreen;
import com.davenonymous.smarthome.gui.events.CardSelectedEvent;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.event.MouseClickEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetVBox;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class CardListWidget extends WidgetVBox {
	private Map<HomeCard, CardSelectionWidget> cardButtonMap;
	private HomeCard selectedCard = null;

	private static final String STATE_SELECTED_CARD = "card_list_widget.selected_card";

	public CardListWidget() {
		super();
		this.setPadding(8);
		cardButtonMap = new HashMap<>();

		this.setWidth(300);
		this.setHeight(200);
	}

	public HomeCard getSelectedCard() {
		return selectedCard;
	}

	public void updateCardList() {
		this.clear();
		cardButtonMap.clear();

		if(DashboardScreen.get() == null) {
			return;
		}

		var selectedHome = DashboardScreen.get().selectedHome;
		if(selectedHome == null) {
			return;
		}

		var cardsByName = selectedHome.cards().stream().sorted(Comparator.comparing(HomeCard::label, Comparator.naturalOrder())).toList();
		for(var card : cardsByName) {
			var button = new CardSelectionWidget(card);
			button.addListener(
				MouseClickEvent.class, (event, widget) -> {
					cardButtonMap.values().forEach(b -> b.setActive(false));
					button.setActive(true);
					selectedCard = card;
					DashboardScreen.get().setScreenState(STATE_SELECTED_CARD, card);
					this.fireEvent(new CardSelectedEvent(card));
					return WidgetEventResult.HANDLED;
				});
			HomeCard initialSelectedCard = DashboardScreen.get().getScreenState(STATE_SELECTED_CARD);
			if(DashboardScreen.get().getScreenState(STATE_SELECTED_CARD) != null && initialSelectedCard.id().equals(card.id())) {
				button.setActive(true);
			}
			this.addContentBox(button, FlexAlign.START);
			cardButtonMap.put(card, button);
		}
	}

	@Override
	public void draw(GuiGraphics guiGraphics, Window window) {
		guiGraphics.blitSprite(SmartHome.sprite(GuiTheme.SpriteComponent.WINDOW_PUSHED_BACKGROUND), 0, 0, this.width, this.height);
		guiGraphics.fill(3, 3, width()-3, height()-3, 0x88000000);

		super.draw(guiGraphics, window);
	}
}
