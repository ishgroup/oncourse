/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.services.pageload.template;

/**
 * User: akoiro
 * Date: 3/4/18
 */
public enum CacheKey {
	resources,
	templates,
	assemblers,
	pages;

	public String getCacheName(String applicationKey) {
		return String.format("%s_%s", this.name(), applicationKey);
	}
}
