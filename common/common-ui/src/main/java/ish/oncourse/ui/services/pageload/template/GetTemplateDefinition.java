/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.services.pageload.template;

import ish.oncourse.services.textile.CustomTemplateDefinition;
import ish.oncourse.services.textile.TextileUtil;
import org.apache.tapestry5.services.pageload.ComponentResourceSelector;

import java.util.Map;

/**
 * User: akoiro
 * Date: 22/11/17
 */
public class GetTemplateDefinition {

	private ComponentResourceSelector selector;

	public GetTemplateDefinition(ComponentResourceSelector selector) {
		this.selector = selector;
	}

	public CustomTemplateDefinition get(String name) {
		Map params = selector.getAxis(Map.class);
		CustomTemplateDefinition definition = params != null ? (CustomTemplateDefinition) params.get(TextileUtil.CUSTOM_TEMPLATE_DEFINITION) : null;
		return definition != null && name.endsWith(definition.getTemplateClassName()) ? definition : null;
	}
}
