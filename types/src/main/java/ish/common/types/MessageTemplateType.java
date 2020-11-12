/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

@Deprecated
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
