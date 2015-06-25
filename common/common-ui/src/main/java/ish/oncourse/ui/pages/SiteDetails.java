package ish.oncourse.ui.pages;

import ish.oncourse.components.ISHCommon;
import ish.oncourse.model.Site;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class SiteDetails extends ISHCommon {

	@Inject
	private Request request;
	
	@Property
	private Site site;
	
	@SetupRender
	public void beforeRender() {
		site = (Site) request.getAttribute(Site.class.getSimpleName());
	}	
	
}
