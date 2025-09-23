package com.davenonymous.smarthome.gui.home;

import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.event.GuiDataUpdatedEvent;
import com.davenonymous.smarthome.lib.gui.event.MouseClickEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetSprite;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetHBox;
import com.davenonymous.smarthome.networking.actions.SetSelectedHomePayload;
import net.minecraft.ChatFormatting;
import net.neoforged.neoforge.network.PacketDistributor;

public class HomeSelectWidget extends WidgetHBox {
	WidgetSprite prevButton;
	WidgetSprite nextButton;
	WidgetTextBox homeNameText;

	public HomeSelectWidget() {
		this.setSpacing(8);
		this.setPadding(0);
		this.setHeight(20);

		this.prevButton = new WidgetSprite(GuiTheme.SpriteComponent.WIDGET_PREV);
		this.prevButton.addListener(MouseClickEvent.class, (event, widget) -> {
			buttonHandler(true);
			return WidgetEventResult.HANDLED;
		});
		this.addContentBox(this.prevButton, FlexAlign.START);

		this.homeNameText = new WidgetTextBox("Home name");
		this.homeNameText.setWordWrap(true);
		//this.homeNameText.setFont(ModFonts.DOS);
		this.homeNameText.setTextColor(ChatFormatting.DARK_GRAY.getColor());
		this.addContentBox(this.homeNameText, FlexAlign.CENTER);

		this.nextButton = new WidgetSprite(GuiTheme.SpriteComponent.WIDGET_NEXT);
		this.nextButton.addListener(MouseClickEvent.class, (event, widget) -> {
			buttonHandler(false);
			return WidgetEventResult.HANDLED;
		});
		this.addContentBox(this.nextButton, FlexAlign.START);

		this.addListener(GuiDataUpdatedEvent.class, (event, widget) -> {
			updateWidgetContent();
			return WidgetEventResult.CONTINUE_PROCESSING;
		});
		updateWidgetContent();
		updateWidgetSizes();
	}

	public void buttonHandler(boolean prev) {
		var screen = HomeScreen.get();
		if(screen == null) {
			return;
		}

		var hasHomes = screen.ownedHomes != null && !screen.ownedHomes.isEmpty();
		if(!hasHomes) {
			return;
		}

		var homes = screen.ownedHomes;
		if(homes.size() < 2) {
			return;
		}

		var currentIndex = homes.indexOf(screen.selectedHome);
		if(currentIndex == -1) {
			return;
		}

		if(prev) {
			currentIndex--;
			if(currentIndex < 0) {
				currentIndex = homes.size() - 1;
			}
		} else {
			currentIndex++;
			if(currentIndex >= homes.size()) {
				currentIndex = 0;
			}
		}

		PacketDistributor.sendToServer(new SetSelectedHomePayload(screen.blockEntity.getBlockPos(), homes.get(currentIndex).id()));
		screen.selectedHome = homes.get(currentIndex);
		screen.getOrCreateGui().fireEvent(new GuiDataUpdatedEvent());
	}

	public void updateWidgetContent() {
		if(HomeScreen.get() == null) {
			return;
		}

		var hasHomes = HomeScreen.get().ownedHomes != null && !HomeScreen.get().ownedHomes.isEmpty();
		this.setVisible(hasHomes);

		if(hasHomes) {
			var home = HomeScreen.get().selectedHome;
			this.homeNameText.setText(home.name());

			var multipleHomes = HomeScreen.get().ownedHomes.size() > 1;
			this.prevButton.setVisible(multipleHomes);
			this.nextButton.setVisible(multipleHomes);
		}
	}

	public void updateWidgetSizes() {
		this.homeNameText.autoWidth(155);
		this.homeNameText.autoHeight();

		var maxHeight = Math.max(prevButton.height(), Math.max(homeNameText.height(), nextButton.height()));
		this.setHeight(maxHeight);

		var width = prevButton.width() + homeNameText.width() + nextButton.width() + this.spacing * 2;
		this.setWidth(width);
	}


}
