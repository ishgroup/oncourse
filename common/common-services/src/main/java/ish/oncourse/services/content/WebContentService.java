package ish.oncourse.services.content;

import ish.oncourse.model.*;
import ish.oncourse.services.BaseService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteVersionService;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.*;

public class WebContentService extends BaseService<WebContent> implements IWebContentService {

	private static final Logger logger = LogManager.getLogger();

	@Inject
	private ICayenneService cayenneService;
	
	@Inject
	private IWebSiteVersionService webSiteVersionService;

	@Override
	public WebContent getWebContent(String searchProperty, Object value) {
		ObjectSelect<WebContent> query = ObjectSelect.query(WebContent.class).
				and(WebContent.WEB_SITE_VERSION.eq(webSiteVersionService.getCurrentVersion()));
		if (searchProperty != null) {
			query.and(ExpressionFactory.matchExp(
					searchProperty, value));
		}
		return query.selectOne(cayenneService.sharedContext());
	}

	@Override
	public SortedSet<WebContent> getBlocksForRegionKey(WebNodeType webNodeType,
			RegionKey regionKey) {
		assert webNodeType != null;

		if (webNodeType.getObjectId().isTemporary() && !RegionKey.unassigned.equals(regionKey)) {
			throw new IllegalArgumentException("Illegal params for WebContentService#getBlocksForRegionKey() call");
		}

		ObjectSelect<WebContent> query = ObjectSelect.query(WebContent.class)
				.and(WebContent.WEB_SITE_VERSION.eq(webSiteVersionService.getCurrentVersion()))
				.and(ExpressionFactory.matchExp(WebContent.WEB_CONTENT_VISIBILITIES.getName() + "+."
						+ WebContentVisibility.WEB_NODE.getName(), null));

		List<WebContentVisibility> webContentVisibilities = webNodeType
				.getWebContentVisibilities();

		List<Long> nodeIds = new ArrayList<>(webContentVisibilities.size());
		List<Long> visibilityIds = new ArrayList<>(
				webContentVisibilities.size());

		for (WebContentVisibility webContentVisibility : webContentVisibilities) {
			visibilityIds.add(webContentVisibility.getId());
			nodeIds.add(webContentVisibility.getWebContent().getId());
		}

		Expression regionKeyQualifier;
		if (regionKey != null && regionKey != RegionKey.unassigned) {
			regionKeyQualifier = ExpressionFactory.inDbExp(
					WebContent.WEB_CONTENT_VISIBILITIES.getName() + "+."
							+ WebContentVisibility.ID_PK_COLUMN, visibilityIds)
					.andExp(ExpressionFactory
							.matchExp(WebContent.WEB_CONTENT_VISIBILITIES.getName()
											+ "+." + WebContentVisibility.REGION_KEY.getName(),
									regionKey));
		} else {
			regionKeyQualifier = ExpressionFactory.notInDbExp(
					WebContent.ID_PK_COLUMN, nodeIds)
					.andExp(ExpressionFactory
							.matchDbExp(WebContent.WEB_CONTENT_VISIBILITIES
									+ "+." + WebContentVisibility.ID_PK_COLUMN, null));
		}

		query = query.and(regionKeyQualifier);

		TreeSet<WebContent> treeSet = new TreeSet<>(new WebContentComparator(webNodeType));
		treeSet.addAll(query.select(webNodeType.getObjectContext()));
		return treeSet;
	}

	@Override
	public List<WebContent> getBlocks() {
		return ObjectSelect.query(WebContent.class)
				.and(WebContent.WEB_SITE_VERSION.eq(webSiteVersionService.getCurrentVersion()))
				.and(getBlockQualifier())
				.orderBy(WebContent.MODIFIED.desc())
				.select(cayenneService.sharedContext());
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
			logger.error("JS try to set the higher position {} to visibility then list contains {}. Changed to last available position {}.",
					position, contentVisibilities.size(), contentVisibilities.size(), new Exception());
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
			// there cannot be visibility for the unassigned block
			return null;
		}
		List<WebContentVisibility> result = new ArrayList<>();
		WebSiteVersion currentSiteVersion = webSiteVersionService.getCurrentVersion();
		//fill the list of corresponding results
		for (WebContentVisibility visibility : webNodeType.getWebContentVisibilities()) {
			if (currentSiteVersion.getId().equals(visibility.getWebContent().getWebSiteVersion().getId()) 
					&& regionKey.equals(visibility.getRegionKey())) {
				result.add(visibility);
			}
		}
		Collections.sort(result);
		return result;
	}


	@Override
	public WebContent getBlockByName(String webContentName) {
		return ObjectSelect.query(WebContent.class)
				.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE)
				.cacheGroups(WebContent.class.getSimpleName())
				.and(WebContent.WEB_SITE_VERSION.eq(webSiteVersionService.getCurrentVersion()))
				.and(WebContent.NAME.eq(webContentName))
				.and(getBlockQualifier())
				.selectOne(cayenneService.sharedContext());
	}

	private Expression getBlockQualifier() {
		return ExpressionFactory.matchExp(
				WebContent.WEB_CONTENT_VISIBILITIES.getName() + "+."
						+ WebContentVisibility.WEB_NODE.getName(), null)
				.orExp(ExpressionFactory.matchDbExp(
						WebContent.WEB_CONTENT_VISIBILITIES.getName() + "+."
								+ WebContentVisibility.ID_PK_COLUMN, null));
	}

	@Override
	public WebNode getWebNodeByName(String webNodeName) {
		return ObjectSelect.query(WebNode.class)
				.and(WebNode.WEB_SITE_VERSION.eq(webSiteVersionService.getCurrentVersion()))
				.and(WebNode.NAME.eq(webNodeName))
				.selectOne(cayenneService.sharedContext());
	}

	@Override
	public WebNodeType getWebNodeTypeByName(String webNodeTypeName) {
		return ObjectSelect.query(WebNodeType.class)
				.and(WebNodeType.WEB_SITE_VERSION.eq(webSiteVersionService.getCurrentVersion()))
				.and(WebNodeType.NAME.eq(webNodeTypeName)).selectOne(cayenneService.sharedContext());
	}
}
