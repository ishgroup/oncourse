package ish.oncourse.services.node;

import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class WebNodeService implements IWebNodeService {

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private Request request;

	static final String PAGE_NAME_PARAMETER = "p";


	@SuppressWarnings("unchecked")
	public List<WebNode> getNodes() {
		SelectQuery query = new SelectQuery(WebNode.class);
		query.andQualifier(siteQualifier());
		query.addOrdering(WebNode.WEIGHTING_PROPERTY, SortOrder.ASCENDING);
		return cayenneService.sharedContext().performQuery(query);
	}

	public WebNode getHomePage() {
		return webSiteService.getCurrentWebSite().getHomePage();
	}

	public WebNode getCurrentPage() {
		String p = request.getParameter(PAGE_NAME_PARAMETER);
		SelectQuery query = new SelectQuery(WebNode.class);
		query.andQualifier(siteQualifier());
		query.andQualifier(
				ExpressionFactory.matchExp(WebNode.NAME_PROPERTY, p));
		query.andQualifier(
				ExpressionFactory.matchExp(WebNode.TYPE_PROPERTY + "."
						+ WebNodeType.NAME_PROPERTY, "Page"));
		
		List<WebNode> nodes = cayenneService.sharedContext()
				.performQuery(query);
		
		return (nodes.size() > 0) ? nodes.get(0) : null;
	}

	private Expression siteQualifier() {
		WebSite site = webSiteService.getCurrentWebSite();
		Expression expression = (site == null) ?
			ExpressionFactory.matchExp(
					WebNode.SITE_PROPERTY + "." + WebSite.COLLEGE_PROPERTY,
					webSiteService.getCurrentCollege())
			: ExpressionFactory.matchExp(WebNode.SITE_PROPERTY, site);

		expression = expression
				.andExp(ExpressionFactory.matchExp(WebNode.PUBLISHED_PROPERTY, true))
				.andExp(ExpressionFactory.matchExp(WebNode.WEB_NAVIGABLE_PROPERTY, true))
				.andExp(ExpressionFactory.matchExp(WebNode.WEB_VISIBLE_PROPERTY, true))
				.andExp(ExpressionFactory.matchExp(WebNode.DELETED_PROPERTY, false)
						.orExp(ExpressionFactory.matchExp(WebNode.DELETED_PROPERTY, null)));
		
		return expression;
	}
}
