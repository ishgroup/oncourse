/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

import java.awt.*;

@Deprecated
public enum TagColor implements DisplayableExtendedEnumeration<Integer> {

	RED(0, Color.RED, "Red"),
	ORANGE(1, Color.ORANGE, "Orange"),
	YELLOW(2, Color.YELLOW, "Yellow"),
	GREEN(3, Color.GREEN, "Green"),
	BLUE(4, Color.BLUE, "Blue"),
	PURPLE(5, Color.PINK, "Purple"),
	GREY(6, Color.GRAY, "Grey");

	private String displayName;
	private int value;
	private Color color;

	private TagColor(int value, Color c, String displayName) {
		this.value = value;
		this.displayName = displayName;
		this.color = c;
	}

	@Override
	public Integer getDatabaseValue() {
		return this.value;
	}

	@Override
	public String getDisplayName() {
		return this.displayName;
	}

	/**
	 * @return Color associated with database value
	 */
	public Color getColor() {
		return this.color;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
}
