package ish.oncourse.services.content;

import ish.oncourse.model.RegionKey;
import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebContentComparator;
import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.BaseService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.ArrayList;
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

		Expression qualifier = ExpressionFactory.matchExp(WebContent.WEB_SITE_PROPERTY, currentSite);

		if (searchProperty != null) {
			qualifier = qualifier.andExp(ExpressionFactory.matchExp(searchProperty, value));
		}

		@SuppressWarnings("unchecked")
		List<WebContent> listResult = findByQualifier(qualifier);

		return !listResult.isEmpty() ? listResult.get(new Random().nextInt(listResult.size())) : null;
	}

	public SortedSet<WebContent> getBlocksForRegionKey(WebNodeType webNodeType, RegionKey regionKey) {

		SelectQuery q = new SelectQuery(WebContent.class);

		Expression siteQualifier = ExpressionFactory.matchExp(WebContent.WEB_SITE_PROPERTY, webSiteService.getCurrentWebSite());
		q.andQualifier(siteQualifier);

		List<WebContentVisibility> webContentVisibilities = webNodeType.getWebContentVisibilities();
		List<Long> ids = new ArrayList<Long>(webContentVisibilities.size());
		for (WebContentVisibility webContentVisibility : webContentVisibilities) {
			ids.add((Long) webContentVisibility.getObjectId().getIdSnapshot().get(WebContentVisibility.ID_PK_COLUMN));
		}

		Expression regionKeyQualifier = ExpressionFactory.inDbExp(WebContent.WEB_CONTENT_VISIBILITIES_PROPERTY + "+."
				+ WebContentVisibility.ID_PK_COLUMN, ids);
		if (regionKey != null && regionKey != RegionKey.unassigned) {
			regionKeyQualifier = regionKeyQualifier.andExp(ExpressionFactory.matchExp(WebContent.WEB_CONTENT_VISIBILITIES_PROPERTY + "."
					+ WebContentVisibility.REGION_KEY_PROPERTY, regionKey));
		} else {
			regionKeyQualifier = regionKeyQualifier.notExp().orExp(
					ExpressionFactory.matchDbExp(WebContent.WEB_CONTENT_VISIBILITIES_PROPERTY + "+." + WebContentVisibility.ID_PK_COLUMN,
							null));

		}

		q.andQualifier(regionKeyQualifier);
		TreeSet<WebContent> treeSet = new TreeSet<WebContent>(new WebContentComparator(webNodeType));
		treeSet.addAll(webNodeType.getObjectContext().performQuery(q));

		return treeSet;
	}

	public List<WebContent> getBlocks() {

		SelectQuery q = new SelectQuery(WebContent.class);
		q.andQualifier(ExpressionFactory.matchExp(WebContent.WEB_SITE_PROPERTY, webSiteService.getCurrentWebSite()));

		Expression expr = ExpressionFactory.matchExp(WebContent.WEB_CONTENT_VISIBILITIES_PROPERTY + "+."
				+ WebContentVisibility.WEB_NODE_PROPERTY, null);

		expr = expr.orExp(ExpressionFactory.matchExp(WebContent.WEB_CONTENT_VISIBILITIES_PROPERTY, null));

		q.andQualifier(expr);

		q.addOrdering(new Ordering(WebContent.MODIFIED_PROPERTY, SortOrder.DESCENDING));

		return cayenneService.sharedContext().performQuery(q);

	}

	public SortedSet<WebContentVisibility> getBlockVisibilityForRegionKey(WebNodeType webNodeType, RegionKey regionKey) {

		if (regionKey == null && regionKey == RegionKey.unassigned) {
			//there con't be visibility for the unassigned block
			return null;
		}
		SelectQuery q = new SelectQuery(WebContentVisibility.class);

		q.andQualifier(ExpressionFactory.matchExp(WebContentVisibility.WEB_CONTENT_PROPERTY + "." + WebContent.WEB_SITE_PROPERTY,
				webSiteService.getCurrentWebSite()));

		q.andQualifier(ExpressionFactory.matchExp(WebContentVisibility.WEB_NODE_TYPE_PROPERTY, webNodeType));
		q.andQualifier(ExpressionFactory.matchExp(WebContentVisibility.REGION_KEY_PROPERTY, regionKey));

		return new TreeSet<WebContentVisibility>(webNodeType.getObjectContext().performQuery(q));
	}
}
