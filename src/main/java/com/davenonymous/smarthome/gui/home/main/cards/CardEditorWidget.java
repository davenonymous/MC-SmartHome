package com.davenonymous.smarthome.gui.home.main.cards;

import com.davenonymous.smarthome.data.HomeCard;
import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.gui.events.SpriteSelectedEvent;
import com.davenonymous.smarthome.gui.general.SpriteSelectorWidget;
import com.davenonymous.smarthome.lib.HackerNoon;
import com.davenonymous.smarthome.lib.gui.ContentAlignment;
import com.davenonymous.smarthome.lib.gui.event.*;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetSprite;
import com.davenonymous.smarthome.networking.actions.cards.SetCardSettingsPayload;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.network.PacketDistributor;

public class CardEditorWidget extends WidgetPanel {
	HomeCardWidget cardWidget = null;

	private ElementSettingsWidget elementSettingsWidget;
	private CardSettingsWidget cardSettingsWidget;

	private WidgetSprite scaleHandle;

	public CardEditorWidget() {
		this(null);
	}

	public CardEditorWidget(HomeCardWidget cardWidget) {
		elementSettingsWidget = new ElementSettingsWidget();
		elementSettingsWidget.setVisible(false);
		this.add(elementSettingsWidget);

		cardSettingsWidget = new CardSettingsWidget();
		cardSettingsWidget.setVisible(false);
		this.add(cardSettingsWidget);

		scaleHandle = new WidgetSprite(HackerNoon.Regular.expand);
		scaleHandle.setColor(0xFFAAAAAA, 0xFFFFFFFF);
		scaleHandle.setVisible(false);

		scaleHandle.addListener(MouseEnterEvent.class, (event, widget) -> {
			scaleHandle.setScale(1.5f);
			scaleHandle.setPosition(
				this.cardWidget.x() + this.cardWidget.width - scaleHandle.width / 2,
				this.cardWidget.y() + this.cardWidget.height - scaleHandle.height / 2
			);
			return WidgetEventResult.HANDLED;
		});

		scaleHandle.addListener(MouseExitEvent.class, (event, widget) -> {
			scaleHandle.setScale(1f);
			scaleHandle.setPosition(
				this.cardWidget.x() + this.cardWidget.width - scaleHandle.width / 2,
				this.cardWidget.y() + this.cardWidget.height - scaleHandle.height / 2
			);
			return WidgetEventResult.HANDLED;
		});

		scaleHandle.addListener(MouseReleasedEvent.class, (event, widget) -> {
			if(!scaleHandle.isHovered()) {
				return WidgetEventResult.CONTINUE_PROCESSING;
			}
			scaleHandle.setScale(1f);
			scaleHandle.setPosition(
				this.cardWidget.x() + this.cardWidget.width - scaleHandle.width / 2,
				this.cardWidget.y() + this.cardWidget.height - scaleHandle.height / 2
			);
			PacketDistributor.sendToServer(new SetCardSettingsPayload(
				HomeScreen.get().selectedHome.id(),
				card().id(),
				card().label(),
				card().icon(),
				new Vec2(this.cardWidget.width, this.cardWidget.height)
			));
			this.updateWidgetSizes();
			return WidgetEventResult.HANDLED;
		});

		scaleHandle.addListener(
			MouseDraggedEvent.class, (event, widget) -> {
				if(getGUI().isDragging() != null) {
					// If the GUI is already being dragged, ignore this event
					return WidgetEventResult.CONTINUE_PROCESSING;
				}

				if(this.cardWidget == null) {
					return WidgetEventResult.CONTINUE_PROCESSING;
				}

				int newScaleHandleX = Math.round(getMouseX() - scaleHandle.width / 2f);
				int newScaleHandleY = Math.round(getMouseY() - scaleHandle.height / 2f);
				var oldScaleX = scaleHandle.x();
				var oldScaleY = scaleHandle.y();
				scaleHandle.setPosition(newScaleHandleX, newScaleHandleY);

				var extraWidth = newScaleHandleX - oldScaleX;
				var extraHeight = newScaleHandleY - oldScaleY;
				this.cardWidget.setWidth(this.cardWidget.width + extraWidth);
				this.cardWidget.setHeight(this.cardWidget.height + extraHeight);

				return WidgetEventResult.CONTINUE_PROCESSING;
			});

		this.add(scaleHandle);

		this.setCardWidget(cardWidget);

		this.addListener(GuiDataUpdatedEvent.class, (event, widget) -> {
			var optCurrentCard = HomeScreen.get().selectedHome.getCard(this.cardWidget.homeCard.id());
			if(optCurrentCard.isEmpty()) {
				setCardWidget(null);
				return WidgetEventResult.CONTINUE_PROCESSING;
			}

			setCardWidget(optCurrentCard.get().createWidget(true));
			return WidgetEventResult.CONTINUE_PROCESSING;
		});
	}

