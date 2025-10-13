package com.davenonymous.smarthome.gui.projector;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.content.blocks.base.HomeBlockEntity;
import com.davenonymous.smarthome.content.blocks.projector.ProjectorContainer;
import com.davenonymous.smarthome.data.HomeCard;
import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.gui.general.VerticalSelectorWidget;
import com.davenonymous.smarthome.lib.gui.ColorHelper;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.event.ValueChangedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.networking.actions.SetSelectedHomePayload;
import com.davenonymous.smarthome.networking.actions.SetSelectedProjectorCardPayload;
import com.davenonymous.smarthome.setup.content.ModFonts;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.neoforge.network.PacketDistributor;

public class ProjectorSettingsPanel extends WidgetPanel {

	@I18DataGen(lang = "en_us", string = "Home:")
	@I18DataGen(lang = "de_de", string = "Home:")
	public static final I18String PROJECTOR_LABEL_HOME = I18String.gui("projector.home", "label");

	@I18DataGen(lang = "en_us", string = "Card:")
	@I18DataGen(lang = "de_de", string = "Karte:")
	public static final I18String PROJECTOR_LABEL_CARD = I18String.gui("projector.card", "label");

	@I18DataGen(lang = "en_us", string = "No cards available")
	@I18DataGen(lang = "de_de", string = "Keine Karten vorhanden")
	public static final I18String PROJECTOR_NO_CARDS = I18String.gui("projector.card", "empty");

	public ProjectorSettingsPanel(ProjectorContainer menu) {
		super();

		var homeLabel = new WidgetTextBox(PROJECTOR_LABEL_HOME.get());
		homeLabel.setFont(ModFonts.NOKIA);
		homeLabel.setTextColor(ChatFormatting.WHITE.getColor());
		homeLabel.autoWidth();
		homeLabel.autoHeight();
		homeLabel.setPosition(8, 8);
		this.add(homeLabel);

		var homeSelection = new VerticalSelectorWidget<>(menu.ownedHomes(), menu.selectedHome(), home -> {
			var textBox = new WidgetTextBox(home.name(), ChatFormatting.GRAY.getColor(), ColorHelper.COLOR_ORANGE);
			textBox.autoWidth();
			textBox.autoHeight();
			return textBox;
		});
		homeSelection.addListener(ValueChangedEvent.class, (event, widget) -> {
			menu.setSelectedHome((HomeCore) event.newValue);
			this.fireEvent(event);
			PacketDistributor.sendToServer(new SetSelectedHomePayload(menu.getBlockEntity().getBlockPos(), ((HomeCore) event.newValue).id()));
			return WidgetEventResult.HANDLED;
		});

		var cardLabel = new WidgetTextBox(PROJECTOR_LABEL_CARD.get());
		cardLabel.setFont(ModFonts.NOKIA);
		cardLabel.setTextColor(ChatFormatting.WHITE.getColor());
		cardLabel.autoWidth();
		cardLabel.autoHeight();
		cardLabel.setPosition(8, 22);
		this.add(cardLabel);

		Widget cardSelection;
		if(!menu.selectedHome().cards().isEmpty()) {
			cardSelection = new VerticalSelectorWidget<>(menu.selectedHome().cards(), menu.selectedCard(), card -> {
				var textBox = new WidgetTextBox(card.label(), ChatFormatting.GRAY.getColor(), ColorHelper.COLOR_ORANGE);
				textBox.autoWidth();
				textBox.autoHeight();
				return textBox;
			});
			cardSelection.addListener(ValueChangedEvent.class, (event, widget) -> {
				menu.setSelectedCard((HomeCard) event.newValue);
				this.fireEvent(event);
				PacketDistributor.sendToServer(new SetSelectedProjectorCardPayload(menu.getBlockEntity().getBlockPos(), ((HomeCard) event.newValue).id()));
				return WidgetEventResult.HANDLED;
			});
		} else {
			var noCardsText = new WidgetTextBox(PROJECTOR_NO_CARDS.get(), ColorHelper.COLOR_ERRORED.getRGB() | 0xFF000000);
			noCardsText.autoWidth();
			noCardsText.autoHeight();
			cardSelection = noCardsText;
			PacketDistributor.sendToServer(new SetSelectedProjectorCardPayload(menu.getBlockEntity().getBlockPos(), HomeBlockEntity.emptyUUID));
		}

		this.add(cardSelection);

		homeLabel.setWidth(Math.max(cardLabel.width(), homeLabel.width()));
		cardLabel.setWidth(Math.max(cardLabel.width(), homeLabel.width()));

		cardSelection.setPosition(8 + cardLabel.width() + 4, 22);
		homeSelection.setPosition(8 + homeLabel.width() + 4, 8);

		this.add(homeSelection);

		this.adjustSizeToContent(false);
		this.setWidth(this.width() + 8);
		this.setHeight(this.height() + 8);
	}

	@Override
	public void draw(GuiGraphics guiGraphics, Window window) {
		guiGraphics.blitSprite(SmartHome.sprite(GuiTheme.SpriteComponent.WINDOW_PUSHED_BACKGROUND), 0, 0, this.width, this.height);
		guiGraphics.fill(3, 3, width()-3, height()-3, 0x88000000);

		super.draw(guiGraphics, window);
	}
}
