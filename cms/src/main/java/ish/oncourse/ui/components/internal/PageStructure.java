package ish.oncourse.ui.components.internal;

import ish.oncourse.cms.components.CmsNavigation;
import ish.oncourse.cms.services.access.IAuthenticationService;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;


/**
 * IMPORTANT NOTE: ADD ONLY CMS DEPENDENT FUNCTIONALITY TO THE CLASS ALL COMMON FUNCTIONALITY SHOULD BE ADDED TO APageStructure
 */
public class PageStructure extends APageStructure {

    @Inject
    private IAuthenticationService authenticationService;

    @InjectComponent
    @Property
    private CmsNavigation cmsNavigation;

    @Property
    @Inject
    private IWebSiteService webSiteService;


    public boolean isLoggedIn() {
        return authenticationService.getUser() != null || authenticationService.getSystemUser() != null;
    }

    public String getFace()
    {
        return "xmlns:fb=\"http://www.facebook.com/2008/fbml\"";
    }

}
