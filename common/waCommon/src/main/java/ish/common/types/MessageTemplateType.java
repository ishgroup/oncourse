package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * @author vdavidovich
 */
public enum MessageTemplateType implements DisplayableExtendedEnumeration<Integer> {

	BASIC_TEMPLATE(0, "Basic template"),
	XSLT_TEMPLATE(1, "XSLT template");

	private String displayName;
	private int value;

	private MessageTemplateType(int value, String displayName) {
		this.displayName = displayName;
		this.value = value;
	}

	@Override
	public Integer getDatabaseValue() {
		return this.value;
	}

	@Override
	public String getDisplayName() {
		return this.displayName;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}

	public static boolean isXSLTTemplate(MessageTemplateType messageTemplateType) {
		return XSLT_TEMPLATE.equals(messageTemplateType);
	}
}
