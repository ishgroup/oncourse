package ish.oncourse.ui.pages;

import ish.oncourse.model.Site;
import ish.oncourse.services.sites.ISitesService;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class SiteDetails {

	@Inject
	private Request request;
	
	@Inject 
	private ISitesService sitesService;
	
	@Property
	private Site site;
	
	@SetupRender
	public void beforeRender() {
		String angelId = (String) request.getAttribute("siteId");
		site = sitesService.getSite(Site.ANGEL_ID_PROPERTY, Long.valueOf(angelId));
	}	
	
}
