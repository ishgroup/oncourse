package ish.oncourse.services.site;

import ish.oncourse.model.College;
import ish.oncourse.model.CollegeDomain;
import java.util.Collections;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Scope;

import ish.oncourse.model.WebBlock;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.services.Request;


@Scope("perthread")
public class WebSiteService implements IWebSiteService {

	private static final String COLLEGE_DOMAIN_CACHE_GROUP = "webhosts";
	private static final String DEFAULT_FOLDER_NAME = "default";
	@Inject
	private Request request;
	@Inject
	private ICayenneService cayenneService;
	private transient CollegeDomain collegeDomain;
	private final Expression activeBlocksNameFilter;
	private final Expression activeBlocksRegionFilter;

	public WebSiteService() {

		String activeBlocksNameExp = String.format(
				"%s = $name and (%s = null or %s = false)",
				WebBlock.NAME_PROPERTY,
				WebBlock.IS_DELETED_PROPERTY,
				WebBlock.IS_DELETED_PROPERTY);

		activeBlocksNameFilter = Expression.fromString(activeBlocksNameExp);

		String activeBlocksRegionExp = String.format(
				"%s = $regionKey and (%s = null or %s = false)",
				WebBlock.REGION_KEY_PROPERTY,
				WebBlock.IS_DELETED_PROPERTY,
				WebBlock.IS_DELETED_PROPERTY);

		activeBlocksRegionFilter = Expression.fromString(activeBlocksRegionExp);
	}

	public CollegeDomain getCurrentDomain() {

		if (collegeDomain == null) {

			String serverName = request.getServerName().toLowerCase();

			SelectQuery query = new SelectQuery(CollegeDomain.class);
			query.andQualifier(ExpressionFactory.matchExp(
					CollegeDomain.IS_DELETED_PROPERTY, null));
			query.orQualifier(ExpressionFactory.matchExp(
					CollegeDomain.IS_DELETED_PROPERTY, false));
			query.andQualifier(ExpressionFactory.matchExp(
					CollegeDomain.NAME_PROPERTY, serverName));

			query.setCacheStrategy(QueryCacheStrategy.LOCAL_CACHE);
			query.setCacheGroups(COLLEGE_DOMAIN_CACHE_GROUP);

			collegeDomain = (CollegeDomain) DataObjectUtils.objectForQuery(
					cayenneService.sharedContext(), query);

			if (collegeDomain == null) {
				throw new IllegalStateException(
						"Can't determine college domain for server name: '"
						+ serverName + "'");
			}
		}

		return collegeDomain;
	}

	/**
	 * Retrieve the resource folder name.
	 *
	 * <p>The resource folder name is the root folder containing college/site
	 * specific resources. The name is constructed from the College key and the
	 * Site key (where applicable).</p>
	 *
	 * <p>Defaults to the "defaults" folder.</p>
	 *
	 * @return resource folder name
	 */
	public String getResourceFolderName() {
		String folderName = DEFAULT_FOLDER_NAME;

		if (getCurrentDomain() != null) {
			folderName = getCurrentDomain().getCollege().getCollegeKey();
			String siteKey = getCurrentDomain().getWebSite().getSiteKey();
			if ((siteKey != null) && !("".equals(siteKey))) {
				folderName += "_" + siteKey;
			}
		}

		return folderName;
	}

	public WebSite getCurrentWebSite() {
		return getCurrentDomain().getWebSite();
	}

	public College getCurrentCollege() {
		return getCurrentDomain().getCollege();
	}

	public List<WebSite> getAvailableSites() {
		return getCurrentCollege().getWebSites();
	}

	public List<WebBlock> getWebBlocksForRegion(String regionKey) {

		List<WebBlock> allBlocks = getCurrentWebSite().getWebBlocks();
		if (allBlocks.isEmpty()) {
			return null;
		}

		Expression filter = activeBlocksRegionFilter.expWithParameters(
				Collections.singletonMap(WebBlock.REGION_KEY_PROPERTY, regionKey));

		allBlocks = filter.filterObjects(allBlocks);

		return (allBlocks.isEmpty()) ? null : allBlocks;
	}

	public WebBlock getWebBlockForName(String name) {

		List<WebBlock> allBlocks = getCurrentWebSite().getWebBlocks();
		if (allBlocks.isEmpty()) {
			return null;
		}

		Expression filter = activeBlocksNameFilter.expWithParameters(
				Collections.singletonMap(WebBlock.NAME_PROPERTY, name));

		allBlocks = filter.filterObjects(allBlocks);

		// TODO: MSW 2010/06/03 Should we return an exception if more than one
		// WebBlock is found? We expect only one web block per site per name.
		return (allBlocks.isEmpty()) ? null : allBlocks.get(0);
	}

	public String getHomeLink() {
		return request.getContextPath() + "/";
	}
}
