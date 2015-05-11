/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.templates;

import ish.oncourse.model.WebSiteLayout;
import ish.oncourse.model.WebTemplate;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;

import java.util.List;

public class WebTemplateService implements IWebTemplateService {

	@Override
	public WebTemplate getTemplateByName(String name, WebSiteLayout layout) {
		return ObjectSelect.query(WebTemplate.class)
				.where(WebTemplate.LAYOUT.eq(layout).andExp(WebTemplate.NAME.eq(name)))
				.selectOne(layout.getObjectContext());
	}

	@Override
	public WebTemplate createWebTemplate(String name, String content, WebSiteLayout layout) {
		ObjectContext context = layout.getObjectContext();

		WebTemplate template = context.newObject(WebTemplate.class);
		template.setName(name);
		template.setLayout(layout);
        template.setContent(content);

		return template;
	}

	@Override
	public List<WebTemplate> getTemplatesForLayout(WebSiteLayout layout) {
		return ObjectSelect.query(WebTemplate.class)
				.where(WebTemplate.LAYOUT.eq(layout))
				.select(layout.getObjectContext());
	}
}
