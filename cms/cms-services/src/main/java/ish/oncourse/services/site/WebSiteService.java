package ish.oncourse.services.site;

import java.util.Collections;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Scope;

import ish.oncourse.model.WebBlock;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.college.ICollegeService;
import ish.oncourse.services.host.IWebHostNameService;

@Scope("perthread")
public class WebSiteService implements IWebSiteService {

	@Inject
	private ICollegeService collegeService;

	@Inject
	private IWebHostNameService webHostNameService;

	private final Expression activeBlocksFilter;

	public WebSiteService() {

		String activeBlocksExp = String.format(
				"%s = $regionKey and (%s = null or %s = false)",
				WebBlock.REGION_KEY_PROPERTY, WebBlock.DELETED_PROPERTY,
				WebBlock.DELETED_PROPERTY);

		activeBlocksFilter = Expression.fromString(activeBlocksExp);
	}

	public List<WebSite> getAvailableSites() {
		return collegeService.getCurrentCollege().getSites();
	}

	public WebSite getCurrentSite() {
		// TODO: andrus Nov 14, 2009, this does not take into account child
		// sites
		return webHostNameService.getCurrentWebHostName().getSite();
	}

	public List<WebBlock> getActiveBlocks(String regionKey) {

		if (regionKey == null) {
			throw new NullPointerException("Null region");
		}

		List<WebBlock> allBlocks = getCurrentSite().getBlocks();
		if (allBlocks.isEmpty()) {
			return allBlocks;
		}

		Expression filter = activeBlocksFilter.expWithParameters(Collections
				.singletonMap("regionKey", regionKey));

		return filter.filterObjects(allBlocks);
	}
}
