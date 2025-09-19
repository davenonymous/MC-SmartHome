package com.davenonymous.smarthome.lib.gui.widgets.layout;

import com.davenonymous.smarthome.lib.gui.widgets.Widget;

public class Spacer extends Widget {
	public Spacer(int width, int height) {
		super();
		this.setSize(width, height);
		this.setVisible(true);
	}

	public static Spacer flex() {
		return new Spacer(1, 1);
	}
}
