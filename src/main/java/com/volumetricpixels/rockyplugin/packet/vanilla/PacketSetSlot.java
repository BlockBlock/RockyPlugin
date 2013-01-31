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
package com.volumetricpixels.rockyplugin.packet.vanilla;

import net.minecraft.server.v1_4_6.ItemStack;
import net.minecraft.server.v1_4_6.Packet103SetSlot;

/**
 * Encapsulate a {@see RockyPacketVanilla} that implements {@see
 * Packet103SetSlot}
 */
public class PacketSetSlot extends RockyPacketVanilla<Packet103SetSlot> {

	/**
	 * Gets the window which is being updated. 0 for player inventory. Note that
	 * all known window types include the player inventory. This packet will
	 * only be sent for the currently opened window while the player is
	 * performing actions, even if it affects the player inventory. After the
	 * window is closed, a number of these packets are sent to update the
	 * player's inventory window (0).
	 */
	public int getWindowId() {
		return packet.a;
	}

	/**
	 * Gets the slot that should be updated
	 */
	public int getSlot() {
		return packet.b;
	}

	/**
	 * Gets the item we're updating
	 */
	public ItemStack getItemStack() {
		return packet.c;
	}
}
