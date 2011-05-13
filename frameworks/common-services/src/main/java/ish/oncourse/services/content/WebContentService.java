package ish.oncourse.services.content;

import ish.oncourse.model.RegionKey;
import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.BaseService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.tapestry5.ioc.annotations.Inject;

public class WebContentService extends BaseService<WebContent> implements
		IWebContentService {

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

	public SortedSet<WebContent> getBlocksForRegionKey(final WebNodeType webNodeType,
			RegionKey regionKey) {
		

		SelectQuery q = new SelectQuery(WebContent.class);

		q.andQualifier(ExpressionFactory.matchExp(WebContent.WEB_SITE_PROPERTY,
				webSiteService.getCurrentWebSite()));
		
		if(webNodeType.getPersistenceState() == PersistenceState.NEW){
			if(regionKey!=null){
			return new TreeSet<WebContent>();
			}else{
				return new TreeSet<WebContent>(cayenneService.sharedContext()
						.performQuery(q));
			}
		}
		
		
		List<WebContentVisibility> webContentVisibilities = webNodeType.getWebContentVisibilities();
		List<Long>ids=new ArrayList<Long>(webContentVisibilities.size());
		for(WebContentVisibility webContentVisibility:webContentVisibilities){
			ids.add((Long) webContentVisibility.getObjectId().getIdSnapshot().get(WebContentVisibility.ID_PK_COLUMN));
		}
		Expression regionKeyQualifier = ExpressionFactory.inDbExp(
				WebContent.WEB_CONTENT_VISIBILITIES_PROPERTY+"."+WebContentVisibility.ID_PK_COLUMN,
				ids);
		if (regionKey != null) {
			regionKeyQualifier = regionKeyQualifier.andExp(ExpressionFactory
					.matchExp(WebContent.WEB_CONTENT_VISIBILITIES_PROPERTY+ "."
							+ WebContentVisibility.REGION_KEY_PROPERTY,
							regionKey));
		}else{
			regionKeyQualifier = regionKeyQualifier.notExp();
		}
		q.andQualifier(regionKeyQualifier);
		TreeSet<WebContent> treeSet = new TreeSet<WebContent>(new Comparator<WebContent>() {

			@Override
			public int compare(WebContent o1, WebContent o2) {
				WebContentVisibility visibility1 = o1.getWebContentVisibility(webNodeType);
				WebContentVisibility visibility2 = o2.getWebContentVisibility(webNodeType);
				int result = 0;
				if (visibility1 != null && visibility2 != null) {
					result = visibility2.getWeight()
							- visibility1.getWeight();
				}
				if (result != 0) {
					return result;
				}
				if (o1.getName() != null && o2.getName() != null) {
					result = o1.getName().compareTo(o2.getName());
				}
				if (result != 0) {
					return result;
				}
				if (o1.getContent() != null && o2.getContent() != null) {
					result = o1.getContent().compareTo(o2.getContent());
				}
				if (result != 0) {
					return result;
				}
				result = o1.getId().compareTo(o2.getId());
				if (result != 0) {
					return result;
				}
				return o1.hashCode() - o2.hashCode();
			}
		});
		treeSet.addAll(webNodeType.getObjectContext()
				.performQuery(q));
		return treeSet;
	}

	public List<WebContent> getBlocks() {

		SelectQuery q = new SelectQuery(WebContent.class);
		q.andQualifier(ExpressionFactory.matchExp(WebContent.WEB_SITE_PROPERTY,
				webSiteService.getCurrentWebSite()));

		Expression expr = ExpressionFactory.matchExp(
				WebContent.WEB_CONTENT_VISIBILITIES_PROPERTY + "+."
						+ WebContentVisibility.WEB_NODE_PROPERTY, null);

		expr = expr.orExp(ExpressionFactory.matchExp(
				WebContent.WEB_CONTENT_VISIBILITIES_PROPERTY, null));

		q.andQualifier(expr);

		q.addOrdering(new Ordering(WebContent.MODIFIED_PROPERTY,
				SortOrder.DESCENDING));

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
