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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import net.minecraft.server.v1_4_6.EntityPlayer;
import net.minecraft.server.v1_4_6.INetworkManager;
import net.minecraft.server.v1_4_6.ItemStack;
import net.minecraft.server.v1_4_6.MinecraftServer;
import net.minecraft.server.v1_4_6.Packet51MapChunk;
import net.minecraft.server.v1_4_6.PlayerConnection;
import net.minecraft.server.v1_4_6.Packet;
import net.minecraft.server.v1_4_6.Packet103SetSlot;
import net.minecraft.server.v1_4_6.Packet104WindowItems;
import net.minecraft.server.v1_4_6.Packet107SetCreativeSlot;
import net.minecraft.server.v1_4_6.Packet14BlockDig;
import net.minecraft.server.v1_4_6.Packet204LocaleAndViewDistance;
import net.minecraft.server.v1_4_6.Packet20NamedEntitySpawn;
import net.minecraft.server.v1_4_6.Packet250CustomPayload;
import net.minecraft.server.v1_4_6.Packet56MapChunkBulk;
import net.minecraft.server.v1_4_6.Packet29DestroyEntity;

import org.bukkit.Bukkit;
import org.fest.reflect.core.Reflection;

import com.volumetricpixels.rockyapi.RockyManager;
import com.volumetricpixels.rockyapi.event.player.PlayerEnterPlayerArea;
import com.volumetricpixels.rockyapi.event.player.PlayerLeavePlayerArea;
import com.volumetricpixels.rockyapi.player.RenderDistance;
import com.volumetricpixels.rockyapi.player.RockyPlayer;
import com.volumetricpixels.rockyplugin.Rocky;
import com.volumetricpixels.rockyplugin.RockyMaterialManager;
import com.volumetricpixels.rockyplugin.chunk.WorldCacheWorker;

/**
 * 
 */
public class RockyPacketHandler extends PlayerConnection {
	/**
	 * 
	 */
	private static final int QUEUE_PACKET_SIZE = 9437184;

	private LinkedBlockingDeque<Packet> resyncQueue = new LinkedBlockingDeque<Packet>();
	private ExecutorService threadService;

	/**
	 * 
	 * @param minecraftserver
	 * @param inetworkmanager
	 * @param entityplayer
	 */
	public RockyPacketHandler(MinecraftServer minecraftserver,
			INetworkManager inetworkmanager, EntityPlayer entityplayer) {
		super(minecraftserver, inetworkmanager, entityplayer);
		Reflection
				.field("y")
				.ofType(double.class)
				.in(this)
				.set(Reflection.field("y").ofType(double.class).in(this).get()
						- QUEUE_PACKET_SIZE);
		threadService = Executors.newCachedThreadPool();
	}

	/**
	 * {@inhericDoc}
	 */
	@Override
	public void a(Packet14BlockDig packet) {
		RockyPlayer player = (RockyPlayer) RockyManager.getPlayer(getPlayer());
		boolean inAir = false;
		if (player.canFly() && !player.getHandle().onGround) {
			inAir = true;
			player.getHandle().onGround = true;
		}
		super.a(packet);
		if (inAir) {
			player.getHandle().onGround = false;
		}
	}

