package ish.oncourse.services.textile.attrs;

import java.util.ArrayList;
import java.util.List;

public enum VideoTextileAttributes {
	VIDEO_PARAM_ID("id:"),
	VIDEO_PARAM_WIDTH("width:"),
	VIDEO_PARAM_HEIGHT("height:"),
	VIDEO_PARAM_TYPE("type:");
	
	private String value;

	private VideoTextileAttributes(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static List<String> getAttrValues() {
		VideoTextileAttributes[] values = values();
		List<String> attrValues = new ArrayList<>(values.length);
		
		for (VideoTextileAttributes attr : values) {
			attrValues.add(attr.getValue());
		}
		
		return attrValues;
	}
}
