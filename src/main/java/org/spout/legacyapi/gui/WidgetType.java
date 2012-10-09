/*
 * This file is part of SpoutLegacy.
 *
 * Copyright (c) 2012-2012, VolumetricPixels <http://www.volumetricpixels.com/>
 * SpoutLegacy is licensed under the GNU Lesser General Public License.
 *
 * SpoutLegacy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutLegacy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

import java.util.HashMap;

/**
 * This defines the built-in Spout widgets.
 */
public class WidgetType {
	public static WidgetType Label = new WidgetType(GenericLabel.class, 0);
	public static WidgetType HealthBar = new WidgetType(HealthBar.class, 1);
	public static WidgetType BubbleBar = new WidgetType(BubbleBar.class, 2);
	public static WidgetType ChatBar = new WidgetType(ChatBar.class, 3);
	public static WidgetType ChatTextBox = new WidgetType(ChatTextBox.class, 4);
	public static WidgetType ArmorBar = new WidgetType(ArmorBar.class, 5);
	public static WidgetType Texture = new WidgetType(GenericTexture.class, 6);
	public static WidgetType PopupScreen = new WidgetType(GenericPopup.class, 7);
	public static WidgetType InGameScreen = new WidgetType(null, 8);
	public static WidgetType ItemWidget = new WidgetType(
			GenericItemWidget.class, 9);
	public static WidgetType Button = new WidgetType(GenericButton.class, 10);
	public static WidgetType Slider = new WidgetType(GenericSlider.class, 11);
	public static WidgetType TextField = new WidgetType(GenericTextField.class,
			12);
	public static WidgetType Gradient = new WidgetType(GenericGradient.class,
			13);
	public static WidgetType Container = new WidgetType(GenericContainer.class,
			14, true);
	public static WidgetType EntityWidget = new WidgetType(
			GenericEntityWidget.class, 15);
	public static WidgetType HungerBar = new WidgetType(HungerBar.class, 16);
	public static WidgetType ExpBar = new WidgetType(ExpBar.class, 17);
	public static WidgetType CheckBox = new WidgetType(GenericCheckBox.class,
			18);
	public static WidgetType RadioButton = new WidgetType(
			GenericRadioButton.class, 19);
	public static WidgetType ListWidget = new WidgetType(
			GenericListWidget.class, 20);
	public static WidgetType ComboBox = new WidgetType(GenericComboBox.class,
			21);
	public static WidgetType Slot = new WidgetType(GenericSlot.class, 22);
	public static WidgetType OverlayScreen = new WidgetType(
			GenericOverlayScreen.class, 23);

	private final int id;
	private final boolean server;
	private final Class<? extends Widget> widgetClass;
	private final static HashMap<WidgetType, Integer> lookupClass = new HashMap<WidgetType, Integer>();
	private final static HashMap<Integer, WidgetType> lookupId = new HashMap<Integer, WidgetType>();
	private static int lastId = 0;

	/**
	 * 
	 * @param widget
	 */
	public WidgetType(Class<? extends Widget> widget) {
		widgetClass = widget;
		id = lastId;
		lastId++;
		lookupClass.put(this, id);
		lookupId.put(id, this);
		server = false;
	}

	/**
	 * 
	 * @param widget
	 * @param id
	 */
	private WidgetType(Class<? extends Widget> widget, int id) {
		widgetClass = widget;
		this.id = id;
		if (id > lastId) {
			lastId = id;
		}
		lookupClass.put(this, id);
		lookupId.put(id, this);
		server = false;
	}

	/**
	 * 
	 * @param widget
	 * @param id
	 * @param server
	 */
	private WidgetType(Class<? extends Widget> widget, int id, boolean server) {
		widgetClass = widget;
		this.id = id;
		if (id > lastId) {
			lastId = id;
		}
		lookupClass.put(this, id);
		lookupId.put(id, this);
		this.server = server;
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
	 * @return
	 */
	public Class<? extends Widget> getWidgetClass() {
		return widgetClass;
	}

	/**
	 * 
	 * @param widget
	 * @return
	 */
	public static Integer getWidgetId(Class<? extends Widget> widget) {
		return lookupClass.get(widget);
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public static WidgetType getWidgetFromId(int id) {
		return lookupId.get(id);
	}

	/**
	 * 
	 * @return
	 */
	public static int getNumWidgetTypes() {
		return lastId;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isServerOnly() {
		return server;
	}
}
