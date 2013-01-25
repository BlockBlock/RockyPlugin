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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.zip.Deflater;

import net.minecraft.server.v1_4_6.Packet250CustomPayload;
import net.minecraft.server.v1_4_6.Packet51MapChunk;
import net.minecraft.server.v1_4_6.Packet56MapChunkBulk;

import org.fest.reflect.core.Reflection;

/**
 * Handler of the entire cache system
 */
public class ChunkCacheHandler {
	/**
	 * List of every world in the cache
	 */
	protected static ChunkCache cache = new ChunkCache();

	/**
	 * Handle packet that the player send us for nearby hashes
	 * 
	 * @param player
	 *            the name of the packet
	 * @param packet
	 *            the packet to handle
	 */
	public static void handlePacket(String player, Packet250CustomPayload packet) {
		Set<Long> playerCache = cache.getPlayerCache(player);

		DataInputStream in = new DataInputStream(new ByteArrayInputStream(
				packet.data));
		try {
			int hashLength = in.readInt();
			for (int i = 0; i < hashLength; i++) {
				playerCache.add(in.readLong());
			}
		} catch (IOException ex) {
		} finally {
			try {
				in.close();
			} catch (IOException ex) {
			}
		}
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
		Set<Long> playerCache = cache.getPlayerCache(playerName);

		// Each chunk sended is handled by:
		// - BlockType: Whole byte per block
		// - BlockMetaData: Half byte per block
		// - BlockLight: Half byte per block
		// - SkyLight: Half byte per block (Only of handleLight is TRUE)
		// - AddArray: Half byte per block (Only if extraMask has the bit,
		// support for FORGE)
		// - BiomeArray: Whole byte per XZ coordinate (Only if isContinous is
		// TRUE)
		int chunkLen = buffer.length / ChunkCache.CHUNK_PARTITION_SIZE;
		if ((chunkLen & 0x7FF) != 0) {
			chunkLen++;
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutputStream dao = new DataOutputStream(out);
		byte[] chunkData = new byte[ChunkCache.CHUNK_PARTITION_SIZE];

		// For each CHUNK_PARTITION_SIZE block, check the hash of it.
		for (int i = 0; i < chunkLen; i++) {
			// Calculate the hash of the current block
			System.arraycopy(buffer, i * ChunkCache.CHUNK_PARTITION_SIZE,
					chunkData, 0x0000, ChunkCache.CHUNK_PARTITION_SIZE);
			long hash = ChunkCache.calculateHash(chunkData);

			// Write the hash into the packet
			dao.writeLong(hash);

			// Check for the chunk with the player cache
			if (playerCache.add(hash)) {
				dao.write(chunkData);
			}
		}

		// Copies the last XZ biome data.
		if (isContinuos) {
			dao.write(buffer, buffer.length - 256, 256);
		}

		// Close the output stream and return the bytes
		dao.close();
		return out.toByteArray();
	}

}
