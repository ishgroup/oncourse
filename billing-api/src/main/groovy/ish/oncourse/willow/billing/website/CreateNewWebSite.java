package ish.oncourse.willow.billing.website;

import ish.oncourse.configuration.Configuration;
import ish.oncourse.model.College;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebSiteVersion;
import ish.oncourse.services.site.WebSitePublisher;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;

import java.io.File;
import java.util.Date;
import java.util.HashMap;

import static ish.oncourse.configuration.Configuration.AdminProperty.DEPLOY_SCRIPT_PATH;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class CreateNewWebSite {


    private String name;
    private String key;
    private WebSite template;
    private String sRootPath;

    private College college;
    private ObjectContext context;

    private Date now = new Date();
    private HashMap<String, String> errors = new HashMap<>();


    private WebSite webSite;
    private WebSiteVersion webSiteVersion;
    private File sRootFile;


    public void create() {
        createFileStructure();
        createWebSiteStructure();
        
        context.commitChanges();

        WebSitePublisher publisher = WebSitePublisher.valueOf(webSiteVersion, Configuration.getValue(DEPLOY_SCRIPT_PATH), context);
        publisher.publish();
    }


    private void createWebSiteStructure() {
        if (template != null && sRootFile != null) {
            boolean result = CopyTemplateStaticResources.valueOf(template, webSite, sRootFile).copy();
            if (!result) {
                errors.put("addSite.copyStaticResources",
                        String.format("Cannot copy static resources from template %s to site %s. See log messages.",
                                template.getSiteKey(),
                                webSite.getSiteKey()));
            }
            WebSiteVersion templateVersion =
                    ObjectSelect.query(WebSiteVersion.class)
                            .and(WebSiteVersion.WEB_SITE.eq(template))
                            .orderBy(WebSiteVersion.DEPLOYED_ON.desc())
                            .limit(1).selectFirst(template.getObjectContext());
            
            WebSitePublisher.valueOf(templateVersion, webSiteVersion, Configuration.getValue(DEPLOY_SCRIPT_PATH), context).publish();
        } else {
            CreateDefaultWebSiteStructure.valueOf(webSiteVersion, context).create();
        }
    }

    private void createFileStructure() {
        if (sRootFile != null) {
            boolean result = CreateSiteFileStructure.valueOf(webSite, sRootFile).create();
            if (!result) {
                errors.put("addSite.createFileStructure",
                        String.format("Cannot create file structure for site with key %s. See log messages.", webSite.getSiteKey()));
            }
        }
    }


    private void initSRoot() {
        if (sRootPath == null) {
            errors.put("addSite.resourceRootDir", "s.root is not configured in context.xml");
            return;
        }
        sRootFile = new File(sRootPath);
    }


    private void initWebSiteVersion() {
        //we create staged web site version
        webSiteVersion = context.newObject(WebSiteVersion.class);
        webSiteVersion.setWebSite(webSite);
        webSiteVersion.setSiteVersion(1L);
    }


    private void initWebSite() {
        webSite = context.newObject(WebSite.class);
        webSite.setCollege(college);
        webSite.setName(name);
        webSite.setSiteKey(key);
        webSite.setCreated(now);
        webSite.setModified(now);
    }

    public HashMap<String, String> getErrors() {
        return errors;
    }

    public WebSite getWebSite() {
        return webSite;
    }

    public static CreateNewWebSite valueOf(String name,
                                           String key,
                                           WebSite template,
                                           String sRootPath,
                                           College college, ObjectContext context) {
        CreateNewWebSite createNewWebSite = new CreateNewWebSite();
        createNewWebSite.name = name;
        createNewWebSite.key = key;
        createNewWebSite.template = template;
        createNewWebSite.context = context;
        createNewWebSite.college = context.localObject(college);
        createNewWebSite.sRootPath = sRootPath;
        createNewWebSite.initSRoot();
        createNewWebSite.initWebSite();
        createNewWebSite.initWebSiteVersion();
        return createNewWebSite;
    }
}
