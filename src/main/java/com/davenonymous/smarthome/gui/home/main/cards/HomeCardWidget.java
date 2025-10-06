package com.davenonymous.smarthome.gui.home.main.cards;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.cards.HomeCardElement;
import com.davenonymous.smarthome.cards.impl.VisualizationCardElement;
import com.davenonymous.smarthome.data.HomeCard;
import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.gui.events.CardElementSelectedEvent;
import com.davenonymous.smarthome.gui.events.VisualizationDataUpdatedEvent;
import com.davenonymous.smarthome.gui.events.WidgetMovedEvent;
import com.davenonymous.smarthome.gui.home.main.devices.NewDeviceEntryWidget;
import com.davenonymous.smarthome.lib.gui.ColorHelper;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.configurable.StringInputWidget;
import com.davenonymous.smarthome.lib.gui.event.MouseClickEvent;
import com.davenonymous.smarthome.lib.gui.event.MouseDraggedEvent;
import com.davenonymous.smarthome.lib.gui.event.MouseReleasedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.tooltip.WrappedStringTooltipComponent;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetSprite;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.FlexSizer;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetHBox;
import com.davenonymous.smarthome.networking.actions.requests.RequestVisualizationDataPayload;
import com.davenonymous.smarthome.setup.content.ModFonts;
import com.mojang.blaze3d.platform.Window;
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

	WidgetHBox topBar;

	int padding = 8;
	boolean editMode = false;

	public HomeCardWidget(HomeCard homeCard) {
		this(homeCard, false);
	}

	public HomeCardWidget(HomeCard homeCard, boolean editMode) {
		super();
		this.homeCard = homeCard;
		this.editMode = editMode;

		this.setSize(homeCard.width(), homeCard.height());

		topBar = new WidgetHBox();
		topBar.setSize(homeCard.width(), 28);
		topBar.setPaddingHorizontal(padding);
		topBar.setPaddingVertical(4);

		icon = new WidgetSprite(homeCard.icon());
		icon.setPosition(padding, padding);
		if(editMode) {
			icon.setColor(0xFFFFFFFF, ColorHelper.COLOR_ORANGE);
		} else {
			icon.setColor(0xFFFFFFFF, 0xFFFFFFFF);
		}
		topBar.addContentBox(icon, FlexSizer.FlexAlign.CENTER);

		if(editMode) {
			cardRenameInput = new StringInputWidget(homeCard.label(), "[a-zA-Z0-9_ -!?+:/\\@#$%^&*()]*");
			cardRenameInput.setWidth(this.width - icon.width - padding * 4);
			cardRenameInput.setDrawBackground(false);
			cardRenameInput.nativeWidget().setTextColor(ChatFormatting.WHITE.getColor());
			cardRenameInput.setFont(ModFonts.SAMSUNG);
			cardRenameInput.setTooltipElements(WrappedStringTooltipComponent.orange(NewDeviceEntryWidget.CLICK_TO_RENAME.get()));
			topBar.addContentBox(cardRenameInput, FlexSizer.FlexAlign.CENTER);
		} else {
			label = new WidgetTextBox(homeCard.label(), 0xFFFFFFFF);
			label.setPosition(padding + icon.width + padding, padding);
			if(editMode) {
				label.setTextColor(0xFFAAAAAA);
			} else {
				label.setTextColor(0xFFFFFFFF);
			}
			label.setFont(ModFonts.SAMSUNG);
			label.setWordWrap(true);
			label.autoWidth(this.width - icon.width - padding * 4);
			label.autoHeight();
			topBar.addContentBox(label, FlexSizer.FlexAlign.CENTER);
		}


		this.add(topBar);

		contentArea = new WidgetPanel();
		contentArea.setPosition(padding, padding + topBar.height + padding);
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
				if(elementWidget != null) {
					elementWidget.setPosition((int)position.x, (int)position.y);

					if(element.getSecond() instanceof VisualizationCardElement vizCardElement) {
						for(UUID deviceId : vizCardElement.devices()) {
							var optDevice = HomeScreen.get().selectedHome.getDevice(deviceId);
							if(optDevice.isEmpty()) {
								continue;
							}

							var payload = new RequestVisualizationDataPayload(HomeScreen.get().selectedHome.id(), optDevice.get().getSecond(), vizCardElement.sensorId(), vizCardElement.vizId(), vizCardElement.vizSettings());
							PacketDistributor.sendToServer(payload);
						}
					}

					if(editMode) {
						String elementIdString = elementId.toString();

						elementWidget.addListener(MouseClickEvent.class, (event, widget) -> {
							if(!elementWidget.isHovered()) {
								return WidgetEventResult.CONTINUE_PROCESSING;
							}

							this.fireEvent(new CardElementSelectedEvent(elementWidget, elementId));
							return WidgetEventResult.CONTINUE_PROCESSING;
						});

						elementWidget.addListener(
							MouseDraggedEvent.class, (event, widget) -> {
								if(getGUI().isDragging() != null) {
									// If the GUI is already being dragged, ignore this event
									return WidgetEventResult.CONTINUE_PROCESSING;
								}

								int newScaleHandleX = Math.round(getMouseX() - contentArea.x - (widget.width / 2f));
								int newScaleHandleY = Math.round(getMouseY() - contentArea.y - (widget.height / 2f));

								elementWidget.setPosition(
									newScaleHandleX,
									newScaleHandleY
								);
								return WidgetEventResult.CONTINUE_PROCESSING;
							});

						elementWidget.addListener(
							MouseReleasedEvent.class, (event, widget) -> {
								if(!elementWidget.isHovered()) {
									return WidgetEventResult.CONTINUE_PROCESSING;
								}

								this.fireEvent(new WidgetMovedEvent(elementWidget, elementId));
								return WidgetEventResult.HANDLED;
							});
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

		contentArea.setWidth(this.width - padding * 2);
		contentArea.setHeight(this.height - padding * 3 - topBar.height);

		this.adjustSizeToContent(false);
	}

	@Override
	public void draw(GuiGraphics guiGraphics, Window window) {
		guiGraphics.blitSprite(SmartHome.sprite(GuiTheme.SpriteComponent.WINDOW_PUSHED_BACKGROUND), 0, 0, this.width, this.height);
		guiGraphics.fill(3, 3, width()-3, height()-3, 0x88000000);

		super.draw(guiGraphics, window);

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
