package ish.oncourse.services.textile.attrs;

import java.util.ArrayList;
import java.util.List;

public enum ImageTextileAttributes {
	IMAGE_PARAM_ALIGH("align:"),
	IMAGE_PARAM_CAPTION("caption:"),
	IMAGE_PARAM_ALT("alt:"),
	IMAGE_PARAM_LINK("link:"),
	IMAGE_PARAM_CLASS ("class:"),
	IMAGE_PARAM_TITLE("title:"),
	IMAGE_PARAM_WIDTH("width:"),
	IMAGE_PARAM_HEIGHT("height:"),
	IMAGE_PARAM_NAME("name:"),
	IMAGE_PARAM_ATTACHMENT("attachment:");
	
	private String value;

	private ImageTextileAttributes(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static List<String> getAttrValues() {
		ImageTextileAttributes[] values = values();
		List<String> attrValues = new ArrayList<>(values.length);
		
		for (ImageTextileAttributes attr : values) {
			attrValues.add(attr.getValue());
		}
		
		return attrValues;
	}
}
