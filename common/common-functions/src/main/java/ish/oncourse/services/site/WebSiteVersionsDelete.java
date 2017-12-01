package ish.oncourse.services.site;

import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebSiteVersion;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class WebSiteVersionsDelete {
    private static final Logger logger = LogManager.getLogger();

    private static final int KEEP_AT_LEAST = 4;

    private WebSite webSite;

    private WebSiteVersion currentVersion;
    private WebSiteVersion deployedVersion;

    private ObjectContext objectContext;

    public void delete() {

        List<WebSiteVersion> allVersions = ObjectSelect.query(WebSiteVersion.class).
                where(WebSiteVersion.WEB_SITE.eq(webSite)).
                and(WebSiteVersion.DEPLOYED_ON.isNotNull()).  //exclude unpublished revisions
                orderBy(WebSiteVersion.DEPLOYED_ON.desc()).
                select(objectContext);

        //if number of revisions less than 5 (4 + 1 unpublished) - nothing to delete
        if (allVersions.size() > KEEP_AT_LEAST) {

            //don't delete the last few
            List<WebSiteVersion> versionsToDelete = allVersions.subList(KEEP_AT_LEAST, allVersions.size());

            //delete all revisions older than 60 days
            Date deleteBeforeDate = DateUtils.addDays(new Date(), -60);
            versionsToDelete = WebSiteVersion.DEPLOYED_ON.lt(deleteBeforeDate).filterObjects(versionsToDelete);

            for (WebSiteVersion version : versionsToDelete) {
                try {
                    WebSiteVersionDelete.valueOf(version, currentVersion, deployedVersion, objectContext).delete();
                } catch (Exception e) {
                    logger.error("Cannot delete WebSiteVersion with id: {}", version.getId(), e);
                    objectContext.rollbackChanges();
                }
            }
        }
    }

    public static WebSiteVersionsDelete valueOf(WebSite webSite, WebSiteVersion currentVersion, WebSiteVersion deployedVersion, ObjectContext objectContext) {
        WebSiteVersionsDelete result = new WebSiteVersionsDelete();
        result.webSite = objectContext.localObject(webSite);
        result.currentVersion = objectContext.localObject(currentVersion);
        result.deployedVersion = objectContext.localObject(deployedVersion);
        result.objectContext = objectContext;
        return result;
    }
}
