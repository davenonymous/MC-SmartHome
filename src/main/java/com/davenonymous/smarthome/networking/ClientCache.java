package com.davenonymous.smarthome.networking;

import com.davenonymous.smarthome.data.HomeCore;

import java.util.List;

public class ClientCache {
	public static final ClientCache INSTANCE = new ClientCache();

	List<HomeCore> ownedHomes;

	public static List<HomeCore> getOwnedHomes() {
		return INSTANCE.ownedHomes;
	}
}
