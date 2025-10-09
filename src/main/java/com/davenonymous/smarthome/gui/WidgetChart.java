package com.davenonymous.smarthome.gui;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.lib.gui.DynamicImageResources;
import com.davenonymous.smarthome.lib.gui.event.MouseMoveEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.event.WidgetSizeChangeEvent;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetImage;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.internal.chartpart.Chart;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class WidgetChart<T extends Chart<?, ?>> extends WidgetPanel {
	private T chart;
	private int texId;

	private WidgetImage image;
	private DynamicImageResources.DynTexture loadedImage;

	public WidgetChart(int texId, T chart) {
		this(texId);
		this.setChart(chart);
	}

	public WidgetChart(int texId) {
		super();
		this.texId = texId;

		this.addListener(
			WidgetSizeChangeEvent.class, (event, widget) -> {
				updateWidgetSizes();
				return WidgetEventResult.CONTINUE_PROCESSING;
			}
		);

		this.addListener(MouseMoveEvent.class, (event, widget) -> {
			// Forward mouse move events to the chart
			if(!this.isHovered() || this.chart == null || !this.areAllParentsVisible()) {
				return WidgetEventResult.CONTINUE_PROCESSING;
			}

			var chartX = chart.getChartXFromCoordinate((int) (getMouseX() * Minecraft.getInstance().getWindow().getGuiScale()));
			var chartY = chart.getChartYFromCoordinate((int) (getMouseY() * Minecraft.getInstance().getWindow().getGuiScale()));
			var instant = Instant.ofEpochMilli((long)chartX).atZone(ZoneId.systemDefault());

			// TODO: Show tooltip with time and value, maybe even search for nearby lines and show their values?

			return WidgetEventResult.CONTINUE_PROCESSING;
		});
	}

	public static <T extends Chart<?, ?>> BufferedImage getBufferedImage(T chart) {
		BufferedImage bufferedImage = new BufferedImage(chart.getWidth(), chart.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2D = bufferedImage.createGraphics();
		chart.paint(graphics2D, chart.getWidth(), chart.getHeight());
		return bufferedImage;
	}

	public static <T extends Chart<?, ?>> byte[] getBitmapBytes(T chart, BitmapEncoder.BitmapFormat bitmapFormat)
		throws IOException {

		BufferedImage bufferedImage = getBufferedImage(chart);

		byte[] imageInBytes;

		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			ImageIO.write(bufferedImage, bitmapFormat.toString().toLowerCase(), baos);
			baos.flush();
			imageInBytes = baos.toByteArray();
		}
		return imageInBytes;
	}

	@Override
	public void updateWidgetSizes() {
		super.updateWidgetSizes();
		if(this.image != null) {
			this.image.setSize(this.width, this.height);
		}
	}

	public void setChart(T chart) {
		this.chart = chart;


		CompletableFuture.supplyAsync(() -> {
			try {
				byte[] imageBytes = getBitmapBytes(this.chart, BitmapEncoder.BitmapFormat.PNG);
				return imageBytes;
			} catch (IOException e) {
				SmartHome.LOGGER.warn("Failed to read image from xchart: {}", e.toString());
			}
			return null;
		}, Util.backgroundExecutor()).thenAccept(bytes -> {
			Minecraft.getInstance().tell(() -> {
				Optional<DynamicImageResources.DynTexture> loadedImage = DynamicImageResources.getImage("xchart_" + texId, bytes);
				if(loadedImage.isEmpty()) {
					return;
				}

				this.loadedImage = loadedImage.get();
				if(image != null) {
					this.remove(this.image);
				}
				this.image = new WidgetImage(this.loadedImage);
				this.image.setPosition(0, 0);
				this.image.setVisible(true);
				this.image.setTextureSize(this.loadedImage.image().getWidth(), this.loadedImage.image().getHeight());
				this.image.setSize(this.width, this.height);
				this.add(this.image);
			});
		});

	}
}