	/**
	 * {@inhericDoc}
	 */
	@Override
	public void a(Packet250CustomPayload packet250custompayload) {
		if (packet250custompayload.tag.equals("TM|Rocky")) {
			RockyPlayer player = (RockyPlayer) RockyManager
					.getPlayer(getPlayer());
			Rocky.getInstance().handlePlayerAuthentication(player);
		} else {
			super.a(packet250custompayload);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void a(Packet204LocaleAndViewDistance packet204localeandviewdistance) {
		RenderDistance distance = RenderDistance
				.getRenderDistanceFromValue(256 >> packet204localeandviewdistance
						.f());
		RockyPlayer player = (RockyPlayer) RockyManager.getPlayer(getPlayer());
		player.setRenderDistance(distance);
		Reflection.field("b").ofType(int.class)
				.in(packet204localeandviewdistance).set(distance.getValue());
		super.a(packet204localeandviewdistance);
	}

	/**
	 * {@inhericDoc}
	 */
	@Override
	public void sendPacket(Packet packet) {
		if (packet == null || checkForMapChunkBulkCache(packet)) {
			return;
		}

		checkForInvalidStack(packet);
		queueOutputPacket(packet);
		checkForPostPacket(packet);
	}

	/**
	 * 
	 * @param packet
	 */
	public void sendImmediatePacket(Packet packet) {
		if (packet == null || checkForMapChunkBulkCache(packet)) {
			return;
		}
		resyncQueue.addFirst(packet);
	}

	/**
	 * 
	 * @param packet
	 */
	public void queueOutputPacket(Packet packet) {
		if (packet == null) {
			return;
		}
		resyncQueue.addLast(packet);
	}

	/**
	 * {@inhericDoc}
	 */
	@Override
	public void d() {
		syncFlushPacketQueue();
		super.d();
	}

	/**
	 * 
	 */
	public void syncFlushPacketQueue() {
		while (!resyncQueue.isEmpty()) {
			Packet p = resyncQueue.pollFirst();
			if (p != null) {
				syncedSendPacket(p);
			}
		}
	}

	/**
	 * 
	 * @param packet
	 * @param packetWrappers
	 */
	private void syncedSendPacket(Packet packet) {
		int packetId = -1;
		try {
			packetId = packet.k();
		} catch (Exception e) {
			return;
		}
		RockyPlayer player = (RockyPlayer) RockyManager.getPlayer(getPlayer());
		if (!RockyManager.getPacketManager().isAllowedToSend(player, packetId)) {
			return;
		} else {
			super.sendPacket(packet);
		}
	}

	/**
	 * Check if the packet is 0x33 or 0x36 for chunk cache
	 * 
	 * @param packet
	 */
	private boolean checkForMapChunkBulkCache(Packet packet) {
		if (!(packet instanceof Packet56MapChunkBulk)
				&& !(packet instanceof Packet51MapChunk)) {
			return false;
		}
		RockyPlayer player = RockyManager.getPlayer(getPlayer());
		if (player == null || !player.isModded()) {
			// return false;
		}
		threadService.submit(new WorldCacheWorker(this, packet));
		return true;
	}

	/**
	 * 
	 * @param packet
	 */
	private void checkForInvalidStack(Packet packet) {
		if (RockyManager.getPlayer(getPlayer()).isModded()) {
			return;
		}
		ItemStack stack = null;

		// Check all packets that use ItemStack
		switch (packet.k()) {
		case 0x5:
			stack = Reflection.field("c").ofType(ItemStack.class).in(packet)
					.get();
			break;
		case 0x14:
			if (((Packet20NamedEntitySpawn) packet).h >= RockyMaterialManager.DEFAULT_ITEM_PLACEHOLDER_ID) {
				((Packet20NamedEntitySpawn) packet).h = RockyManager
						.getMaterialManager()
						.getItem(((Packet20NamedEntitySpawn) packet).h)
						.getDefaultId();
			}
			break;
		case 0x67:
			stack = ((Packet103SetSlot) packet).c;
			break;
		case 0x68:
			ItemStack[] stacks = ((Packet104WindowItems) packet).b;
			for (ItemStack itemStack : stacks) {
				if (itemStack != null
						&& itemStack.id >= RockyMaterialManager.DEFAULT_ITEM_PLACEHOLDER_ID) {
					itemStack.id = RockyManager.getMaterialManager()
							.getItem(itemStack.id).getDefaultId();
				}
			}
			break;
		case 0x6B:
			stack = ((Packet107SetCreativeSlot) packet).b;
			break;
		}

		// The stack contain a custom id and we don't have a custom client
		if (stack != null
				&& stack.id >= RockyMaterialManager.DEFAULT_ITEM_PLACEHOLDER_ID) {
			stack.id = RockyManager.getMaterialManager().getItem(stack.id)
					.getDefaultId();
		}
	}

	/**
	 * 
	 * @param packet
	 */
	private void checkForPostPacket(Packet packet) {
		RockyPlayer player = null;
		switch (packet.k()) {
		case 0x14:
			player = RockyManager
					.getPlayerFromId(((Packet20NamedEntitySpawn) packet).a);
			if (player != null) {
				Bukkit.getPluginManager().callEvent(
						new PlayerEnterPlayerArea(player, RockyManager
								.getPlayer(getPlayer())));
			}
			break;
		case 0x1D:
			int[] ids = ((Packet29DestroyEntity) packet).a;
			for (int id : ids) {
				player = RockyManager.getPlayerFromId(id);
				if (player != null) {
					Bukkit.getPluginManager().callEvent(
							new PlayerLeavePlayerArea(player, RockyManager
									.getPlayer(getPlayer())));
				}
			}
			break;
		}

	}

}
