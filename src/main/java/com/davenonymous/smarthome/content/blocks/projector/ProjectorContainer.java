package com.davenonymous.smarthome.content.blocks.projector;

import com.davenonymous.smarthome.data.HomeCard;
import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.lib.gui.WidgetBlockEntityContainer;
import com.davenonymous.smarthome.networking.OpenProjectorScreenPayload;
import com.davenonymous.smarthome.setup.content.ModContainers;
import com.davenonymous.smarthome.watcher.WorldWatcherUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class ProjectorContainer extends WidgetBlockEntityContainer<ProjectorBlockEntity> {
	public static int WIDTH = 176;
	public static int HEIGHT = 76;

	private List<HomeCore> ownedHomes;
	private HomeCore selectedHome;
	private HomeCard selectedCard;
	private UUID selectedHomeId;
	private UUID selectedCardId;

	public ProjectorContainer(int id, BlockPos pos, Inventory inv, @NotNull Player player) {
		super(ModContainers.PROJECTOR_CONTAINER.get(), id, pos, inv, player);
	}

	public ProjectorContainer(int id, Inventory playerInv, RegistryFriendlyByteBuf buf) {
		super(ModContainers.PROJECTOR_CONTAINER.get(), id, buf.readBlockPos(), playerInv, playerInv.player);
		HEIGHT = 76;
		var data = OpenProjectorScreenPayload.CODEC.decode(buf);
		ownedHomes = data.homes();
		selectedHomeId = data.homeId();
		selectedCardId = data.cardId();
		if(!ownedHomes.isEmpty() && selectedHomeId != null) {
			for(HomeCore home : ownedHomes) {
				if(home.id().equals(selectedHomeId)) {
					selectedHome = home;
					selectedCard = home.getCard(data.cardId()).orElse(null);
					break;
				}
			}
		}
	}

	public List<HomeCore> ownedHomes() {
		return ownedHomes;
	}

	public UUID selectedCardId() {
		return selectedCardId;
	}

	public UUID selectedHomeId() {
		return selectedHomeId;
	}

	public HomeCore selectedHome() {
		return selectedHome;
	}

	public HomeCard selectedCard() {
		return selectedCard;
	}

	public ProjectorContainer setSelectedHome(HomeCore selectedHome) {
		this.selectedHome = selectedHome;
		this.selectedHomeId = selectedHome != null ? selectedHome.id() : null;
		if(selectedHome != null && (this.selectedCard == null || !selectedHome.cards().contains(this.selectedCard))) {
			this.selectedCard = selectedHome.cards().isEmpty() ? null : selectedHome.cards().getFirst();
			this.selectedCardId = this.selectedCard != null ? this.selectedCard.id() : null;
		}

		return this;
	}

	public ProjectorContainer setSelectedCard(HomeCard selectedCard) {
		this.selectedCard = selectedCard;
		this.selectedCardId = selectedCard != null ? selectedCard.id() : null;
		return this;
	}
}
