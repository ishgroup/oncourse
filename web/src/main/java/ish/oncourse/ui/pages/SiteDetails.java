package ish.oncourse.ui.pages;

import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.model.Site;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

public class SiteDetails extends ISHCommon {
	@Property
	private Site site;
	
	@SetupRender
	public void beforeRender() {
		site = (Site) request.getAttribute(Site.class.getSimpleName());
	}	
	
}
