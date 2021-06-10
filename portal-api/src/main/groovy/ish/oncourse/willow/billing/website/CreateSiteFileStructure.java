package ish.oncourse.willow.billing.website;

import ish.oncourse.model.WebSite;
import ish.oncourse.util.StaticResourcePath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class CreateSiteFileStructure {

    private static final Logger logger = LogManager.getLogger();

    private WebSite webSite;
    private File sRootDir;

    public boolean create() {
        File siteDir = new File(sRootDir, webSite.getSiteKey());
        if (!siteDir.exists() && !siteDir.mkdirs()) {
            logger.error("Cannot create dir {}.", siteDir.getAbsolutePath());
            return false;
        }

        StaticResourcePath[] paths = StaticResourcePath.values();
        for (StaticResourcePath path : paths) {
            File file = new File(siteDir, path.getFileSystemPath());
            if (!file.exists() && !file.mkdirs()) {
                logger.error("Cannot create dir %s.", file.getAbsolutePath());
            }
        }
        return true;
    }

    public static CreateSiteFileStructure valueOf(WebSite webSite, File sRootDir) {
        CreateSiteFileStructure result = new CreateSiteFileStructure();
        result.webSite = webSite;
        result.sRootDir = sRootDir;
        return result;
    }


}
