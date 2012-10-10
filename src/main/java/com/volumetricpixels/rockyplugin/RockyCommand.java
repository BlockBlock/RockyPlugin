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
package com.volumetricpixels.rockyplugin;

import net.minecraft.server.Item;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * 
 */
public class RockyCommand implements CommandExecutor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage("[Rocky] Server version: "
					+ Rocky.getInstance().getDescription().getVersion());
			return true;
		}

		String c = args[0];
		if (!sender.isOp()) {
			sender.sendMessage("[Rocky] This command is Op only");
			return true;
		} else if (c.equals("waypoint")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("Only players can add waypoints.");
				return true;
			}
			if (args.length > 1) {
				String name = args[1];
				Rocky.getInstance().getConfiguration()
						.addWaypoint(name, ((Player) sender).getLocation());
				sender.sendMessage("Waypoint [" + name
						+ "] created successfully");
				return true;
			} else {
				sender.sendMessage("You must give a name to the waypoint.");
				return true;
			}
		} else if (c.equals("reload")) {
			Rocky.getInstance().onDisable();
			Rocky.getInstance().onLoad();
			return true;
		} else if (c.equals("item") && args.length > 1) {
			Integer item = Integer.valueOf(args[1]);
			int amount = (args.length > 2 ? Integer.valueOf(args[2]) : 1);
			String name = (args.length > 3 ? args[3] : sender.getName());

			Player player = Bukkit.getPlayerExact(name);
			if (player == null) {
				sender.sendMessage("The user must be online.");
				return true;
			} else if (amount > 64 || amount < 1) {
				sender.sendMessage("The minimum is 1 and the maximum is 64");
				return true;
			} else if (item < 0 || item > Item.byId.length
					|| Item.byId[item] == null) {
				sender.sendMessage("The item doesn't exist");
				return true;
			}
			player.getInventory().addItem(new ItemStack(item, amount));
			return true;
		}

		return false;
	}
}
