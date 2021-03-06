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
package com.volumetricpixels.rockyapi.event.input;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.volumetricpixels.rockyapi.keyboard.KeyBinding;
import com.volumetricpixels.rockyapi.player.RockyPlayer;

/**
 * Event that is called when a player stroke a key.
 */
public class KeyBindingEvent extends Event {
	private static final HandlerList HANDLER = new HandlerList();
	private final KeyBinding binding;
	private final RockyPlayer player;

	/**
	 * Default constructor
	 * 
	 * @param player
	 *            the player
	 * @param binding
	 *            the bind
	 */
	public KeyBindingEvent(RockyPlayer player, KeyBinding binding) {
		this.binding = binding;
		this.player = player;
	}

	/**
	 * Gets the binding that was trigger
	 * 
	 * @return the binding that was trigger
	 */
	public KeyBinding getBinding() {
		return binding;
	}

	/**
	 * Gets the player that press the key
	 * 
	 * @return the player that press the key
	 */
	public RockyPlayer getPlayer() {
		return player;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HandlerList getHandlers() {
		return HANDLER;
	}

	/**
	 * Gets all the event's handlers
	 * 
	 * @return all the event's handlers
	 */
	public static HandlerList getHandlerList() {
		return HANDLER;
	}
}