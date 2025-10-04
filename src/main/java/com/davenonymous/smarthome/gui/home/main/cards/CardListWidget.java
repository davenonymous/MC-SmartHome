package com.davenonymous.smarthome.gui.home.main.cards;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.data.HomeCard;
import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.gui.events.CardSelectedEvent;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.event.MouseClickEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetVBox;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.GuiGraphics;

import java.util.HashMap;
import java.util.Map;

public class CardListWidget extends WidgetVBox {
	private Map<HomeCard, CardSelectionWidget> cardButtonMap;
	private HomeCard selectedCard = null;


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

		if(HomeScreen.get() == null) {
			return;
		}

		var selectedHome = HomeScreen.get().selectedHome;
		if(selectedHome == null) {
			return;
		}

		for(var card : selectedHome.cards()) {
			var button = new CardSelectionWidget(card);
			button.addListener(
				MouseClickEvent.class, (event, widget) -> {
					if(selectedCard != null && cardButtonMap.containsKey(selectedCard)) {
						cardButtonMap.get(selectedCard).setActive(false);
					}
					button.setActive(true);
					selectedCard = card;
					this.fireEvent(new CardSelectedEvent(card));
					return WidgetEventResult.HANDLED;
				});
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
