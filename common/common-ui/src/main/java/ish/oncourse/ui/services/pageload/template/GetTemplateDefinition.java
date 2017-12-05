/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.services.pageload.template;

import ish.oncourse.services.textile.CustomTemplateDefinition;
import ish.oncourse.services.textile.TextileUtil;
import org.apache.tapestry5.services.Request;

/**
 * User: akoiro
 * Date: 22/11/17
 */
public class GetTemplateDefinition {

	private Request request;

	public GetTemplateDefinition(Request request) {
		this.request = request;
	}

	public CustomTemplateDefinition get(String name) {
		CustomTemplateDefinition ctd = (CustomTemplateDefinition) request.getAttribute(TextileUtil.CUSTOM_TEMPLATE_DEFINITION);
		if (ctd != null && !name.endsWith(ctd.getTemplateClassName())) {
			ctd = null;
		}
		return ctd;
	}
}
