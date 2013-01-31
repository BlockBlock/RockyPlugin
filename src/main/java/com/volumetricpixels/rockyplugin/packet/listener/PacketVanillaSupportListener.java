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
package com.volumetricpixels.rockyplugin.packet.listener;

import java.util.List;

import net.minecraft.server.v1_4_6.ItemStack;
import net.minecraft.server.v1_4_6.NBTTagCompound;
import net.minecraft.server.v1_4_6.NBTTagList;
import net.minecraft.server.v1_4_6.NBTTagString;

import com.volumetricpixels.rockyapi.RockyManager;
import com.volumetricpixels.rockyapi.material.Item;
import com.volumetricpixels.rockyapi.packet.PacketListener;
import com.volumetricpixels.rockyapi.packet.PacketVanilla;
import com.volumetricpixels.rockyapi.player.RockyPlayer;
import com.volumetricpixels.rockyplugin.RockyMaterialManager;
import com.volumetricpixels.rockyplugin.packet.vanilla.PacketEntityEquipment;
import com.volumetricpixels.rockyplugin.packet.vanilla.PacketNamedEntitySpawn;
import com.volumetricpixels.rockyplugin.packet.vanilla.PacketSetCreativeSlot;
import com.volumetricpixels.rockyplugin.packet.vanilla.PacketSetSlot;
import com.volumetricpixels.rockyplugin.packet.vanilla.PacketWindowItems;

/**
 * Encapsulate {@see PacketListener} for supporting custom features such as item
 * into a non modded client.
 */
public class PacketVanillaSupportListener implements PacketListener {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean checkPacket(RockyPlayer player, PacketVanilla packet) {
		int packetId = packet.getId();
		ItemStack item = null;

		if (packetId == 0x5) {
			item = ((PacketEntityEquipment) packet).getItem();
		} else if (packetId == 0x67) {
			item = ((PacketSetSlot) packet).getItemStack();
		} else if (packetId == 0x6B) {
			item = ((PacketSetCreativeSlot) packet).getItem();
		} else if (packetId == 0x13
				&& ((PacketNamedEntitySpawn) packet).getCurrentItem() >= RockyMaterialManager.DEFAULT_ITEM_PLACEHOLDER_ID) {
			((PacketNamedEntitySpawn) packet)
					.setCurrentItem(RockyManager
							.getMaterialManager()
							.getItem(
									((PacketNamedEntitySpawn) packet)
											.getCurrentItem()).getDefaultId());
		} else if (packetId == 0x68) {
			ItemStack[] stacks = ((PacketWindowItems) packet).getItems();
			for (ItemStack itemStack : stacks) {
				if (itemStack != null
						&& itemStack.id >= RockyMaterialManager.DEFAULT_ITEM_PLACEHOLDER_ID) {
					setVanillaData(itemStack, RockyManager.getMaterialManager()
							.getItem(itemStack.id));
				}
			}
		}
		if (item != null
				&& item.id >= RockyMaterialManager.DEFAULT_ITEM_PLACEHOLDER_ID) {
			setVanillaData(item,
					RockyManager.getMaterialManager().getItem(item.id));
		}
		return true;
	}

	/**
	 * Sets the vanilla data of a custom item.
	 * 
	 * @param stack
	 *            The vanilla stack
	 * @param item
	 *            The custom item
	 */
	private void setVanillaData(ItemStack stack, Item item) {
		stack.id = item.getDefaultId();
		stack.c(item.getName());

		NBTTagCompound tag = stack.tag.getCompound("display");
		NBTTagList list = tag.getList("Lore");
		List<String> lore = item.getLore();

		if (list == null) {
			list = new NBTTagList();
		}
		if (lore.size() > 0) {
			for (String loreName : lore) {
				list.add(new NBTTagString("", loreName));
			}
			tag.set("Lore", list);
		}
	}

}
