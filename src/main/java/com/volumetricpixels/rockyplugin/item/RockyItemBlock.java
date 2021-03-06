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

import net.minecraft.server.v1_4_6.ItemBlock;

import org.fest.reflect.core.Reflection;

import com.volumetricpixels.rockyapi.material.Block;
import com.volumetricpixels.rockyapi.material.Material;

/**
 * 
 */
public class RockyItemBlock extends ItemBlock {

	/**
	 * 
	 * @param material
	 */
	public RockyItemBlock(Material material) {
		this((Block) material);
	}

	/**
	 * 
	 * @param i
	 */
	public RockyItemBlock(Block block) {
		super(block.getItemBlock().getId() - 256);

		Reflection.field("name").ofType(String.class).in(this)
				.set("name." + block.getName());
		Reflection.field("id").ofType(int.class).in(this).set(block.getId());
	}

}
