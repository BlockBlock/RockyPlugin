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
package com.volumetricpixels.rockyplugin.player;

import java.util.List;
import java.util.Queue;

import org.fest.reflect.core.Reflection;

import com.volumetricpixels.rockyapi.RockyManager;
import com.volumetricpixels.rockyapi.player.RenderDistance;
import com.volumetricpixels.rockyplugin.Rocky;

import net.minecraft.server.v1_4_6.EntityPlayer;
import net.minecraft.server.v1_4_6.LongHashMap;
import net.minecraft.server.v1_4_6.PlayerChunkMap;
import net.minecraft.server.v1_4_6.WorldServer;

/**
 * 
 */
public class RockyPlayerServerManager extends PlayerChunkMap {

	private int serverDistance;

	/**
	 * 
	 * @param instance
	 */
	public RockyPlayerServerManager(PlayerChunkMap instance) {
		super(Reflection.field("world").ofType(WorldServer.class).in(instance)
				.get(), 3);

		serverDistance = Reflection.field("e").ofType(int.class).in(instance)
				.get();

		Reflection
				.field("managedPlayers")
				.ofType(List.class)
				.in(this)
				.set(Reflection.field("managedPlayers").ofType(List.class)
						.in(instance).get());
		Reflection
				.field("c")
				.ofType(LongHashMap.class)
				.in(this)
				.set(Reflection.field("c").ofType(LongHashMap.class)
						.in(instance).get());
		Reflection
				.field("d")
				.ofType(Queue.class)
				.in(this)
				.set(Reflection.field("d").ofType(Queue.class).in(instance)
						.get());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPlayer(EntityPlayer player) {
		if (Rocky.getInstance().isDisabled()
				|| RockyManager.getPlayerFromId(player.id) == null) {
			super.addPlayer(player);
			return;
		}
		setDynamicViewDistance(RockyManager.getPlayerFromId(player.id)
				.getRenderDistance());
		super.addPlayer(player);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void b(EntityPlayer player) {
		if (Rocky.getInstance().isDisabled()
				|| RockyManager.getPlayerFromId(player.id) == null) {
			super.b(player);
			return;
		}
		setDynamicViewDistance(RockyManager.getPlayerFromId(player.id)
				.getRenderDistance());
		super.b(player);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removePlayer(EntityPlayer player) {
		if (Rocky.getInstance().isDisabled()
				|| RockyManager.getPlayerFromId(player.id) == null) {
			super.removePlayer(player);
			return;
		}
		setDynamicViewDistance(RockyManager.getPlayerFromId(player.id)
				.getRenderDistance());
		super.removePlayer(player);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void movePlayer(EntityPlayer player) {
		if (Rocky.getInstance().isDisabled()
				|| RockyManager.getPlayerFromId(player.id) == null) {
			super.movePlayer(player);
			return;
		}
		setDynamicViewDistance(RockyManager.getPlayerFromId(player.id)
				.getRenderDistance());
		super.movePlayer(player);
	}

	/**
	 * 
	 * @param viewDistance
	 */
	private void setDynamicViewDistance(RenderDistance distance) {
		int lastDistance = serverDistance;
		if (distance.getValue() < serverDistance) {
			lastDistance = distance.getValue();
		}
		Reflection.field("e").ofType(int.class).in(this).set(lastDistance);
	}

}
