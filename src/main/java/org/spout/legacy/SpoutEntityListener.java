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
package org.spout.legacy;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.spout.legacyapi.packet.protocol.PacketWaypoint;
import org.spout.legacyapi.player.SpoutPlayer;

/**
 * 
 */
public class SpoutEntityListener implements Listener {

	/**
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof SpoutPlayer)
			event.setCancelled(!((SpoutPlayer) event.getEntity())
					.isPreCachingComplete());
	}

	/**
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onEntityTarget(EntityTargetEvent event) {
		if (event.getTarget() instanceof SpoutPlayer)
			event.setCancelled(!((SpoutPlayer) event.getTarget())
					.isPreCachingComplete());
	}

	/**
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDeath(EntityDeathEvent event) {
		if (!(event.getEntity() instanceof SpoutPlayer)) {
			return;
		}
		Location l = event.getEntity().getLocation();
		((SpoutPlayer) event.getEntity()).sendPacket(new PacketWaypoint(l
				.getX(), l.getY(), l.getZ(), "", true));
	}
}
