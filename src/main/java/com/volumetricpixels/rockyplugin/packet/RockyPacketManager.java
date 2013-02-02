/*
 * This file is part of RockyPlugin.
 *
 * Copyright (c) 2011-2012, VolumetricPixels <http://www.volumetricpixels.com/>
 * RockyPlugin is licensed under the GNU Lesser General Public License.
 *
 * RockyPlugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RockyPlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.volumetricpixels.rockyplugin.packet;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.server.v1_4_6.Packet;

import com.volumetricpixels.rockyapi.RockyManager;
import com.volumetricpixels.rockyapi.packet.PacketListener;
import com.volumetricpixels.rockyapi.packet.PacketManager;
import com.volumetricpixels.rockyapi.packet.PacketVanilla;
import com.volumetricpixels.rockyapi.player.RockyPlayer;
import com.volumetricpixels.rockyplugin.packet.listener.PacketMapCacheListener;
import com.volumetricpixels.rockyplugin.packet.listener.PacketVanillaSupportListener;
import com.volumetricpixels.rockyplugin.packet.vanilla.PacketBulkChunkData;
import com.volumetricpixels.rockyplugin.packet.vanilla.PacketChunkData;
import com.volumetricpixels.rockyplugin.packet.vanilla.PacketEntityEquipment;
import com.volumetricpixels.rockyplugin.packet.vanilla.PacketNamedEntitySpawn;
import com.volumetricpixels.rockyplugin.packet.vanilla.PacketPluginMessage;
import com.volumetricpixels.rockyplugin.packet.vanilla.PacketSetCreativeSlot;
import com.volumetricpixels.rockyplugin.packet.vanilla.PacketSetSlot;
import com.volumetricpixels.rockyplugin.packet.vanilla.PacketWindowItems;
import com.volumetricpixels.rockyplugin.packet.vanilla.RockyPacketVanilla;

/**
 * 
 */
public class RockyPacketManager implements PacketManager {

	private Map<Integer, Class<? extends PacketVanilla>> corePacket = new HashMap<Integer, Class<? extends PacketVanilla>>();
	private Map<Integer, List<PacketListener>> listenerList = new HashMap<Integer, List<PacketListener>>();

	/**
	 * Default constructor for registering all minecract packets.
	 */
	public RockyPacketManager() {
		addVanillaPacket(0x5, PacketEntityEquipment.class);
		addVanillaPacket(0x14, PacketNamedEntitySpawn.class);
		addVanillaPacket(0x33, PacketChunkData.class);
		addVanillaPacket(0x36, PacketBulkChunkData.class);
		addVanillaPacket(0x67, PacketSetSlot.class);
		addVanillaPacket(0x68, PacketWindowItems.class);
		addVanillaPacket(0x6B, PacketSetCreativeSlot.class);
		addVanillaPacket(0xFA, PacketPluginMessage.class);
		
		// Default listeners for ChunkCache and VanillaSupport
		addListener(new PacketMapCacheListener(), 0x33, 0x36);
		addListener(new PacketVanillaSupportListener(), 0x5, 0x14, 0x67, 0x68,
				0x6B);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addVanillaPacket(int id, Class<? extends PacketVanilla> extended) {
		corePacket.put(id, extended);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PacketVanilla getInstance(Packet packet) {
		RockyPacketVanilla<Packet> vanilla = null;
		if (corePacket.containsKey(packet.k())) {
			try {
				Class<? extends PacketVanilla> clazz = corePacket.get(packet
						.k());
				Constructor<? extends PacketVanilla> constructor = clazz
						.getConstructor();
				vanilla = (RockyPacketVanilla<Packet>) constructor
						.newInstance();
				vanilla.setPacket(packet);
			} catch (InstantiationException e) {
				RockyManager.printConsole(
						"Error trying to get a vanilla packet instance: %s",
						e.getMessage());
			} catch (IllegalAccessException e) {
				RockyManager.printConsole(
						"Error trying to get a vanilla packet instance: %s",
						e.getMessage());
			} catch (IllegalArgumentException e) {
				RockyManager.printConsole(
						"Error trying to get a vanilla packet instance: %s",
						e.getMessage());
			} catch (InvocationTargetException e) {
				RockyManager.printConsole(
						"Error trying to get a vanilla packet instance: %s",
						e.getMessage());
			} catch (NoSuchMethodException e) {
				RockyManager.printConsole(
						"Error trying to get a vanilla packet instance: %s",
						e.getMessage());
			} catch (SecurityException e) {
				RockyManager.printConsole(
						"Error trying to get a vanilla packet instance: %s",
						e.getMessage());
			}

		}
		return vanilla;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addListener(PacketListener listener, int... packetIds) {
		for (int packetId : packetIds) {
			List<PacketListener> listListener = listenerList.get(packetId);
			if (listListener == null) {
				listListener = new ArrayList<PacketListener>();
				listenerList.put(packetId, listListener);
			}
			listListener.add(listener);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeListener(int packetId, PacketListener listener) {
		List<PacketListener> listListener = listenerList.get(packetId);
		if (listListener == null) {
			return false;
		}
		return listenerList.remove(listener) != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearAllListeners() {
		for (List<PacketListener> listener : listenerList.values()) {
			if (listener != null) {
				listener.clear();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAllowedToSend(RockyPlayer player, Packet packet) {
		List<PacketListener> listenerReference = listenerList.get(packet.k());
		if (listenerReference != null) {
			PacketVanilla wrapper = getInstance(packet);
			for (PacketListener listener : listenerReference) {
				if (!listener.checkPacket(player, wrapper)) {
					return false;
				}
			}
		}
		return true;
	}

}
