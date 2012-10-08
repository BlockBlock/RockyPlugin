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
package org.spout.legacyapi.gui;

import java.io.IOException;

import org.spout.legacyapi.packet.PacketInputStream;
import org.spout.legacyapi.packet.PacketOutputStream;

public class GenericCheckBox extends GenericButton implements CheckBox {
	boolean checked = false;

	/**
	 * 
	 */
	public GenericCheckBox() {
		super();
	}

	/**
	 * 
	 * @param text
	 */
	public GenericCheckBox(String text) {
		super(text);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void readData(PacketInputStream input) throws IOException {
		super.readData(input);
		checked = input.readBoolean();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeData(PacketOutputStream output) throws IOException {
		super.writeData(output);
		output.writeBoolean(checked);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public WidgetType getType() {
		return WidgetType.CheckBox;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isChecked() {
		return checked;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CheckBox copy() {
		return ((CheckBox) super.copy()).setChecked(isChecked());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CheckBox setChecked(boolean checked) {
		if (isChecked() != checked) {
			this.checked = checked;
			autoDirty();
		}
		return this;
	}
}
