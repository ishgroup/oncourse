package ish.oncourse.services.site;

import ish.oncourse.services.site.IWebSiteService;
import java.util.Collections;
import java.util.List;

import ish.oncourse.model.WebBlock;
import ish.oncourse.model.WebSite;

public class MockWebSiteService implements IWebSiteService {

	private String siteCode;

	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
	}

	public List<WebSite> getAvailableSites() {
		return Collections.emptyList();
	}

	public WebSite getCurrentSite() {
		WebSite site = new WebSite();
		site.setCode(siteCode);
		return site;
	}
	
	public List<WebBlock> getActiveBlocks(String regionKey) {
		return Collections.emptyList();
	}

}
