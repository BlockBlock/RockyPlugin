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
		byte[][] chunkBuffer = Reflection.field("inflatedBuffers")
				.ofType(byte[][].class).in(packet).get();

		for (int i = 0; i < chunkLen; i++) {
			int chunkX = chunkXArray[i];
			int chunkZ = chunkZArray[i];

			byte[] newByteData = handleCompression(player, chunkX, chunkZ,
					chunkBuffer[i]);
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
		byte[] newByteData = handleCompression(player, chunkX, chunkZ,
				oldByteData);

		Reflection.field("inflatedBuffer").ofType(byte[].class).in(packet)
				.set(newByteData);
	}

	/**
	 * 
	 * @param buffer
	 */
	public static byte[] handleCompression(String playerName, double x,
			double z, byte[] buffer) {
		RockyPlayer player = RockyManager.getPlayer(Bukkit
				.getPlayer(playerName));
		WorldCache world = getWorld(player.getWorld().getName());

		ChunkCacheEntry chunk = world.getChunk(x, z);
		ChunkCacheEntry playerChunk = world.getPlayerChunk(playerName, x, z);

		// TODO: Check section and check if both is good and send the data with
		// the hash to the client

		return null;
	}

}
