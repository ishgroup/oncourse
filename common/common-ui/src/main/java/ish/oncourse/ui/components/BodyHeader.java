package ish.oncourse.ui.components;

import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.model.College;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class BodyHeader extends ISHCommon {

    @Inject
    private PreferenceController preferenceController;

	@Inject
	private IWebSiteService siteService;

	@Property
	private College college;

	@SetupRender
	void beforeRender() {
		college = siteService.getCurrentCollege();
	}

	public String getCollegeName() {
		return college.getName();
	}

	public String getHomeLink() {
		return "/";
	}

    public boolean isPaymentGatewayEnabled() {
        return preferenceController.isPaymentGatewayEnabled();
    }
}
