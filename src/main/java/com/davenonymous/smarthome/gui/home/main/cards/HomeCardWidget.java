package com.davenonymous.smarthome.gui.home.main.cards;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.content.cards.HomeCardElement;
import com.davenonymous.smarthome.content.cards.impl.VisualizationCardElement;
import com.davenonymous.smarthome.data.HomeCard;
import com.davenonymous.smarthome.gui.DashboardScreen;
import com.davenonymous.smarthome.gui.events.CardElementSelectedEvent;
import com.davenonymous.smarthome.gui.events.WidgetMovedEvent;
import com.davenonymous.smarthome.gui.events.WidgetScaledEvent;
import com.davenonymous.smarthome.gui.home.main.devices.NewDeviceEntryWidget;
import com.davenonymous.smarthome.lib.gui.ColorHelper;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.configurable.StringInputWidget;
import com.davenonymous.smarthome.lib.gui.event.*;
import com.davenonymous.smarthome.lib.gui.tooltip.WrappedStringTooltipComponent;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetSprite;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.networking.ClientCache;
import com.davenonymous.smarthome.networking.actions.requests.RequestVisualizationDataPayload;
import com.davenonymous.smarthome.setup.content.ModFonts;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.UUID;

public class HomeCardWidget extends WidgetPanel {
	HomeCard homeCard;
	WidgetPanel contentArea;
	WidgetSprite icon;

	WidgetTextBox label;
	StringInputWidget cardRenameInput;

	WidgetPanel topBar;

	int padding = 8;
	boolean editMode = false;

	private int relativeClickX = 0;
	private int relativeClickY = 0;
	private int oldSizeX = 0;
	private int oldSizeY = 0;

	public HomeCardWidget(HomeCard homeCard) {
		this(homeCard, false);
	}

	public HomeCardWidget(HomeCard homeCard, boolean editMode) {
		super();
		this.homeCard = homeCard;
		this.editMode = editMode;

		this.setSize(homeCard.width(), homeCard.height());

		topBar = new WidgetPanel();
		topBar.setSize(homeCard.width(), 28);

		icon = new WidgetSprite(homeCard.icon());
		icon.setPosition(padding, padding);
		if(editMode) {
			icon.setColor(0xFFFFFFFF, ColorHelper.COLOR_ORANGE);
		} else {
			icon.setColor(0xFFFFFFFF, 0xFFFFFFFF);
		}
		topBar.add(icon);


		if(editMode) {
			cardRenameInput = new StringInputWidget(homeCard.label(), ModFonts.SAFE_FONT_CHARS);
			cardRenameInput.setPosition(padding + icon.width, padding);
			cardRenameInput.setWidth(this.width - icon.width - padding * 4);
			cardRenameInput.setDrawBackground(false);
			cardRenameInput.nativeWidget().setTextColor(ChatFormatting.WHITE.getColor());
			cardRenameInput.setFont(ModFonts.SAMSUNG);
			cardRenameInput.setTooltipElements(WrappedStringTooltipComponent.orange(NewDeviceEntryWidget.CLICK_TO_RENAME.get()));
			topBar.add(cardRenameInput);
		} else {
			label = new WidgetTextBox(homeCard.label(), 0xFFFFFFFF);
			label.setPosition(padding + icon.width, padding);
			if(editMode) {
				label.setTextColor(0xFFAAAAAA);
			} else {
				label.setTextColor(0xFFFFFFFF);
			}
			label.setFont(ModFonts.SAMSUNG);
			label.setWordWrap(true);
			label.autoWidth(this.width - icon.width - padding * 4);
			label.autoHeight();
			topBar.add(label);
		}


		this.add(topBar);
		if(Minecraft.getInstance().screen == null) {
			topBar.zLevel -= 2;
			icon.zLevel -= 2;
		}

		contentArea = new WidgetPanel();
		contentArea.setPosition(0, topBar.height);
		this.add(contentArea);

		updateCard(homeCard);
	}

