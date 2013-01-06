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

import org.fest.reflect.core.Reflection;

import net.minecraft.server.v1_4_6.Packet56MapChunkBulk;
import net.minecraft.server.v1_4_6.PlayerConnection;

/**
 * 
 */
public class CacheWorker implements Runnable {

	private Packet56MapChunkBulk packet;
	private PlayerConnection connection;

	/**
	 * 
	 * @param connection
	 * @param packet
	 */
	public CacheWorker(final PlayerConnection connection,
			final Packet56MapChunkBulk packet) {
		this.packet = packet;
		this.connection = connection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		int chunkLen = packet.a.length;
		int[] chunkXArray = Reflection.field("c").ofType(int[].class)
				.in(packet).get();
		int[] chunkZArray = Reflection.field("d").ofType(int[].class)
				.in(packet).get();
		int[] chunkBitArray = Reflection.field("a").ofType(int[].class)
				.in(packet).get();

		// Get the values of the packet of a single chunk
		for (int i = 0; i < chunkLen; i++) {
			int chunkX = chunkXArray[i];
			int chunkZ = chunkZArray[i];
			short yOldBitmap = (short) chunkBitArray[i];

			byte[] oldByteData = Reflection.field("buffer")
					.ofType(byte[].class).in(packet).get();
			byte[] newByteData = new byte[1];

			// Check for each individual Y structure bit.
			for (int j = 0; j < 16; j++) {
				// Get the current bit of the chunk
				boolean isHandled = ((yOldBitmap & (0x0001 << i)) == 0x1);
				if (isHandled) {
					System.out.println(chunkX + "/" + chunkZ + " with bit " + j
							+ " is handled by the user");
				}
				// TODO: Check the hash and build the outgoing packet
				// boolean isCached = ChunkCacheHandler.checkCache(chunkX, i,
				// chunkZ,
			}

			// TODO: WHAT?
		}
	}

}
