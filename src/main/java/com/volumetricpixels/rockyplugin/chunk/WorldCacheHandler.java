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
package com.volumetricpixels.rockyplugin.chunk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.fest.reflect.core.Reflection;

import com.volumetricpixels.rockyapi.RockyManager;
import com.volumetricpixels.rockyapi.player.RockyPlayer;
import com.volumetricpixels.rockyplugin.chunk.WorldCache.ChunkCacheEntry;

import net.minecraft.server.v1_4_6.Packet51MapChunk;
import net.minecraft.server.v1_4_6.Packet56MapChunkBulk;

/**
 * Handler of the entire cache system
 */
public class WorldCacheHandler {
	/**
	 * List of every world in the cache
	 */
	protected static Map<String, WorldCache> cache = new HashMap<String, WorldCache>();

	/**
	 * The size per cache in the packet
	 */
	protected final static int CACHE_SIZE = 4096;

	/**
	 * Gets the cache for a world
	 * 
	 * @param name
	 *            the name of the world
	 * @return the structure of the cache
	 */
	public static WorldCache getWorld(String name) {
		if (!cache.containsKey(name)) {
			cache.put(name, new WorldCache());
		}
		return cache.get(name);
	}

	/**
	 * Handle 0x38 packet for sending bulk chunks to a player
	 * 
	 * @param player
	 *            the name of the player
	 * @param packet
	 *            the packet to handle
	 */
	public static void handlePacket(String player, Packet56MapChunkBulk packet) {
		int chunkLen = packet.a.length;
		int[] chunkXArray = Reflection.field("c").ofType(int[].class)
				.in(packet).get();
		int[] chunkZArray = Reflection.field("d").ofType(int[].class)
				.in(packet).get();
		int[] chunkBitArray = Reflection.field("a").ofType(int[].class)
				.in(packet).get();

		byte[][] chunkBuffer = Reflection.field("inflatedBuffers")
				.ofType(byte[][].class).in(packet).get();

		for (int i = 0; i < chunkLen; i++) {
			int chunkX = chunkXArray[i];
			int chunkZ = chunkZArray[i];

			byte[] newByteData = handleCompression(player, chunkBitArray[i],
					chunkX, chunkZ, chunkBuffer[i]);
			chunkBuffer[i] = newByteData;
		}

		Reflection.field("inflatedBuffers").ofType(byte[][].class).in(packet)
				.set(chunkBuffer);
	}

	/**
	 * Handle 0x33 packet for sending a single chunk to a player
	 * 
	 * @param player
	 *            the name of the player
	 * @param packet
	 *            the packet to handle
	 */
	public static void handlePacket(String player, Packet51MapChunk packet) {
		int chunkX = packet.a;
		int chunkZ = packet.b;

		byte[] oldByteData = Reflection.field("inflatedBuffer")
				.ofType(byte[].class).in(packet).get();
		byte[] newByteData = handleCompression(player, packet.c, chunkX,
				chunkZ, oldByteData);

		Reflection.field("inflatedBuffer").ofType(byte[].class).in(packet)
				.set(newByteData);
	}

	/**
	 * 
	 * @param buffer
	 * @throws IOException
	 */
	public static byte[] handleCompression(String playerName, int bitMask,
			double x, double z, byte[] buffer) {
		RockyPlayer player = RockyManager.getPlayer(Bukkit
				.getPlayer(playerName));
		WorldCache world = getWorld(player.getWorld().getName());

		ChunkCacheEntry chunk = world.getChunk(x, z);
		ChunkCacheEntry playerChunk = world.getPlayerChunk(playerName, x, z);

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		// Loop though the whole 16x256x16 by doing 16x16x16
		byte[] chunkBuffer = new byte[CACHE_SIZE];
		for (int i = 0; i < 16; i++) {
			// If the bitmask indicates this chunk is sent, otherwise is
			// only air
			if ((bitMask & 1 << i) > 0) {
				// Gets the buffer of the current section
				System.arraycopy(buffer, CACHE_SIZE * (i + 1), chunkBuffer,
						0x0000, CACHE_SIZE);

				// Calculate the chunk buffer
				chunk.entry[i] = WorldCache.calculateHash(chunkBuffer);

				// Check if the player has the same cache
				if (chunk.entry[i] == playerChunk.entry[i]) {
					out.write(0x00000000);
				} else {
					try {
						out.write(chunkBuffer);
					} catch (IOException ex) {
					}
				}
			}
			playerChunk.entry[i] = chunk.entry[i];
		}
		return out.toByteArray();
	}

}
