package com.davenonymous.smarthome.gui.home.main.cards;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.cards.HomeCardElement;
import com.davenonymous.smarthome.data.HomeCard;
import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.gui.events.*;
import com.davenonymous.smarthome.gui.general.ScaleHandle;
import com.davenonymous.smarthome.gui.general.SpriteSelectorWidget;
import com.davenonymous.smarthome.lib.gui.ColorHelper;
import com.davenonymous.smarthome.lib.gui.ContentAlignment;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.event.*;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetSprite;
import com.davenonymous.smarthome.networking.actions.cards.AddCardElementPayload;
import com.davenonymous.smarthome.networking.actions.cards.SetCardElementPositionPayload;
import com.davenonymous.smarthome.networking.actions.cards.SetCardElementSettingsPayload;
import com.davenonymous.smarthome.networking.actions.cards.SetCardSettingsPayload;
import com.davenonymous.smarthome.setup.dynamic.ModCardElements;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.network.PacketDistributor;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.UUID;

public class CardEditorWidget extends WidgetPanel {
	HomeCardWidget cardWidget = null;

	private ElementSettingsWidget elementSettingsWidget;
	private CardElementsContainer cardElementsContainer;

	private WidgetSprite scaleHandle;

	Widget selectedElementWidget = null;
	UUID selectedElementId = null;

	public CardEditorWidget() {
		this(null);
	}

	public CardEditorWidget(HomeCardWidget cardWidget) {
		elementSettingsWidget = new ElementSettingsWidget();
		elementSettingsWidget.setVisible(false);
		elementSettingsWidget.addListener(ElementSettingsChangedEvent.class, (event, widget) -> {
			var elementId = event.id();
			var newData = event.newCardElement();
			PacketDistributor.sendToServer(new SetCardElementSettingsPayload(
				HomeScreen.get().selectedHome.id(),
				card().id(),
				elementId, newData
			));
			return WidgetEventResult.HANDLED;
		});
		this.add(elementSettingsWidget);

		cardElementsContainer = new CardElementsContainer();
		cardElementsContainer.setVisible(false);
		cardElementsContainer.addListener(AddCardElementEvent.class, (event, widget) -> {
			if(cardWidget == null) {
				return WidgetEventResult.CONTINUE_PROCESSING;
			}

			PacketDistributor.sendToServer(new AddCardElementPayload(
				HomeScreen.get().selectedHome.id(),
				card().id(),
				event.id()
			));

			return WidgetEventResult.HANDLED;
		});
		this.add(cardElementsContainer);

		this.setCardWidget(cardWidget);

		this.addListener(GuiDataUpdatedEvent.class, (event, widget) -> {
			if(!this.isVisible() || !this.areAllParentsVisible()) {
				return WidgetEventResult.CONTINUE_PROCESSING;
			}

			var optCurrentCard = HomeScreen.get().selectedHome.getCard(this.cardWidget.homeCard.id());
			if(optCurrentCard.isEmpty()) {
				setCardWidget(null);
				return WidgetEventResult.CONTINUE_PROCESSING;
			}

			setCardWidget(optCurrentCard.get().createWidget(true));
			return WidgetEventResult.CONTINUE_PROCESSING;
		});
	}

