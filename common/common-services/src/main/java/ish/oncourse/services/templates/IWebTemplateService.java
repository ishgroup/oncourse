/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.templates;

import ish.oncourse.model.WebSiteLayout;
import ish.oncourse.model.WebTemplate;

import java.util.List;

public interface IWebTemplateService {

	/**
	 * Returns template with specified name belonging to specified layout if exists.
	 */
	WebTemplate getTemplateByName(String name, WebSiteLayout layout);

	/**
	 * Creates {@link WebTemplate} record with specified name and content linked to specified layout.
	 */
	WebTemplate createWebTemplate(String name, String content, WebSiteLayout layout);

	/**
	 * Returns list of templates belonging to specified layout.
	 */
	List<WebTemplate> getTemplatesForLayout(WebSiteLayout layout);
}
