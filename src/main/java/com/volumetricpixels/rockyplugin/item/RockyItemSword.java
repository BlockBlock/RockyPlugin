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
package com.volumetricpixels.rockyplugin.item;

import net.minecraft.server.v1_4_6.EnumToolMaterial;
import net.minecraft.server.v1_4_6.ItemSword;

import org.fest.reflect.core.Reflection;

import com.volumetricpixels.rockyapi.material.Material;
import com.volumetricpixels.rockyapi.material.Weapon;

/**
 * 
 */
public class RockyItemSword extends ItemSword {

	/**
	 * 
	 * @param item
	 */
	public RockyItemSword(Material item) {
		this((Weapon)item);
	}
	
	/**
	 * 
	 * @param item
	 */
	public RockyItemSword(Weapon item) {
		super(item.getId() - 256, EnumToolMaterial.DIAMOND);

		Reflection.field("maxStackSize").ofType(int.class).in(this)
				.set(item.isStackable() ? 64 : 1);
		setMaxDurability(item.getDurability());
		Reflection.field("name").ofType(String.class).in(this)
				.set("name." + item.getName());
		Reflection.field("damage").ofType(int.class).in(this)
				.set(item.getDamage());
	}

}