	private CardEditorWidget createScaleHandle() {
		if(scaleHandle != null || cardWidget == null) {
			return this;
		}

		scaleHandle = new ScaleHandle(cardWidget);
		scaleHandle.setColor(0xFFAAAAAA, 0xFFFFFFFF);
		scaleHandle.setVisible(false);

		scaleHandle.addListener(ScaleHandleResizedEvent.class, (event, widget) -> {
			PacketDistributor.sendToServer(new SetCardSettingsPayload(
				HomeScreen.get().selectedHome.id(),
				card().id(),
				card().label(),
				card().icon(),
				new Vec2(event.attachedTo().width, event.attachedTo().height)
			));

			return WidgetEventResult.HANDLED;
		});

		this.add(scaleHandle);
		return this;
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
			cardElementsContainer.setVisible(false);
			if(scaleHandle != null) {
				scaleHandle.setVisible(false);
			}
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
				selector.zLevel++;
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

			this.cardWidget.addListener(WidgetMovedEvent.class, (event, widget) -> {
				var elementId = event.elementId();
				var elementEntry = this.cardWidget.homeCard.elements().get(elementId);
				if(elementEntry == null) {
					return WidgetEventResult.CONTINUE_PROCESSING;
				}

				PacketDistributor.sendToServer(new SetCardElementPositionPayload(
					HomeScreen.get().selectedHome.id(),
					card().id(),
					elementId,
					new Vec2(event.movedWidget().x, event.movedWidget().y)
				));
				return WidgetEventResult.HANDLED;
			});

			this.cardWidget.addListener(CardElementSelectedEvent.class, (event, widget) -> {
				var elementId = event.elementId();
				var wigget = event.elementWidget();

				this.selectedElementWidget = wigget;
				this.selectedElementId = elementId;
				this.elementSettingsWidget.setElement(card().elements().get(elementId).getSecond());

				return WidgetEventResult.HANDLED;
			});

			this.add(newCardWidget);
			newCardWidget.zLevel++;
			this.createScaleHandle();
		} else {
			this.cardWidget.updateCard(newCardWidget.homeCard);
			this.elementSettingsWidget.rebuild(newCardWidget.homeCard);
		}

		cardWidget.setVisible(true);
		elementSettingsWidget.setVisible(true);
		cardElementsContainer.setVisible(true);
		scaleHandle.setVisible(true);

		updateWidgetSizes();
		return this;
	}

	@Override
	public void updateWidgetSizes() {
		super.updateWidgetSizes();

		cardElementsContainer.setPosition(this.width - cardElementsContainer.width - 4, 4);
		elementSettingsWidget.setPosition(this.width - elementSettingsWidget.width - 4, cardElementsContainer.y + cardElementsContainer.height + 4);

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

			cardWidget.setPosition((this.width - elementSettingsWidget.width - cardWidget.width) / 2, (this.height - cardWidget.height) / 2);
		}

		if(scaleHandle != null) {
			scaleHandle.updateWidgetSizes();
		}
	}

	@Override
	public void draw(GuiGraphics guiGraphics, Window window) {
		super.draw(guiGraphics, window);

		if(this.cardWidget == null) {
			return;
		}

		for(Widget element : cardWidget.contentArea.children()) {
			if(element instanceof ScaleHandle) {
				continue;
			}

			Optional<Integer> highlightColor = Optional.empty();
			if(element.isHovered()) {
				highlightColor = Optional.of(SmartHome.color(GuiTheme.ColorComponent.TEXT_ACTIVE));
			}

			// TODO: This doesn't work as we are replacing the complete card widget and the selected element Widget does not exist anymore.
			if(element == selectedElementWidget) {
				highlightColor = Optional.of(SmartHome.color(GuiTheme.ColorComponent.BUTTON_BG_ACTIVE_HOVER));
			}

			if(highlightColor.isEmpty()) {
				continue;
			}

			int color = highlightColor.get();
			int elementX = cardWidget.x + cardWidget.contentArea.x + element.x - 2;
			int elementY = cardWidget.y + cardWidget.contentArea.y + element.y - 2;

			int lineStartX = cardWidget.x + cardWidget.contentArea.x - 1;
			int lineStartY = cardWidget.y + cardWidget.contentArea.y - 1;


			var pose = guiGraphics.pose();
			pose.pushPose();
			pose.translate(0, 0, 10);
			guiGraphics.vLine( elementX, lineStartY, lineStartY + cardWidget.contentArea.height, color & 0x88FFFFFF);
			guiGraphics.vLine( elementX + element.width + 4, lineStartY, lineStartY + cardWidget.contentArea.height, color & 0x88FFFFFF);
			guiGraphics.hLine(lineStartX, lineStartX + cardWidget.contentArea.width, elementY, color & 0x88FFFFFF);
			guiGraphics.hLine(lineStartX, lineStartX + cardWidget.contentArea.width, elementY + element.height + 4, color & 0x88FFFFFF);
			guiGraphics.fill(elementX, elementY, elementX + element.width + 4, elementY + element.height + 4, color & 0x22FFFFFF);
			pose.popPose();
			break;
		}
	}
}
