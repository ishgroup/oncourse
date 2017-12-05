package ish.oncourse.ui.pages;

import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.model.Site;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

public class Sites extends ISHCommon {

	@Property
	private List<Site> sites;
	
	@Property
	private Site site;
	
	@Inject
	private IWebSiteService webSiteService;
	
	@SetupRender
	void beginRender(){
		this.sites = webSiteService.getCurrentCollege().getWebVisibleSites();
	}
}
