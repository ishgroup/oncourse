package ish.oncourse.services.node;

import ish.oncourse.model.WebLayoutPath;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.WebSiteVersion;
import ish.oncourse.services.BaseService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.specialpages.RequestMatchType;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.Comparator;
import java.util.List;

public class WebNodeTypeService extends BaseService<WebNodeType> implements
		IWebNodeTypeService {

    private static final String KEY_defaultWebNodeType = "defaultWebNodeType";

	@Inject
	private IWebSiteVersionService webSiteVersionService;

    @Inject
    private Request request;

    @Inject
    private IWebSiteService webSiteService;

    @Inject
    private ICayenneService cayenneService;

	public WebNodeType getDefaultWebNodeType() {
        Expression expr = ExpressionFactory.matchExp(
                WebNodeType.WEB_SITE_VERSION_PROPERTY,
                webSiteVersionService.getCurrentVersion());


        expr = expr.andExp(ExpressionFactory.matchExp(WebNodeType.NAME_PROPERTY, WebNodeType.PAGE));

        List<WebNodeType> webNodeTypes = findByQualifier(expr);

        WebNodeType result = (!webNodeTypes.isEmpty()) ? webNodeTypes.get(0) : null;
        return result;
	}

	public List<WebNodeType> getWebNodeTypes() {

        WebSiteVersion version = webSiteVersionService.getCurrentVersion();

        ObjectContext context = getCayenneService().sharedContext();

        Expression expression = ExpressionFactory.matchExp(
                WebNodeType.WEB_SITE_VERSION_PROPERTY, context.localObject(version)
        );

        SelectQuery selectQuery = new SelectQuery(WebNodeType.class, expression);
        selectQuery.setCacheStrategy(QueryCacheStrategy.LOCAL_CACHE);
        selectQuery.setCacheGroup(getCacheGroup());
        selectQuery.addOrdering(WebNodeType.MODIFIED_PROPERTY, SortOrder.DESCENDING);

        return context.performQuery(selectQuery);
	}

    @Override
    public WebNodeType getWebNodeType() {

        if (webSiteService.getCurrentWebSite() == null) {
            return null;
        }
        String urlPath = request.getPath().toLowerCase();

        //exact match search
        WebLayoutPath layoutPath = ObjectSelect.query(WebLayoutPath.class)
                .where(WebLayoutPath.WEB_SITE_VERSION.eq(webSiteVersionService.getCurrentVersion()))
                .and(WebLayoutPath.PATH.eq(urlPath))
                .and(WebLayoutPath.MATCH_TYPE.eq(RequestMatchType.EXACT))
                .prefetch(WebLayoutPath.WEB_NODE_TYPE.joint())
                .cacheStrategy(QueryCacheStrategy.LOCAL_CACHE)
                .cacheGroup(getCacheGroup())
                .selectFirst(cayenneService.sharedContext());

        if (layoutPath == null) {
            layoutPath = ObjectSelect.query(WebLayoutPath.class)
                .where(WebLayoutPath.WEB_SITE_VERSION.eq(webSiteVersionService.getCurrentVersion()))
                .and(WebLayoutPath.MATCH_TYPE.eq(RequestMatchType.STARTS_WITH))
                .prefetch(WebLayoutPath.WEB_NODE_TYPE.joint())
                .cacheStrategy(QueryCacheStrategy.LOCAL_CACHE)
                .cacheGroup(getCacheGroup())    
                .select(cayenneService.sharedContext())
                .stream()
                .filter(path -> urlPath.startsWith(path.getPath()))
                .max(Comparator.comparing(p -> p.getPath().length()))
                .orElse(null);
        }
        if (layoutPath != null) {
            return layoutPath.getWebNodeType();
        }  else  {
            return getDefaultWebNodeType();
        }
    }
}
