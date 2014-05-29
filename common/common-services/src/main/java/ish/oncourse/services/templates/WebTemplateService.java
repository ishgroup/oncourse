/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.templates;

import ish.oncourse.model.WebSiteLayout;
import ish.oncourse.model.WebTemplate;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

import java.util.List;

public class WebTemplateService implements IWebTemplateService {

	@Override
	public WebTemplate getTemplateByName(String name, WebSiteLayout layout) {
		ObjectContext context = layout.getObjectContext();

		SelectQuery query = new SelectQuery(WebTemplate.class);

		query.andQualifier(ExpressionFactory.matchExp(WebTemplate.LAYOUT_PROPERTY, layout));
		query.andQualifier(ExpressionFactory.matchExp(WebTemplate.NAME_PROPERTY, name));

		return (WebTemplate) Cayenne.objectForQuery(context, query);
	}

	@Override
	public WebTemplate createWebTemplate(String name, String content, WebSiteLayout layout) {
		ObjectContext context = layout.getObjectContext();

		WebTemplate template = context.newObject(WebTemplate.class);
		template.setName(name);
		template.setLayout(layout);

		return template;
	}

	@Override
	public List<WebTemplate> getTemplatesForLayout(WebSiteLayout layout) {
		ObjectContext context = layout.getObjectContext();
		
		SelectQuery query = new SelectQuery(WebTemplate.class);
		query.andQualifier(ExpressionFactory.matchExp(WebTemplate.LAYOUT_PROPERTY, layout));
		
		return context.performQuery(query);
	}
}
