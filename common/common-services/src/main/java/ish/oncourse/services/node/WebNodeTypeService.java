package ish.oncourse.services.node;

import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.WebSiteVersion;
import ish.oncourse.services.BaseService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

public class WebNodeTypeService extends BaseService<WebNodeType> implements
		IWebNodeTypeService {
	
	@Inject
	private IWebSiteVersionService webSiteVersionService;

	public WebNodeType getDefaultWebNodeType() {
		IWebSiteService webSiteService = getWebSiteService();
		
		Expression expr = ExpressionFactory.matchExp(
				WebNodeType.WEB_SITE_VERSION_PROPERTY, 
				webSiteVersionService.getCurrentVersion(webSiteService.getCurrentWebSite()));

		expr = expr.andExp(ExpressionFactory
				.matchExp(WebNodeType.LAYOUT_KEY_PROPERTY,
						WebNodeType.DEFAULT_LAYOUT_KEY));

        expr = expr.andExp(ExpressionFactory.matchExp(WebNodeType.NAME_PROPERTY, WebNodeType.PAGE));
		
		List<WebNodeType> webNodeTypes = findByQualifier(expr);

		return (!webNodeTypes.isEmpty()) ? webNodeTypes.get(0) : null;
	}

	public List<WebNodeType> getWebNodeTypes() {

        WebSiteVersion version = webSiteVersionService.getCurrentVersion(getWebSiteService().getCurrentWebSite());

        ObjectContext context = getCayenneService().newNonReplicatingContext();

        Expression expression = ExpressionFactory.matchExp(
                WebNodeType.WEB_SITE_VERSION_PROPERTY,context.localObject(version)
        );

        SelectQuery selectQuery = new SelectQuery(WebNodeType.class, expression);
        selectQuery.addOrdering(WebNodeType.MODIFIED_PROPERTY, SortOrder.DESCENDING);

        return context.performQuery(selectQuery);
	}
}
