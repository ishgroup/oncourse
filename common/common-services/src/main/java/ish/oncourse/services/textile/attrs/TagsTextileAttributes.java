package ish.oncourse.services.textile.attrs;

import java.util.ArrayList;
import java.util.List;

public enum TagsTextileAttributes {
	TAGS_PARAM_NAME("name:"),
	TAGS_MAX_LEVELS_PARAM("maxLevels:"),
	TAGS_SHOW_DETAIL_PARAM("showDetail:"),
	TAGS_HIDE_TOP_LEVEL("hideTopLevel:"),
    TAGS_TEMPLATE_FILE_NAME("template:");//user defined template file name for this textile

	private String value;

	private TagsTextileAttributes(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static List<String> getAttrValues() {
		TagsTextileAttributes[] values = values();
		List<String> attrValues = new ArrayList<>(values.length);
		
		for (TagsTextileAttributes attr : values) {
			attrValues.add(attr.getValue());
		}
		
		return attrValues;
	}
}
