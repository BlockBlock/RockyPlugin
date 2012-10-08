/*
 * This file is part of SpoutLegacy.
 *
 * Copyright (c) 2011-2012, VolumetricPixels <http://www.volumetricpixels.com/>
 * SpoutLegacy is licensed under the GNU Lesser General Public License.
 *
 * SpoutLegacy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutLegacy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * This file is part of SpoutPlugin.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutPlugin is licensed under the GNU Lesser General Public License.
 *
 * SpoutPlugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutPlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.spout.legacyapi.packet;

import net.minecraft.server.Packet;

import org.spout.legacyapi.player.SpoutPlayer;

/**
 * 
 */
public interface PacketManager {
	/**
	 * 
	 * @param id
	 * @param vanilla
	 * @param extended
	 */
	public void addVanillaPacket(int id, Class<? extends Packet> vanilla,
			Class<? extends PacketVanilla> extended);

	/**
	 * 
	 * @param player
	 * @param packet
	 * @return
	 */
	public boolean isAllowedToSend(SpoutPlayer player, int packet);

	/**
	 * Returns a MCPacket instance with the default constructor.
	 * <p/>
	 * An id of 256 will give an uncompressed Map Chunk packet
	 * 
	 * @param packetId
	 *            the id of the desired packet
	 * @return an empty MCPacket of type packetId
	 */
	public PacketVanilla getInstance(int packetId);

	/**
	 * adds a packet listener for uncompressed map chunk packets
	 * <p/>
	 * These listeners are NOT called from within the main thread.
	 * 
	 * @param listener
	 *            the listener instance
	 */
	public void addListenerUncompressedChunk(PacketListener listener);

	/**
	 * adds a packet listener for packets of the given id
	 * <p/>
	 * These listeners are called from the main server thread
	 * 
	 * @param packetId
	 *            the packet id
	 * @param listener
	 *            the listener instance
	 */
	public void addListener(int packetId, PacketListener listener);

	/**
	 * removes a packet listener for uncompressed map chunk packets
	 * 
	 * @param listener
	 *            the listener instance
	 * @return true if listener was removed
	 */
	public boolean removeListenerUncompressedChunk(PacketListener listener);

	/**
	 * removes a packet listener for packets of the given id
	 * 
	 * @param listener
	 *            the listener instance
	 * @return true if listener was removed
	 */
	public boolean removeListener(int packetId, PacketListener listener);

	/**
	 * removes all packet listeners
	 * 
	 * @param listener
	 *            the listener instance
	 * @return true if listener was removed
	 */
	public void clearAllListeners();
}