	public HomeCardWidget updateCard(HomeCard newCard) {
		if(newCard == null) {
			icon.setSprite(null);
			if(label != null) {
				label.setText("");
			}
			if(cardRenameInput != null) {
				cardRenameInput.setValue("");
			}
			contentArea.clear();
			this.homeCard = null;
		} else {
			this.homeCard = newCard;
			icon.setSprite(newCard.icon());
			if(label != null) {
				label.setText(newCard.label());
			}
			if(cardRenameInput != null) {
				cardRenameInput.setValue(newCard.label());
			}
			contentArea.clear();

			this.setSize(newCard.width(), newCard.height());

			for(var elementEntry : newCard.elements().entrySet()) {
				UUID elementId = elementEntry.getKey();
				Pair<Vec2, HomeCardElement<?>> element = elementEntry.getValue();

				var position = element.getFirst();
				var elementWidget = element.getSecond().createWidget();
				elementWidget.zLevel -= 4;
				if(elementWidget != null) {
					elementWidget.setPosition((int) position.x, (int) position.y);
					elementWidget.setUserData("element_id", elementId);

					if(element.getSecond() instanceof VisualizationCardElement vizCardElement) {
						for(UUID deviceId : vizCardElement.devices()) {
							var optDevice = ClientCache.getConfiguredDevice(deviceId);
							if(optDevice.isEmpty()) {
								continue;
							}

							if(Minecraft.getInstance().screen != null && DashboardScreen.get() != null) {
								var home = DashboardScreen.get().selectedHome;
								var payload = new RequestVisualizationDataPayload(optDevice.get().getSecond(), vizCardElement.sensorId(), vizCardElement.vizId(), vizCardElement.vizSettings(), home.timeRange());
								PacketDistributor.sendToServer(payload);
							}
						}
					}

					if(editMode) {
						String elementIdString = elementId.toString();

						elementWidget.addListener(
							MouseClickEvent.class, (event, widget) -> {
								if(!elementWidget.isHovered()) {
									return WidgetEventResult.CONTINUE_PROCESSING;
								}

								getGUI().setDragging(elementWidget);
								this.relativeClickX = getMouseX() - contentArea.x - elementWidget.x;
								this.relativeClickY = getMouseY() - contentArea.y - elementWidget.y;
								this.oldSizeX = elementWidget.width;
								this.oldSizeY = elementWidget.height;
								DashboardScreen.get().setScreenState("card_widget_selected_element", elementId);
								this.fireEvent(new CardElementSelectedEvent(elementWidget, elementId));
								return WidgetEventResult.CONTINUE_PROCESSING;
							}
						);

						elementWidget.addListener(
							MouseDraggedEvent.class, (event, widget) -> {
								if(getGUI().isDragging() != elementWidget) {
									// If the GUI is already being dragged, ignore this event
									return WidgetEventResult.CONTINUE_PROCESSING;
								}

								if(!elementWidget.isHovered()) {
									return WidgetEventResult.CONTINUE_PROCESSING;
								}

								if(event.button() == 0) {
									int newPosX = Math.round(getMouseX() - contentArea.x - relativeClickX);
									int newPosY = Math.round(getMouseY() - contentArea.y - relativeClickY);

									elementWidget.setPosition(
										newPosX,
										newPosY
									);
								} else if(event.button() == 1) {
									// scaling with right mouse button
									int relMouseX = Math.round(getMouseX() - contentArea.x - relativeClickX);
									int relMouseY = Math.round(getMouseY() - contentArea.y - relativeClickY);
									elementWidget.setSize(oldSizeX + relMouseX, oldSizeY + relMouseY);
									elementWidget.updateWidgetSizes();
								}
								return WidgetEventResult.CONTINUE_PROCESSING;
							}
						);

						elementWidget.addListener(
							MouseReleasedEvent.class, (event, widget) -> {
								if(!elementWidget.isHovered()) {
									return WidgetEventResult.CONTINUE_PROCESSING;
								}

								if(event.button == 0) {
									this.fireEvent(new WidgetMovedEvent(elementWidget, elementId));
								} else if(event.button == 1) {
									this.fireEvent(new WidgetScaledEvent(elementWidget, elementId));
								}
								getGUI().setDragging(null);
								return WidgetEventResult.HANDLED;
							}
						);
					}

					contentArea.add(elementWidget);
				}
			}
		}
		updateWidgetSizes();
		icon.setSize(12, 12);
		return this;
	}

	private void makeWidgetMovable(Widget movableWidget, UUID elementId) {

	}

	public WidgetSprite icon() {
		return icon;
	}

	@Override
	public void updateWidgetSizes() {
		super.updateWidgetSizes();

		contentArea.setWidth(this.width);
		contentArea.setHeight(this.height - topBar.height);

		this.adjustSizeToContent(false);
	}

	@Override
	public void draw(GuiGraphics guiGraphics, Window window) {
		if(Minecraft.getInstance().screen == null) {
			guiGraphics.pose().pushPose();
			guiGraphics.pose().translate(0, 0, 2);
			RenderSystem.setShaderColor(0.6f, 0.8f, 1, .8f);
		}
		RenderSystem.enableBlend();
		guiGraphics.blitSprite(SmartHome.sprite(GuiTheme.SpriteComponent.WINDOW_PUSHED_BACKGROUND), 0, 0, this.width, this.height);
		if(Minecraft.getInstance().screen == null) {
			guiGraphics.pose().translate(0, 0, -2);
		}
		guiGraphics.fill(3, 3, width() - 3, height() - 3, 0x88000000);


		if(Minecraft.getInstance().screen == null) {
			guiGraphics.pose().translate(0, 0, -2);
			RenderSystem.setShaderColor(1, 1, 1, 1f);
		} else {
			guiGraphics.pose().translate(0, 0, 4);
		}

		super.draw(guiGraphics, window);

		if(Minecraft.getInstance().screen == null) {
			guiGraphics.pose().popPose();
		}
		if(editMode) {
			var font = Minecraft.getInstance().font;
			var widthText = FormattedCharSequence.forward(this.width() + "px", Style.EMPTY.withFont(ModFonts.SAMSUNG.id()));
			var heightText = FormattedCharSequence.forward(this.height() + "px", Style.EMPTY.withFont(ModFonts.SAMSUNG.id()));

			guiGraphics.drawString(
				font,
				widthText,
				(this.width() - font.width(widthText)) / 2,
				-22,
				0xFFAAAAAA, false
			);

			var pose = guiGraphics.pose();
			pose.pushPose();
			pose.mulPose(Axis.ZP.rotationDegrees(-90));
			guiGraphics.drawString(
				font,
				heightText,
				-(this.height() + font.width(heightText)) / 2,
				-22,
				0xFFAAAAAA, false
			);
			pose.popPose();
		}
	}
}
