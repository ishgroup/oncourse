package ish.oncourse.ui.components;

import ish.oncourse.components.ISHCommon;
import ish.oncourse.services.datalayer.DataLayerFactory;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.ui.components.internal.DataLayer;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Created by pavel on 3/16/17.
 */
public class GoogleTagmanagerNoscript extends ISHCommon {
    @Inject
    private IWebSiteService siteService;

    public String getAccount() {
        String account = siteService.getCurrentWebSite().getGoogleTagmanagerAccount();
        return (StringUtils.trimToNull(account) == null) ? null : account.trim();
    }
}
