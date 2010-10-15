package ish.oncourse.services.site;

import ish.oncourse.model.College;
import ish.oncourse.model.Site;
import ish.oncourse.model.WebHostName;

import java.util.Collections;
import java.util.List;

import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebSite;

public class MockWebSiteService implements IWebSiteService {

	private String siteCode;

	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
	}

	public List<WebSite> getAvailableSites() {
		return Collections.emptyList();
	}

	public WebSite getCurrentWebSite() {
		WebSite site = new WebSite();
		site.setSiteKey(siteCode);
		return site;
	}
	
	public List<WebContent> getActiveBlocks(String regionKey) {
		return Collections.emptyList();
	}

	public College getCurrentCollege() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public WebHostName getCurrentDomain() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public String getResourceFolderName() {
		return getCurrentWebSite().getSiteKey();
	}

	public List<WebContent> getWebBlocksForRegion(String regionKey) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public WebContent getWebBlockForName(String name) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public String getHomeLink() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public List<Site> getCollegeSites() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean isCollegePaymentEnabled() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
