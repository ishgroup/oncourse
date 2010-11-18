package ish.oncourse.services.node;

import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.Collections;
import java.util.List;

import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;

public class WebNodeTypeService implements IWebNodeTypeService {

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ICayenneService cayenneService;

	public WebNodeType getDefaultWebNodeType() {
		SelectQuery q = new SelectQuery(WebNodeType.class);

		q.andQualifier(ExpressionFactory.matchExp(
				WebNodeType.WEB_SITE_PROPERTY,
				webSiteService.getCurrentWebSite()));

		q.andQualifier(ExpressionFactory
				.matchExp(WebNodeType.LAYOUT_KEY_PROPERTY,
						WebNodeType.DEFAULT_LAYOUT_KEY));

		return (WebNodeType) DataObjectUtils.objectForQuery(
				cayenneService.sharedContext(), q);
	}

	public List<WebNodeType> getWebNodeTypes() {
		SelectQuery q = new SelectQuery(WebNodeType.class);

		q.andQualifier(ExpressionFactory.matchExp(
				WebNodeType.WEB_SITE_PROPERTY,
				webSiteService.getCurrentWebSite()));

		return cayenneService.sharedContext().performQuery(q);
	}

	public WebNodeType newWebNodeType() {

		WebNodeType type = cayenneService.sharedContext().newObject(
				WebNodeType.class);

		cayenneService.sharedContext().commitChanges();

		return type;
	}

	public List<WebNodeType> loadByIds(Object... ids) {

		if (ids.length == 0) {
			return Collections.emptyList();
		}

		SelectQuery q = new SelectQuery(WebNodeType.class);
		q.andQualifier(ExpressionFactory.inDbExp("id", ids));

		return cayenneService.sharedContext().performQuery(q);
	}
}
