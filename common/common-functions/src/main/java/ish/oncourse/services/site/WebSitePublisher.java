/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.services.site;

import ish.oncourse.model.*;
import ish.oncourse.util.ContextUtil;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.Executors;

public class WebSitePublisher extends WebSiteVersionCopy {

    private static final Logger logger = LogManager.getLogger();

    private SystemUser systemUser;
    private String userEmail;
    private String scriptPath;

    //create new published version as copy of draftVersion
    public void publish() {
        copyContent();
        context.commitChanges();
        executeDeployScript(toVersion);
    }
    
    private void initPublishedVersion() {
        toVersion = context.newObject(WebSiteVersion.class);
        toVersion.setWebSite(fromVersion.getWebSite());
        toVersion.setDeployedOn(new Date());
        toVersion.setSiteVersion(fromVersion.getSiteVersion());
        fromVersion.setSiteVersion(GetNextSiteVersion.valueOf(context, fromVersion.getWebSite()).get());
        if (systemUser != null) {
            toVersion.setDeployedBy(context.localObject(systemUser));
        }
    }

    /**
     * Executes deploySite.sh script passing specified file as a parameter.
     * E.g.:
     * 		/var/onCourse/scripts/deploySite.sh -s {siteVersion.getId()} -c {site.getSiteKey()}
     */
    private void executeDeployScript(WebSiteVersion siteVersion) {

        if (scriptPath == null) {
            scriptPath = ContextUtil.getCmsDeployScriptPath();
        }

        if (StringUtils.trimToNull(scriptPath) == null) {
            logger.error("Deploy site script is not defined! Resources have not been deployed!");
            return;
        }

        List<String> scriptCommand = new ArrayList<>();

        scriptCommand.add(scriptPath);
        scriptCommand.add("-s");
        scriptCommand.add(String.valueOf(siteVersion.getId()));
        scriptCommand.add("-c");
        scriptCommand.add(siteVersion.getWebSite().getSiteKey());

        if (userEmail != null) {
            scriptCommand.add("-e");
            scriptCommand.add(userEmail);
        }
        
        new ScriptExecutor(scriptCommand).execute();
    }


    public static WebSitePublisher valueOf(String scriptPath, WebSiteVersion webSiteVersion, SystemUser systemUser, String userEmail, ObjectContext objectContext)
    {
        WebSitePublisher publisher = valueOf(webSiteVersion, systemUser, userEmail, objectContext);
        publisher.scriptPath = scriptPath;
        return publisher;
    }
    
    public static WebSitePublisher valueOf(WebSiteVersion webSiteVersion, SystemUser systemUser, String userEmail, ObjectContext objectContext)
    {
        WebSitePublisher publisher = new WebSitePublisher();
        publisher.context = objectContext;
        publisher.fromVersion = objectContext.localObject(webSiteVersion);
        if (systemUser != null) {
            publisher.systemUser = objectContext.localObject(systemUser);
        }
        publisher.userEmail = userEmail;
        publisher.initPublishedVersion();
        return publisher;
    }

    public static WebSitePublisher valueOf(WebSiteVersion webSiteVersion, ObjectContext objectContext)
    {
        WebSitePublisher publisher = new WebSitePublisher();
        publisher.context = objectContext;
        publisher.fromVersion = objectContext.localObject(webSiteVersion);
        publisher.initPublishedVersion();
        return publisher;
    }

    public static WebSitePublisher valueOf(WebSiteVersion webSiteVersion, WebSiteVersion publishedVersion, ObjectContext objectContext)
    {
        WebSitePublisher publisher = new WebSitePublisher();
        publisher.context = objectContext;
        publisher.fromVersion = objectContext.localObject(webSiteVersion);
        publisher.toVersion = objectContext.localObject(publishedVersion);
        return publisher;
    }


}
