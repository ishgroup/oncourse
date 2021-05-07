/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.services.site;

import ish.oncourse.model.*;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class WebSitePublisher {

    private static final Logger logger = LogManager.getLogger();

    private ObjectContext context;
    private WebSiteVersion draftVersion;
    private WebSiteVersion newVersion;

    private SystemUser systemUser;
    private String userEmail;
    private String scriptPath;
    private String message;

    public void publish() {
        WebSiteVersionCopy.valueOf(context, draftVersion, newVersion).copyContent();
        executeDeployScript(newVersion);
    }
    
    private void initPublishedVersion() {
        newVersion = context.newObject(WebSiteVersion.class);
        newVersion.setWebSite(draftVersion.getWebSite());
        newVersion.setDeployedOn(new Date());
        newVersion.setSiteVersion(draftVersion.getSiteVersion());
        draftVersion.setSiteVersion(GetNextSiteVersion.valueOf(context, draftVersion.getWebSite()).get());
        if (systemUser != null) {
            newVersion.setDeployedBy(context.localObject(systemUser));
        }
    }

    /**
     * Executes deploySite.sh script passing specified file as a parameter.
     * E.g.:
     * 		/var/onCourse/scripts/deploySite.sh -s {siteVersion.getId()} -c {site.getSiteKey()}
     */
    private void executeDeployScript(WebSiteVersion siteVersion) {
        if (StringUtils.trimToNull(scriptPath) == null) {
            logger.error("Deploy site script is not defined! Resources have not been deployed!");
            return;
        }

        List<String> scriptCommand = new ArrayList<>();

        scriptCommand.add(scriptPath);

        scriptCommand.add("-c");
        scriptCommand.add(siteVersion.getWebSite().getSiteKey());

        if (userEmail != null) {
            scriptCommand.add("-e");
            scriptCommand.add(userEmail);
        }
        
        if (systemUser != null && systemUser.getFirstName() != null  && systemUser.getSurname() != null) {
            scriptCommand.add("-u");
            scriptCommand.add(systemUser.getFirstName() + " " + systemUser.getSurname());
        }
        if (message != null) {
            scriptCommand.add("-m");
            scriptCommand.add(message);
        }
        new ScriptExecutor(scriptCommand).execute();
    }


    public static WebSitePublisher valueOf(String scriptPath, WebSiteVersion draftVersion, SystemUser systemUser, String userEmail, ObjectContext objectContext, String message) {
        WebSitePublisher publisher = valueOf(draftVersion, systemUser, userEmail, scriptPath, objectContext);
        publisher.message = message;
        return publisher;
    }
    
    public static WebSitePublisher valueOf(WebSiteVersion draftVersion, SystemUser systemUser, String userEmail, String scriptPath, ObjectContext objectContext) {
        WebSitePublisher publisher = new WebSitePublisher();
        publisher.context = objectContext;
        publisher.draftVersion = objectContext.localObject(draftVersion);
        if (systemUser != null) {
            publisher.systemUser = objectContext.localObject(systemUser);
        }
        publisher.scriptPath = scriptPath;
        publisher.userEmail = userEmail;
        publisher.initPublishedVersion();
        return publisher;
    }

    public static WebSitePublisher valueOf(WebSiteVersion draftVersion, String scriptPath, ObjectContext objectContext) {
        WebSitePublisher publisher = new WebSitePublisher();
        publisher.context = objectContext;
        publisher.draftVersion = objectContext.localObject(draftVersion);
        publisher.scriptPath = scriptPath;
        publisher.initPublishedVersion();
        return publisher;
    }

    public static WebSitePublisher valueOf(WebSiteVersion draftVersion, WebSiteVersion publishedVersion, String scriptPath, ObjectContext objectContext) {
        WebSitePublisher publisher = new WebSitePublisher();
        publisher.context = objectContext;
        publisher.draftVersion = objectContext.localObject(draftVersion);
        publisher.newVersion = objectContext.localObject(publishedVersion);
        publisher.scriptPath = scriptPath;
        return publisher;
    }


}
