package ish.oncourse.services.textile.attrs;

import java.util.ArrayList;
import java.util.List;

public enum TagsTextileAttributes {
	TAGS_PARAM_NAME("name:"),
	TAGS_ENTITY_TYPE_PARAM("entityType:"),
	TAGS_MAX_LEVELS_PARAM("maxLevels:"),
	TAGS_SHOW_DETAIL_PARAM("showtopdetail:"),
	TAGS_HIDE_TOP_LEVEL("isHidingTopLevelTags:"),
	TAGS_FILTERED_PARAM("isFiltered:");
	
	private String value;

	private TagsTextileAttributes(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static List<String> getAttrValues() {
		TagsTextileAttributes[] values = values();
		List<String> attrValues = new ArrayList<String>(values.length);
		
		for (TagsTextileAttributes attr : values) {
			attrValues.add(attr.getValue());
		}
		
		return attrValues;
	}
}
