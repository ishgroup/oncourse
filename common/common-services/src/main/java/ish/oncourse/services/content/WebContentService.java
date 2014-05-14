package ish.oncourse.services.content;

import ish.oncourse.model.*;
import ish.oncourse.services.BaseService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.*;

public class WebContentService extends BaseService<WebContent> implements IWebContentService {

	private static final Logger LOGGER = Logger.getLogger(BaseService.class);

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ICayenneService cayenneService;

	@Override
	public WebContent getWebContent(String searchProperty, Object value) {
		WebSite currentSite = webSiteService.getCurrentWebSite();
		Expression qualifier = ExpressionFactory.matchExp(
				WebContent.WEB_SITE_PROPERTY, currentSite);
		if (searchProperty != null) {
			qualifier = qualifier.andExp(ExpressionFactory.matchExp(
					searchProperty, value));
		}
		
		SelectQuery query = new SelectQuery(WebContent.class, qualifier);
		List<WebContent> results = cayenneService.sharedContext().performQuery(query);
		
		if (!results.isEmpty()) {
			return results.get(0);
		}

		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public SortedSet<WebContent> getBlocksForRegionKey(WebNodeType webNodeType,
			RegionKey regionKey) {
		if (webNodeType != null && webNodeType.getObjectId().isTemporary() && !RegionKey.unassigned.equals(regionKey)) {
			throw new IllegalArgumentException("Illegal params for WebContentService#getBlocksForRegionKey() call");
		}
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
		List<Long> nodeIds = new ArrayList<>(webContentVisibilities.size());
		List<Long> visibilityIds = new ArrayList<>(
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
		TreeSet<WebContent> treeSet = new TreeSet<>(new WebContentComparator(webNodeType));
		treeSet.addAll(webNodeType.getObjectContext().performQuery(q));
		return treeSet;
	}

	@Override
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
	
	@Override
	public void putWebContentVisibilityToPosition(WebNodeType webNodeType, RegionKey regionKey, WebContentVisibility webContentVisibility, 
		int position) {
		if (regionKey == null || regionKey == RegionKey.unassigned) {
			//unassigned region have no ordering
			return;
		}
		List<WebContentVisibility> contentVisibilities = getBlockVisibilityForRegionKey(webNodeType, regionKey);
		if (contentVisibilities.isEmpty()) {
			webContentVisibility.setRegionKey(regionKey);
			webContentVisibility.setWeight(position);
			return;
		}
		if (position > contentVisibilities.size()) {
			LOGGER.error(String.format("JS try to set the higher position %s to visibility then list contains %s. Changed to last available position %s.", 
				position, contentVisibilities.size(), contentVisibilities.size()), new Exception());
			position = contentVisibilities.size();
		}
		
		int oldVisibilityPosition = contentVisibilities.indexOf(webContentVisibility);
		if (oldVisibilityPosition == -1) {
			//not linked before
			webContentVisibility.setRegionKey(regionKey);
			contentVisibilities.add(position, webContentVisibility);
		} else {
			if (position == oldVisibilityPosition) {
				//nothing to do
				return;
			}
			if (position > oldVisibilityPosition) {
				//shift down
				contentVisibilities.add(position + 1, webContentVisibility);
				contentVisibilities.remove(oldVisibilityPosition);
			} else {
				//shift up
				contentVisibilities.remove(oldVisibilityPosition);
				contentVisibilities.add(position, webContentVisibility);
			}
		}
		//re-weight the elements
		for (int i = 0; i < contentVisibilities.size() ; i++) {
			WebContentVisibility visibility = contentVisibilities.get(i);
			visibility.setWeight(i);
		}
	}
	
	@Override
	public List<WebContentVisibility> getBlockVisibilityForRegionKey(WebNodeType webNodeType, RegionKey regionKey) {
		if (regionKey == null || regionKey == RegionKey.unassigned) {
			// there con't be visibility for the unassigned block
			return null;
		}
		List<WebContentVisibility> result = new ArrayList<>();
		WebSite currentWebSite = webSiteService.getCurrentWebSite();
		//fill the list of corresponding results
		for (WebContentVisibility visibility : webNodeType.getWebContentVisibilities()) {
			if (currentWebSite.getId().equals(visibility.getWebContent().getWebSite().getId()) && regionKey.equals(visibility.getRegionKey())) {
				result.add(visibility);
			}
		}
		Collections.sort(result);
		return result;
	}


	@Override
		 public WebContent getBlockByName(String webContentName) {
		WebContent webContent = null;
		SelectQuery selectQuery = new SelectQuery(WebContent.class);
		selectQuery.andQualifier(ExpressionFactory.matchExp(WebContent.WEB_SITE_PROPERTY,
				webSiteService.getCurrentWebSite()));
		Expression expression = ExpressionFactory.matchExp(
				WebContent.WEB_CONTENT_VISIBILITIES_PROPERTY + "+."
						+ WebContentVisibility.WEB_NODE_PROPERTY, null);
		expression = expression.orExp(ExpressionFactory.matchDbExp(
				WebContent.WEB_CONTENT_VISIBILITIES_PROPERTY + "+."
						+ WebContentVisibility.ID_PK_COLUMN, null));
		selectQuery.andQualifier(expression);
		selectQuery.andQualifier(ExpressionFactory.matchExp(WebContent.NAME_PROPERTY, webContentName));
		List<WebContent> list = cayenneService.sharedContext().performQuery(selectQuery);
		if(!list.isEmpty()){
			webContent=list.get(0);
		}
		return webContent;
	}

	@Override
	public WebNode getWebNodeByName(String webNodeName) {
		WebNode webNode = null;
		SelectQuery selectQuery = new SelectQuery(WebNode.class);
		selectQuery.andQualifier(ExpressionFactory.matchExp(WebNode.WEB_SITE_PROPERTY,
				webSiteService.getCurrentWebSite()));
		selectQuery.andQualifier(ExpressionFactory.matchExp(WebNode.NAME_PROPERTY, webNodeName));
		List<WebNode> list = cayenneService.sharedContext().performQuery(selectQuery);
		if(!list.isEmpty()){
			webNode=list.get(0);
		}
		return webNode;
	}
}
