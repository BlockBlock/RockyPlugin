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

import com.volumetricpixels.rockyapi.keyboard.Keyboard;
import com.volumetricpixels.rockyapi.player.RockyPlayer;

/**
 * Event that is called when a player stroke a key.
 */
public class KeyEvent extends Event {
	private static final HandlerList HANDLER = new HandlerList();
	private final RockyPlayer player;
	private final Keyboard key;
	private final boolean isPressed;

	/**
	 * Default constructor
	 * 
	 * @param keyPress
	 *            the key number
	 * @param player
	 *            the player
	 * @param isPressed
	 *            if the key was pressed
	 */
	public KeyEvent(int keyPress, RockyPlayer player, boolean isPressed) {
		this.player = player;
		this.key = Keyboard.getKey(keyPress);
		this.isPressed = isPressed;
	}

	/**
	 * Gets the player
	 * 
	 * @return the player
	 */
	public RockyPlayer getPlayer() {
		return player;
	}

	/**
	 * Gets the key pressed
	 * 
	 * @return the key pressed
	 */
	public Keyboard getKey() {
		return key;
	}

	/**
	 * Gets if the key was pressed or not
	 * 
	 * @return if the key was pressed or not
	 */
	public boolean isPressed() {
		return isPressed;
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