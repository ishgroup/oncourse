package ish.oncourse.admin.services.website;

import ish.oncourse.admin.utils.LicenseFeeUtil;
import ish.oncourse.model.*;
import ish.oncourse.services.node.IWebNodeService;
import org.apache.cayenne.ObjectContext;

import java.util.Date;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class CreateDefaultWebSiteStructure {
    public final static String DEFAULT_HOME_PAGE_NAME = "Home page";

    private IWebNodeService webNodeService;
    private WebSiteVersion stagedVersion;
    private ObjectContext context;

    public void create() {
        Date now = new Date();
        WebSiteLayout webSiteLayout = context.newObject(WebSiteLayout.class);
        webSiteLayout.setLayoutKey(WebNodeType.DEFAULT_LAYOUT_KEY);
        webSiteLayout.setWebSiteVersion(stagedVersion);

        WebNodeType page = context.newObject(WebNodeType.class);
        page.setName(WebNodeType.PAGE);
        page.setCreated(now);
        page.setModified(now);
        page.setWebSiteLayout(webSiteLayout);
        page.setWebSiteVersion(stagedVersion);

        WebNode node = webNodeService.createNewNodeBy(stagedVersion, page, DEFAULT_HOME_PAGE_NAME, DEFAULT_HOME_PAGE_NAME, 1);
        node.setPublished(true);

        WebMenu menu = context.newObject(WebMenu.class);
        menu.setName("Home");
        menu.setCreated(now);
        menu.setModified(now);
        menu.setWebSiteVersion(stagedVersion);
        menu.setWeight(1);
        menu.setWebNode(node);

        WebSite webSite = stagedVersion.getWebSite();
        College college = webSite.getCollege();
        LicenseFeeUtil.createFee(context, college, webSite, LicenseFeeUtil.HOSTING_FEE_CODE);
        LicenseFeeUtil.createFee(context, college, webSite, LicenseFeeUtil.CC_WEB_FEE_CODE);
        LicenseFeeUtil.createFee(context, college, webSite, LicenseFeeUtil.ECOMMERCE_FEE_CODE);
        context.commitChanges();

        WebUrlAlias urlAlias = context.newObject(WebUrlAlias.class);
        urlAlias.setWebSiteVersion(stagedVersion);
        urlAlias.setUrlPath("/");
        urlAlias.setWebNode(node);
        urlAlias.setDefault(true);
        context.commitChanges();
    }

    public static CreateDefaultWebSiteStructure valueOf(WebSiteVersion stagedVersion, ObjectContext context, IWebNodeService webNodeService) {
        CreateDefaultWebSiteStructure result = new CreateDefaultWebSiteStructure();
        result.stagedVersion = stagedVersion;
        result.context = context;
        result.webNodeService = webNodeService;
        return result;
    }
}
