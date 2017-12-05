/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.services.pageload.template.resource;

import org.apache.tapestry5.TapestryConstants;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.model.ComponentModel;
import org.apache.tapestry5.services.pageload.ComponentResourceLocator;
import org.apache.tapestry5.services.pageload.ComponentResourceSelector;

/**
 * User: akoiro
 * Date: 22/11/17
 */
public class GetDefaultTemplateResource {
	private ComponentResourceLocator componentResourceLocator;

	public GetDefaultTemplateResource(ComponentResourceLocator componentResourceLocator) {
		this.componentResourceLocator = componentResourceLocator;
	}

	public Resource get(ComponentModel initialModel, ComponentResourceSelector selector) {
		ComponentModel model = initialModel;
		while (model != null) {
			Resource localized = componentResourceLocator.locateTemplate(model, selector);
			if (localized != null) {
				return localized;
			}
			model = model.getParentModel();
		}
		return initialModel.getBaseResource().withExtension(TapestryConstants.TEMPLATE_EXTENSION);
	}
}
