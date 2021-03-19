package ish.oncourse.ui.components;

import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.model.College;
import ish.oncourse.model.WebMenu;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.services.menu.IWebMenuService;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.ioc.annotations.Inject;

@SupportsInformalParameters
public class BodyStructure extends ISHCommon {

	@Inject
	private IWebMenuService webMenuService;
	@Inject
	private IWebSiteService siteService;
	
	@Parameter
	@Property
	private WebNodeType webNodeType;
	
	@Property
	private College college;

	@SetupRender
	void beforeRender() {
		college = siteService.getCurrentCollege();
	}

	public String getCollegeName() {
		return college.getName();
	}

    public WebMenu getRootMenu() {
		return webMenuService.getRootMenu();
	}

    /**
     * Not used after confirmation dialog move to ConfirmOrderDialog component.
     * Should be removed after all live customizations will be cleaned.
     * @return
     */
    @Deprecated
    public boolean isPaymentGatewayEnabled() {
        return false;//preferenceController.isPaymentGatewayEnabled();
    }
}
