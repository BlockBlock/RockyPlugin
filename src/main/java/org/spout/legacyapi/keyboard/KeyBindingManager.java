/*
 * This file is part of SpoutPlugin.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutPlugin is licensed under the GNU Lesser General Public License.
 *
 * SpoutPlugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutPlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.spout.legacyapi.keyboard;

import java.util.UUID;

import org.bukkit.plugin.Plugin;
import org.spout.legacyapi.player.SpoutPlayer;

/**
 * 
 */
public interface KeyBindingManager {
	/**
	 * Registers a new kind binding
	 * 
	 * @param id
	 *            unique id that represents your binding. IT MUST BE UNIQUE!
	 * @param defaultKey
	 *            for this binding.
	 * @param description
	 *            of this key binding, that players will see.
	 * @param callback
	 *            the class that will receive the event callbacks.
	 * @param plugin
	 *            that registered this binding.
	 * @throws IllegalArgumentException
	 *             if the id passed is not unique.
	 */
	public void registerBinding(String id, Keyboard defaultKey,
			String description, BindingExecutionDelegate callback, Plugin plugin)
			throws IllegalArgumentException;

	/**
	 * 
	 * @param uniqueId
	 * @param player
	 * @param key
	 * @param pressed
	 */
	public void summonKey(UUID uniqueId, SpoutPlayer player, Keyboard key,
			boolean pressed);
	
	/**
	 * 
	 * @param player
	 */
	public void sendKeyBinding(SpoutPlayer player);
	
}