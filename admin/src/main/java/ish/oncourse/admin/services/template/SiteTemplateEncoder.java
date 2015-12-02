/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.admin.services.template;

import ish.oncourse.model.WebSite;
import org.apache.tapestry5.ValueEncoder;

import java.util.List;

public class SiteTemplateEncoder implements ValueEncoder<WebSite> {

	private List<WebSite> templates;
	
	@Override
	public String toClient(WebSite value) {
		return value.getSiteKey();
	}

	@Override
	public WebSite toValue(String clientValue) {
		for (WebSite template : templates) {
			if (template.getSiteKey().equals(clientValue)) {
				return template;
			}
		}
		throw new IllegalArgumentException(String.format("Cannot find template with name %s", clientValue));
	}

	public static SiteTemplateEncoder valueOf(List<WebSite> templates) {
		SiteTemplateEncoder result = new SiteTemplateEncoder();
		result.templates = templates;
		return result;
	}
}
