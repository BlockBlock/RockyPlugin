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
package com.volumetricpixels.rockyapi.gui;

/**
 * Used to define the order widgets are rendered to screen.
 * <p/>
 * Please remember that the earlier a widget is rendered, the more widgets will
 * be placed on top of it. In other words, first (Highest) is on the bottom and
 * last (Lowest) is on the top.
 */
public enum RenderPriority {
	/**
	 * Will render before all other textures and widgets
	 */
	Highest(0),
	/**
	 * Will render before most other textures and widgets
	 */
	High(1),
	/**
	 * Will render in line with most other textures and widgets
	 */
	Normal(2),
	/**
	 * Will render after most other textures and widgets
	 */
	Low(3),
	/**
	 * Will render after all other textures and widgets
	 */
	Lowest(4);
	private final int id;

	/**
	 * 
	 * @param id
	 */
	private RenderPriority(int id) {
		this.id = id;
	}

	/**
	 * 
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public static RenderPriority getRenderPriorityFromId(int id) {
		for (RenderPriority rp : values()) {
			if (rp.getId() == id) {
				return rp;
			}
		}
		return null;
	}
}
