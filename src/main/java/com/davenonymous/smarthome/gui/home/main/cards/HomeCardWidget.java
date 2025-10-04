package com.davenonymous.smarthome.gui.home.main.cards;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.data.HomeCard;
import com.davenonymous.smarthome.lib.gui.GUIHelper;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetSprite;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.FlexSizer;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetHBox;
import com.davenonymous.smarthome.setup.content.ModFonts;
import com.mojang.blaze3d.platform.Window;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

public class HomeCardWidget extends WidgetPanel {
	HomeCard homeCard;
	WidgetPanel contentArea;
	WidgetSprite icon;
	WidgetTextBox label;

	WidgetHBox topBar;

	int padding = 8;
	boolean editMode = false;

	public HomeCardWidget(HomeCard homeCard) {
		super();
		this.homeCard = homeCard;
		this.setSize(homeCard.width(), homeCard.height());

		topBar = new WidgetHBox();
		topBar.setSize(homeCard.width(), 28);
		topBar.setPaddingHorizontal(padding);
		topBar.setPaddingVertical(4);

		icon = new WidgetSprite(homeCard.icon());
		icon.setPosition(padding, padding);
		topBar.addContentBox(icon, FlexSizer.FlexAlign.CENTER);

		label = new WidgetTextBox(homeCard.label(), 0xFFFFFFFF);
		label.setPosition(padding + icon.width + padding, padding);
		label.setFont(ModFonts.SAMSUNG);
		label.setWordWrap(true);
		label.autoWidth(this.width - icon.width - padding * 4);
		label.autoHeight();
		topBar.addContentBox(label, FlexSizer.FlexAlign.CENTER);

		this.add(topBar);

		contentArea = new WidgetPanel();
		contentArea.setPosition(padding, padding + topBar.height + padding);
		this.add(contentArea);

		updateCard();
	}

	public HomeCardWidget setEditing(boolean editMode) {
		this.editMode = editMode;
		return this;
	}

	public HomeCardWidget updateCard() {
		if(homeCard == null) {
			icon.setSprite(null);
			label.setText("");
			contentArea.clear();
		} else {
			icon.setSprite(homeCard.icon());
			label.setText(homeCard.label());
			contentArea.clear();

			for(var element : homeCard.elements().entrySet()) {
				var position = element.getKey();
				var widget = element.getValue().createWidget();
				if(widget != null) {
					widget.setPosition((int)position.x, (int)position.y);
					contentArea.add(widget);
				}
			}
		}
		updateWidgetSizes();
		icon.setSize(12, 12);
		return this;
	}

	@Override
	public void updateWidgetSizes() {
		super.updateWidgetSizes();

		contentArea.adjustSizeToContent(false);
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
