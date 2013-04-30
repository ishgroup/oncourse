package ish.oncourse.services.textile.attrs;

import java.util.ArrayList;
import java.util.List;

public enum AttachmentTextileAttributes {
	
	ATTACHMENT_PARAM_NAME("name:");
	
	private String value;
	
	private AttachmentTextileAttributes(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static List<String> getAttrValues() {
		AttachmentTextileAttributes[] values = values();
		List<String> attrValues = new ArrayList<>(values.length);
		
		for (AttachmentTextileAttributes attr : values) {
			attrValues.add(attr.getValue());
		}
		
		return attrValues;
	}

}