	public HomeCard card() {
		if(cardWidget != null) {
			return cardWidget.homeCard;
		}
		return null;
	}

	public CardEditorWidget setCardWidget(HomeCardWidget newCardWidget) {
		if(newCardWidget == null) {
			if(this.cardWidget != null) {
				this.cardWidget.setVisible(false);
			}

			elementSettingsWidget.setVisible(false);
			cardSettingsWidget.setVisible(false);
			scaleHandle.setVisible(false);
			updateWidgetSizes();
			return this;
		}

		if(this.cardWidget == null) {
			this.cardWidget = newCardWidget;
			this.cardWidget.cardRenameInput.addListener(ValueChangedEvent.class, (event, widget) -> {
				if(this.cardWidget == null) {
					return WidgetEventResult.CONTINUE_PROCESSING;
				}

				PacketDistributor.sendToServer(new SetCardSettingsPayload(
					HomeScreen.get().selectedHome.id(),
					card().id(),
					this.cardWidget.cardRenameInput.getValue(),
					card().icon(),
					card().size()
				));
				return WidgetEventResult.HANDLED;
			});

			this.cardWidget.icon.addListener(MouseClickEvent.class, (event, widget) -> {
				if(!widget.isHovered()) {
					return WidgetEventResult.CONTINUE_PROCESSING;
				}

				var selector = SpriteSelectorWidget.openAt(getMouseX(), getMouseY(), ContentAlignment.TOP_LEFT);
				selector.addListener(MouseExitEvent.class, (event2, widget2) -> {
					this.remove(selector);
					return WidgetEventResult.HANDLED;
				});
				selector.addListener(SpriteSelectedEvent.class, (event2, widget2) -> {
					this.remove(selector);
					if(this.cardWidget == null) {
						return WidgetEventResult.HANDLED;
					}

					this.cardWidget.icon.setSprite(event2.sprite());

					PacketDistributor.sendToServer(new SetCardSettingsPayload(
						HomeScreen.get().selectedHome.id(),
						card().id(),
						card().label(),
						event2.sprite(),
						card().size()
					));
					return WidgetEventResult.HANDLED;
				});

				this.add(selector);
				return WidgetEventResult.HANDLED;
			});

			this.add(newCardWidget);
		} else {
			this.cardWidget.updateCard(newCardWidget.homeCard);
		}

		cardWidget.setVisible(true);
		elementSettingsWidget.setVisible(true);
		cardSettingsWidget.setVisible(true);
		scaleHandle.setVisible(true);

		cardSettingsWidget.setCard(newCardWidget.homeCard);

		updateWidgetSizes();
		return this;
	}

	@Override
	public void updateWidgetSizes() {
		super.updateWidgetSizes();

		cardSettingsWidget.setPosition(this.width - cardSettingsWidget.width - 4, 4);
		elementSettingsWidget.setPosition(this.width - elementSettingsWidget.width - 4, this.height - elementSettingsWidget.height - 4);

		if(cardWidget != null) {
			//cardWidget.adjustSizeToContent(false);
			if(cardWidget.width > this.width) {
				cardWidget.setWidth(this.width - 16);
			}
			if(cardWidget.height > this.height) {
				cardWidget.setHeight(this.height - 16);
			}

			cardWidget.setWidth(Math.max(cardWidget.homeCard.width(), 50));
			cardWidget.setHeight(Math.max(cardWidget.homeCard.height(), 50));

			cardWidget.setPosition((this.width - cardWidget.width) / 2, (this.height - cardWidget.height) / 2);

			scaleHandle.setPosition(
				cardWidget.x() + cardWidget.width - scaleHandle.width / 2,
				cardWidget.y() + cardWidget.height - scaleHandle.height / 2
			);
		}
	}
}
