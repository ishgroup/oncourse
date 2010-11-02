package ish.oncourse.services.site;

import java.util.Collections;
import java.util.List;

import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Scope;
import org.apache.tapestry5.services.Request;

import ish.oncourse.model.College;
import ish.oncourse.model.Site;
import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebHostName;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.services.persistence.ICayenneService;


@Scope("perthread")
public class WebSiteService implements IWebSiteService {

	private static final String COLLEGE_DOMAIN_CACHE_GROUP = "webhosts";
	private static final String DEFAULT_FOLDER_NAME = "default";
	
	@Inject
	private Request request;

	@Inject
	private ICayenneService cayenneService;

	private transient WebHostName collegeDomain;
	private final Expression activeBlocksNameFilter;
	//TODO commented till the question with the layouts regions will be resolved
	//private final Expression activeBlocksRegionFilter;

	private final static Logger LOGGER = Logger.getLogger(WebSiteService.class);


	public WebSiteService() {

		String activeBlocksNameExp = String.format(
				"%s = $name", WebContent.NAME_PROPERTY);

		activeBlocksNameFilter = Expression.fromString(activeBlocksNameExp);

		//TODO commented till the question with the layouts regions will be resolved
		/*String activeBlocksRegionExp = String.format(
				"%s = $regionKey", WebContent.REGION_KEY_PROPERTY);

		activeBlocksRegionFilter = Expression.fromString(activeBlocksRegionExp);*/
	}

	public WebHostName getCurrentDomain() {

		if (collegeDomain == null) {

			String serverName = request.getServerName().toLowerCase();

			SelectQuery query = new SelectQuery(WebHostName.class);
			query.andQualifier(ExpressionFactory.matchExp(
					WebHostName.NAME_PROPERTY, serverName));

			query.setCacheStrategy(QueryCacheStrategy.LOCAL_CACHE);
			query.setCacheGroups(COLLEGE_DOMAIN_CACHE_GROUP);

			collegeDomain = (WebHostName) DataObjectUtils.objectForQuery(
					cayenneService.sharedContext(), query);


			if (collegeDomain == null) {
				throw new IllegalStateException(
						"Can't determine college domain for server name: '"
						+ serverName + "'");
			}
		}

		if (collegeDomain != null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request server name: " + request.getServerName()
						+ " for Request #" + request.hashCode()
						+ " ; returning domain object with " + collegeDomain.getName());
			}
		}

		return collegeDomain;
	}

	/**
	 * Retrieve the resource folder name.
	 *
	 * <p>The resource folder name is the root folder containing site
	 * specific resources. The name is constructed from the Site key.</p>
	 *
	 * <p>Defaults to the "defaults" folder.</p>
	 *
	 * @return resource folder name
	 */
	public String getResourceFolderName() {
		String folderName = DEFAULT_FOLDER_NAME;

		if (getCurrentDomain() != null) {
			String siteKey = getCurrentDomain().getWebSite().getSiteKey();
			if ((siteKey != null) && !("".equals(siteKey))) {
				folderName = siteKey;
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

	public List<WebContent> getWebBlocksForRegion(String regionKey) {
		//TODO commented till the question with the layouts regions will be resolved
		if(true){
			return null;
		}
		List<WebContent> allBlocks = Collections.emptyList();//getCurrentWebSite().getWebBlocks();
		if (allBlocks.isEmpty()) {
			return null;
		}

		//TODO commented till the question with the layouts regions will be resolved
		//Expression filter = activeBlocksRegionFilter.expWithParameters(
			//	Collections.singletonMap(WebContent.REGION_KEY_PROPERTY, regionKey));

		//allBlocks = filter.filterObjects(allBlocks);

		return (allBlocks.isEmpty()) ? null : allBlocks;
	}

	public WebContent getWebBlockForName(String name) {

		List<WebContent> allBlocks = Collections.emptyList();//getCurrentWebSite().getWebBlocks();
		if (allBlocks.isEmpty()) {
			return null;
		}

		Expression filter = activeBlocksNameFilter.expWithParameters(
				Collections.singletonMap(WebContent.NAME_PROPERTY, name));

		allBlocks = filter.filterObjects(allBlocks);

		// TODO: MSW 2010/06/03 Should we return an exception if more than one
		// WebContent is found? We expect only one web block per site per name.
		return (allBlocks.isEmpty()) ? null : allBlocks.get(0);
	}

	public String getHomeLink() {
		return "/";
	}

	public List<Site> getCollegeSites() {
		return getCurrentCollege().getSites();
	}

	public boolean isCollegePaymentEnabled() {
		return getCurrentCollege().getIsWebSitePaymentsEnabled();
	}
}
