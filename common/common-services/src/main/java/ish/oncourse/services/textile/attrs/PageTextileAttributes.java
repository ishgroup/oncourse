package ish.oncourse.services.textile.attrs;

import java.util.ArrayList;
import java.util.List;

public enum PageTextileAttributes {
	PAGE_CODE_PARAM("code:");
	
	private String value;

	private PageTextileAttributes(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static List<String> getAttrValues() {
		PageTextileAttributes[] values = values();
		List<String> attrValues = new ArrayList<>(values.length);
		
		for (PageTextileAttributes attr : values) {
			attrValues.add(attr.getValue());
		}
		
		return attrValues;
	}
}
