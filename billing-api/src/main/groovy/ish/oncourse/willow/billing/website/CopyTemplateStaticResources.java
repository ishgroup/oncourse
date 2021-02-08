package ish.oncourse.willow.billing.website;

import ish.oncourse.model.WebSite;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class CopyTemplateStaticResources {
    private static final Logger logger = LogManager.getLogger();

    private File sRootDir;

    private WebSite template;
    private WebSite webSite;

    public boolean copy() {
        File templateDir = new File(sRootDir, template.getSiteKey());
        File webSiteDir = new File(sRootDir, webSite.getSiteKey());

        try {
            FileUtils.copyDirectory(templateDir, webSiteDir, true);
            return true;
        } catch (IOException e) {
            logger.error("Cannot copy {} to {}", templateDir, webSiteDir, e);
            return false;
        }
    }

    public static CopyTemplateStaticResources valueOf(WebSite template, WebSite webSite, File sRootDir) {
        CopyTemplateStaticResources result = new CopyTemplateStaticResources();
        result.template = template;
        result.webSite = webSite;
        result.sRootDir = sRootDir;
        return result;
    }
}
