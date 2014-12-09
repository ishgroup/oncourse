package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

import java.awt.*;

/**
 * enumeration of available colours for the tags
 * 
 * @PublicApi
 */
public enum TagColor implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * @PublicApi
	 */
	RED(0, Color.RED, "Red"),

	/**
	 * @PublicApi
	 */
	ORANGE(1, Color.ORANGE, "Orange"),

	/**
	 * @PublicApi
	 */
	YELLOW(2, Color.YELLOW, "Yellow"),

	/**
	 * @PublicApi
	 */
	GREEN(3, Color.GREEN, "Green"),

	/**
	 * @PublicApi
	 */
	BLUE(4, Color.BLUE, "Blue"),

	/**
	 * @PublicApi
	 */
	PURPLE(5, Color.PINK, "Purple"),

	/**
	 * @PublicApi
	 */
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
