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
import java.util.zip.Deflater;

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
	public static void handlePacket(String player, Packet56MapChunkBulk packet)
			throws IOException {
		int chunkLen = packet.a.length;
		int[] chunkXArray = Reflection.field("c").ofType(int[].class)
				.in(packet).get();
		int[] chunkZArray = Reflection.field("d").ofType(int[].class)
				.in(packet).get();
		int[] chunkBitArray = Reflection.field("a").ofType(int[].class)
				.in(packet).get();
		int[] chunkExtraBitArray = Reflection.field("b").ofType(int[].class)
				.in(packet).get();
		boolean isSkyLight = Reflection.field("h").ofType(boolean.class)
				.in(packet).get();

		byte[][] chunkBuffer = Reflection.field("inflatedBuffers")
				.ofType(byte[][].class).in(packet).get();
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		for (int i = 0; i < chunkLen; i++) {
			int chunkX = chunkXArray[i];
			int chunkZ = chunkZArray[i];

			byte[] newByteData = handleCompression(player, chunkBitArray[i],
					chunkExtraBitArray[i], true, isSkyLight, chunkX, chunkZ,
					chunkBuffer[i]);
			buffer.write(newByteData);
		}
		Reflection.field("buildBuffer").ofType(byte[].class).in(packet)
				.set(buffer.toByteArray());
	}

	/**
	 * Handle 0x33 packet for sending a single chunk to a player
	 * 
	 * @param player
	 *            the name of the player
	 * @param packet
	 *            the packet to handle
	 */
	public static void handlePacket(String player, Packet51MapChunk packet)
			throws IOException {
		int chunkX = packet.a;
		int chunkZ = packet.b;

		byte[] oldByteData = Reflection.field("inflatedBuffer")
				.ofType(byte[].class).in(packet).get();
		byte[] newByteData = handleCompression(player, packet.c, packet.d,
				packet.e, true, chunkX, chunkZ, oldByteData);

		Deflater deflater = new Deflater(-1);
		deflater.setInput(newByteData, 0, newByteData.length);
		deflater.finish();
		byte[] buffer = new byte[newByteData.length];
		int size = deflater.deflate(buffer);
		deflater.end();

		Reflection.field("buffer").ofType(byte[].class).in(packet).set(buffer);
		Reflection.field("size").ofType(int.class).in(packet).set(size);
	}

	/**
	 * 
	 * @param buffer
	 * @throws IOException
	 */
	public static byte[] handleCompression(String playerName, int bitMask,
			int extraMask, boolean isContinuos, boolean handleLight, double x,
			double z, byte[] buffer) throws IOException {
		RockyPlayer player = RockyManager.getPlayer(Bukkit
				.getPlayer(playerName));
		WorldCache world = getWorld(player.getWorld().getName());

		ChunkCacheEntry chunk = world.getChunk(x, z);
		ChunkCacheEntry playerChunk = world.getPlayerChunk(playerName, x, z);

		// Each chunk sended is handled by:
		// - BlockType: Whole byte per block
		// - BlockMetaData: Half byte per block
		// - BlockLight: Half byte per block
		// - SkyLight: Half byte per block (Only of handleLight is TRUE)
		// - AddArray: Half byte per block (Only if extraMask has the bit)
		// - BiomeArray: Whole byte per XZ coordinate (Only if isContinous is
		// TRUE)
		int currentIndex = 0;
		int currentSize = 4096 + 2048 + 2048 + (handleLight ? 2048 : 0)
				+ (isContinuos ? 256 : 0);
		byte[] chunkBuffer = new byte[currentSize + 2048];
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		// Compute chunk number, the number of sections and the number
		// of extra data provided by the chunk
		for (int i = 0; i < 16; i++) {
			if ((bitMask & 1 << i) > 0) {
				boolean isExtraMask = ((extraMask & 1 << i) > 0);

				// Gets the block cache at the current location
				System.arraycopy(buffer, currentIndex, chunkBuffer, 0x0000,
						currentSize + (isExtraMask ? 2048 : 0));

				// Calculate the chunk buffer
				chunk.entry[i] = WorldCache.calculateHash(chunkBuffer);

				// Check if the player has the same cache
				if (chunk.entry[i] == playerChunk.entry[i]) {
					out.write(0x00000000);
				} else {
					out.write(chunkBuffer);
				}
				currentIndex += currentSize + (isExtraMask ? 2048 : 0);
			} else {
				chunk.entry[i] = 0x00000000;
			}
			playerChunk.entry[i] = chunk.entry[i];
		}
		return out.toByteArray();
	}

}
