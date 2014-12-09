package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * @PublicApi
 */
public enum MessageTemplateType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * @PublicApi
	 */
	BASIC_TEMPLATE(0, "Basic template"),
	
	/**
	 * @PublicApi
	 */
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
