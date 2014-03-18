package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

import java.awt.*;

/**
 * enumeration of available colours for the tags
 * 
 * @author marcin
 */
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
