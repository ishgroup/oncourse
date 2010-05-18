package ish.oncourse.services.site;

import java.util.List;

import ish.oncourse.model.WebBlock;
import ish.oncourse.model.WebSite;

public interface IWebSiteService {

	List<WebSite> getAvailableSites();

	WebSite getCurrentSite();

	List<WebBlock> getActiveBlocks(String regionKey);
}
