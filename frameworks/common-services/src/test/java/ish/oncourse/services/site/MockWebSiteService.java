package ish.oncourse.services.site;

import ish.oncourse.model.College;
import ish.oncourse.model.CollegeDomain;
import ish.oncourse.model.Site;

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

	public WebSite getCurrentWebSite() {
		WebSite site = new WebSite();
		site.setCode(siteCode);
		return site;
	}
	
	public List<WebBlock> getActiveBlocks(String regionKey) {
		return Collections.emptyList();
	}

	public College getCurrentCollege() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public CollegeDomain getCurrentDomain() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public String getResourceFolderName() {
		return getCurrentWebSite().getCode();
	}

	public List<WebBlock> getWebBlocksForRegion(String regionKey) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public WebBlock getWebBlockForName(String name) {
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
