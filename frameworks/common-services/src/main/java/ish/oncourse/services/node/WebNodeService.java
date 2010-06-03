package ish.oncourse.services.node;

import java.util.List;

import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;


public class WebNodeService implements IWebNodeService {

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ICayenneService cayenneService;

	public List<WebNode> getNodes() {

		SelectQuery query = new SelectQuery(WebNode.class);

		WebSite site = webSiteService.getCurrentWebSite();
		if (site == null) {

			query.andQualifier(ExpressionFactory.matchExp(
					WebNode.SITE_PROPERTY + "." + WebSite.COLLEGE_PROPERTY,
					webSiteService.getCurrentCollege()));
		} else {
			query.andQualifier(ExpressionFactory.matchExp(
					WebNode.SITE_PROPERTY, site));
		}

		query.addOrdering(WebNode.NAME_PROPERTY, SortOrder.ASCENDING);
		return cayenneService.sharedContext().performQuery(query);
	}

	public WebNode getHomePage() {
		return webSiteService.getCurrentWebSite().getHomePage();
	}
}
