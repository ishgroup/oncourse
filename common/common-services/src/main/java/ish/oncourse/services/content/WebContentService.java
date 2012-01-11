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
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.EJBQLQuery;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

public class WebContentService extends BaseService<WebContent> implements
		IWebContentService {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(BaseService.class);

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ICayenneService cayenneService;

	public WebContent getWebContent(String searchProperty, Object value) {
		WebSite currentSite = webSiteService.getCurrentWebSite();
		Expression qualifier = ExpressionFactory.matchExp(
				WebContent.WEB_SITE_PROPERTY, currentSite);
		if (searchProperty != null) {
			qualifier = qualifier.andExp(ExpressionFactory.matchExp(
					searchProperty, value));
		}

		WebContent result = findRandomWebContentByQualifier(qualifier);

		return result;
	}

	@SuppressWarnings("unchecked")
	public SortedSet<WebContent> getBlocksForRegionKey(WebNodeType webNodeType,
			RegionKey regionKey) {

		SelectQuery q = new SelectQuery(WebContent.class);

		Expression siteQualifier = ExpressionFactory.matchExp(
				WebContent.WEB_SITE_PROPERTY,
				webSiteService.getCurrentWebSite());
		q.andQualifier(siteQualifier);
		q.andQualifier(ExpressionFactory.matchExp(
				WebContent.WEB_CONTENT_VISIBILITIES_PROPERTY + "+."
						+ WebContentVisibility.WEB_NODE_PROPERTY, null));

		List<WebContentVisibility> webContentVisibilities = webNodeType
				.getWebContentVisibilities();
		List<Long> nodeIds = new ArrayList<Long>(webContentVisibilities.size());
		List<Long> visibilityIds = new ArrayList<Long>(
				webContentVisibilities.size());
		for (WebContentVisibility webContentVisibility : webContentVisibilities) {
			visibilityIds.add(webContentVisibility.getId());
			nodeIds.add(webContentVisibility.getWebContent().getId());
		}

		Expression regionKeyQualifier = null;
		if (regionKey != null && regionKey != RegionKey.unassigned) {
			regionKeyQualifier = ExpressionFactory.inDbExp(
					WebContent.WEB_CONTENT_VISIBILITIES_PROPERTY + "+."
							+ WebContentVisibility.ID_PK_COLUMN, visibilityIds);
			regionKeyQualifier = regionKeyQualifier.andExp(ExpressionFactory
					.matchExp(WebContent.WEB_CONTENT_VISIBILITIES_PROPERTY
							+ "+." + WebContentVisibility.REGION_KEY_PROPERTY,
							regionKey));
		} else {
			regionKeyQualifier = ExpressionFactory.notInDbExp(
					WebContent.ID_PK_COLUMN, nodeIds);
			regionKeyQualifier = regionKeyQualifier.orExp(ExpressionFactory
					.matchDbExp(WebContent.WEB_CONTENT_VISIBILITIES_PROPERTY
							+ "+." + WebContentVisibility.ID_PK_COLUMN, null));
		}

		q.andQualifier(regionKeyQualifier);
		TreeSet<WebContent> treeSet = new TreeSet<WebContent>(
				new WebContentComparator(webNodeType));
		treeSet.addAll(webNodeType.getObjectContext().performQuery(q));

		return treeSet;
	}

	@SuppressWarnings("unchecked")
	public List<WebContent> getBlocks() {

		SelectQuery q = new SelectQuery(WebContent.class);
		q.andQualifier(ExpressionFactory.matchExp(WebContent.WEB_SITE_PROPERTY,
				webSiteService.getCurrentWebSite()));

		Expression expr = ExpressionFactory.matchExp(
				WebContent.WEB_CONTENT_VISIBILITIES_PROPERTY + "+."
						+ WebContentVisibility.WEB_NODE_PROPERTY, null);

		expr = expr.orExp(ExpressionFactory.matchDbExp(
				WebContent.WEB_CONTENT_VISIBILITIES_PROPERTY + "+."
						+ WebContentVisibility.ID_PK_COLUMN, null));

		q.andQualifier(expr);

		q.addOrdering(new Ordering(WebContent.MODIFIED_PROPERTY,
				SortOrder.DESCENDING));

		return cayenneService.sharedContext().performQuery(q);

	}

	@SuppressWarnings("unchecked")
	public SortedSet<WebContentVisibility> getBlockVisibilityForRegionKey(
			WebNodeType webNodeType, RegionKey regionKey) {

		if (regionKey == null && regionKey == RegionKey.unassigned) {
			// there con't be visibility for the unassigned block
			return null;
		}
		if (webNodeType != null && webNodeType.getObjectId().isTemporary()) {
			//return no visible content for temporary webNodeType
			return new TreeSet<WebContentVisibility>();
		}
		SelectQuery q = new SelectQuery(WebContentVisibility.class);

		q.andQualifier(ExpressionFactory.matchExp(
				WebContentVisibility.WEB_CONTENT_PROPERTY + "."
						+ WebContent.WEB_SITE_PROPERTY,
				webSiteService.getCurrentWebSite()));

		q.andQualifier(ExpressionFactory.matchExp(
				WebContentVisibility.WEB_NODE_TYPE_PROPERTY, webNodeType));
		q.andQualifier(ExpressionFactory.matchExp(
				WebContentVisibility.REGION_KEY_PROPERTY, regionKey));

		return new TreeSet<WebContentVisibility>(webNodeType.getObjectContext()
				.performQuery(q));
	}

	public WebContent findRandomWebContentByQualifier(Expression qualifier) {

		ObjectContext sharedContext = cayenneService.sharedContext();

		EJBQLQuery q = new EJBQLQuery(
				"select count(i) from WebContent i where "
						+ qualifier.toEJBQL("i"));

		Long count = (Long) sharedContext.performQuery(q).get(0);

		WebContent randomResult = null;

		int attempt = 0;

		if (count != null && count > 0) {
			while (randomResult == null && attempt++ < 5) {
				int random = new Random().nextInt(count.intValue());

				SelectQuery query = new SelectQuery(WebContent.class, qualifier);
				query.setFetchOffset(random);
				query.setFetchLimit(1);
				randomResult = (WebContent) Cayenne.objectForQuery(
						sharedContext, query);
			}
		}

		return randomResult;
	}
}
