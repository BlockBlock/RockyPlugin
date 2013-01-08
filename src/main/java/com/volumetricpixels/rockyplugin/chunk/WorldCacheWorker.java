/*
 * This file is part of+ RockyPlugin.
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
package com.volumetricpixels.rockyplugin.chunk;

import java.io.IOException;

import com.volumetricpixels.rockyapi.RockyManager;
import com.volumetricpixels.rockyplugin.packet.RockyPacketHandler;

import net.minecraft.server.v1_4_6.Packet;
import net.minecraft.server.v1_4_6.Packet51MapChunk;
import net.minecraft.server.v1_4_6.Packet56MapChunkBulk;

/**
 * Encapsulate a worker that does the chunk cache async, by pooling this
 * runnable into a pool thread
 */
public class WorldCacheWorker implements Runnable {

	private Packet packet;
	private RockyPacketHandler connection;

	/**
	 * Constructor of a cache worker
	 * 
	 * @param connection
	 *            the connection of the player
	 * @param packet
	 *            the chunk packet data
	 */
	public WorldCacheWorker(RockyPacketHandler connection, Packet packet) {
		this.packet = packet;
		this.connection = connection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		try {
			String player = connection.player.getName();
			if (packet instanceof Packet56MapChunkBulk) {
				WorldCacheHandler.handlePacket(player,
						(Packet56MapChunkBulk) packet);
			} else {
				WorldCacheHandler.handlePacket(player,
						(Packet51MapChunk) packet);
			}
			connection.queueOutputPacket(packet);
		} catch (IOException ex) {
			RockyManager.printConsole(ex.getLocalizedMessage());
		}
	}

}
