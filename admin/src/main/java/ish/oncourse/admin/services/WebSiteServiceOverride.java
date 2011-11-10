package ish.oncourse.admin.services;

import ish.oncourse.model.College;
import ish.oncourse.model.WebHostName;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.site.IWebSiteService;

public class WebSiteServiceOverride implements IWebSiteService {
	
	public WebSiteServiceOverride() {
		
	}

	@Override
	public WebSite getCurrentWebSite() {
		return null;
	}

	@Override
	public College getCurrentCollege() {
		return null;
	}

	@Override
	public WebHostName getCurrentDomain() {
		return null;
	}
}
