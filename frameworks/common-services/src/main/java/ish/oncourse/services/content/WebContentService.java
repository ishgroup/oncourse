package ish.oncourse.services.content;

import ish.oncourse.model.RegionKey;
import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.BaseService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.tapestry5.ioc.annotations.Inject;

public class WebContentService extends BaseService<WebContent> implements IWebContentService {

    @Inject
    private IWebSiteService webSiteService;

    @Inject
    private ICayenneService cayenneService;

    public WebContent getWebContent(String searchProperty, Object value) {
        WebSite currentSite = webSiteService.getCurrentWebSite();

        ObjectContext sharedContext = cayenneService.sharedContext();

        Expression qualifier = ExpressionFactory.matchExp(
                WebContent.WEB_SITE_PROPERTY, currentSite);

        if (searchProperty != null) {
            qualifier = qualifier.andExp(ExpressionFactory.matchExp(
                    searchProperty, value));
        }

        @SuppressWarnings("unchecked")
        List<WebContent> listResult = findByQualifier(qualifier);

        return !listResult.isEmpty() ? listResult.get(new Random()
                .nextInt(listResult.size())) : null;
    }

    public SortedSet<WebContent> getBlocksForRegionKey(RegionKey regionKey) {

        SelectQuery q = new SelectQuery(WebContent.class);

        q.andQualifier(ExpressionFactory.matchExp(WebContent.WEB_SITE_PROPERTY,
                webSiteService.getCurrentWebSite()));

        q.andQualifier(ExpressionFactory.matchExp(
                WebContent.WEB_CONTENT_VISIBILITY_PROPERTY + "."
                        + WebContentVisibility.REGION_KEY_PROPERTY, regionKey));

        return new TreeSet<WebContent>(cayenneService.sharedContext()
                .performQuery(q));
    }
    
	public List<WebContent> getBlocks() {

		SelectQuery q = new SelectQuery(WebContent.class);
		q.andQualifier(ExpressionFactory.matchExp(WebContent.WEB_SITE_PROPERTY,
				webSiteService.getCurrentWebSite()));

		Expression expr = ExpressionFactory.matchExp(WebContent.WEB_CONTENT_VISIBILITY_PROPERTY
				+ "+." + WebContentVisibility.WEB_NODE_PROPERTY, null);

		expr = expr.orExp(ExpressionFactory.matchExp(WebContent.WEB_CONTENT_VISIBILITY_PROPERTY,
				null));

		q.andQualifier(expr);

		q.addOrdering(new Ordering(WebContent.MODIFIED_PROPERTY, SortOrder.DESCENDING));

		return cayenneService.sharedContext().performQuery(q);

	}

    public SortedSet<WebContentVisibility> getBlockVisibilityForRegionKey(
            RegionKey regionKey) {

        SelectQuery q = new SelectQuery(WebContentVisibility.class);

        q.andQualifier(ExpressionFactory.matchExp(
                WebContentVisibility.WEB_CONTENT_PROPERTY + "."
                        + WebContent.WEB_SITE_PROPERTY,
                webSiteService.getCurrentWebSite()));

        q.andQualifier(ExpressionFactory.matchExp(
                WebContentVisibility.REGION_KEY_PROPERTY, regionKey));

        return new TreeSet<WebContentVisibility>(cayenneService.sharedContext()
                .performQuery(q));
    }
}
