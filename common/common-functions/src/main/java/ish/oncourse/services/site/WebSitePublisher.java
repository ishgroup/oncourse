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

public class WebSitePublisher {

    private static final Logger logger = LogManager.getLogger();

    private ObjectContext context;
    private SystemUser systemUser;
    private String userEmail;
    private WebSiteVersion draftVersion;
    private WebSiteVersion publishedVersion;
    private String scriptPath;

    private Map<WebNodeType, WebNodeType> webNodeTypeMap = new HashMap<>();
    private Map<WebNode, WebNode> webNodeMap = new HashMap<>();
    private Map<WebMenu, WebMenu> webMenuMap = new HashMap<>();
	private Map<WebSiteLayout, WebSiteLayout> layoutMap = new HashMap<>();

    //create new published version as copy of draftVersion
    public void publish()
    {
        copyLayouts();

        copyWebNodeTypes();

        copyWebNodes();

        copyWebUrlAliases();

        copyWebContents();

        copyWebMenus();

        context.commitChanges();

        executeDeployScript(publishedVersion);
    }

    private void updateParentMenus() {
        // ... then once we duplicated all the menus we can set up child-parent relations between them
        for (WebMenu menu : webMenuMap.keySet()) {
            WebMenu newMenu = webMenuMap.get(menu);

            newMenu.setParentWebMenu(webMenuMap.get(menu.getParentWebMenu()));
        }
    }

    private void copyWebMenus() {
        // first duplicate all the existing WebMenu records...
        for (WebMenu menu : draftVersion.getMenus()) {

            WebMenu newMenu = context.newObject(WebMenu.class);

            newMenu.setCreated(menu.getCreated());
            newMenu.setModified(menu.getModified());
            newMenu.setName(menu.getName());
            newMenu.setUrl(menu.getUrl());
            newMenu.setWebNode(webNodeMap.get(menu.getWebNode()));
            newMenu.setWebSiteVersion(publishedVersion);
            newMenu.setWeight(menu.getWeight());

            webMenuMap.put(menu, newMenu);
        }

        updateParentMenus();
    }

    private void copyWebContents() {
        for (WebContent content : draftVersion.getContents()) {
            WebContent newContent = context.newObject(WebContent.class);

            newContent.setCreated(content.getCreated());
            newContent.setModified(content.getModified());
            newContent.setName(content.getName());
            newContent.setContent(content.getContent());
            newContent.setContentTextile(content.getContentTextile());
            newContent.setWebSiteVersion(publishedVersion);

            copyWebContentVisibilities(content, newContent);
        }
    }

    private void copyWebContentVisibilities(WebContent oldContent, WebContent newContent) {
        for (WebContentVisibility visibility : oldContent.getWebContentVisibilities()) {
            WebContentVisibility newVisibility = context.newObject(WebContentVisibility.class);

            newVisibility.setRegionKey(visibility.getRegionKey());
            newVisibility.setWeight(visibility.getWeight());
            newVisibility.setWebContent(newContent);
            newVisibility.setWebNode(webNodeMap.get(visibility.getWebNode()));
            newVisibility.setWebNodeType(webNodeTypeMap.get(visibility.getWebNodeType()));
        }
    }

    private void copyWebUrlAliases() {
        for (WebUrlAlias webUrlAlias : draftVersion.getWebURLAliases()) {
            WebUrlAlias newWebUrlAlias = context.newObject(WebUrlAlias.class);

            newWebUrlAlias.setCreated(webUrlAlias.getCreated());
            newWebUrlAlias.setModified(webUrlAlias.getModified());
            newWebUrlAlias.setUrlPath(webUrlAlias.getUrlPath());
            newWebUrlAlias.setDefault(webUrlAlias.isDefault());
            newWebUrlAlias.setWebNode(webNodeMap.get(webUrlAlias.getWebNode()));
            newWebUrlAlias.setWebSiteVersion(publishedVersion);
            newWebUrlAlias.setRedirectTo(webUrlAlias.getRedirectTo());
        }
    }

    private void copyWebNodes() {
        for (WebNode node : draftVersion.getWebNodes()) {
            WebNode newNode = context.newObject(WebNode.class);

            newNode.setCreated(node.getCreated());
            newNode.setModified(node.getModified());
            newNode.setName(node.getName());
            newNode.setNodeNumber(node.getNodeNumber());
            newNode.setPublished(node.isPublished());
            newNode.setWebNodeType(webNodeTypeMap.get(node.getWebNodeType()));
            newNode.setWebSiteVersion(publishedVersion);

            webNodeMap.put(node, newNode);
        }
    }

    private void copyWebNodeTypes() {
        for (WebNodeType webNodeType : draftVersion.getWebNodeTypes()) {
            WebNodeType newWebNodeType = context.newObject(WebNodeType.class);

            newWebNodeType.setCreated(webNodeType.getCreated());
            newWebNodeType.setModified(webNodeType.getModified());
            newWebNodeType.setWebSiteLayout(layoutMap.get(webNodeType.getWebSiteLayout()));
            newWebNodeType.setName(webNodeType.getName());
            newWebNodeType.setWebSiteVersion(publishedVersion);

            webNodeTypeMap.put(webNodeType, newWebNodeType);
        }
    }

    private void copyLayouts() {
        for (WebSiteLayout oldLayout : draftVersion.getLayouts()) {

            WebSiteLayout newLayout = context.newObject(WebSiteLayout.class);

            newLayout.setLayoutKey(oldLayout.getLayoutKey());
            newLayout.setWebSiteVersion(publishedVersion);

            copyTemplates(oldLayout, newLayout);
			
			layoutMap.put(oldLayout, newLayout);
        }
    }

    private void copyTemplates(WebSiteLayout oldLayout, WebSiteLayout newLayout) {
        for (WebTemplate template : oldLayout.getTemplates()) {
            WebTemplate newTemplate = context.newObject(WebTemplate.class);

            newTemplate.setLayout(newLayout);
            newTemplate.setName(template.getName());
            newTemplate.setContent(template.getContent());

            // need to change modified date of every template to make
            // tapestry rendering logic to reload them
            template.setModified(new Date());
        }
    }

    private void initPublishedVersion() {
        publishedVersion = context.newObject(WebSiteVersion.class);
        publishedVersion.setWebSite(draftVersion.getWebSite());
        publishedVersion.setDeployedOn(new Date());
        if (systemUser != null) {
            publishedVersion.setDeployedBy(context.localObject(systemUser));
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

        ProcessBuilder processBuilder = new ProcessBuilder(scriptCommand);

        try {
            processBuilder.start();
        } catch (Exception e) {
            logger.error("Error executing script '{}'", scriptPath, e);
        }
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
        publisher.draftVersion = objectContext.localObject(webSiteVersion);
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
        publisher.draftVersion = objectContext.localObject(webSiteVersion);
        publisher.initPublishedVersion();
        return publisher;
    }

    public static WebSitePublisher valueOf(WebSiteVersion webSiteVersion, WebSiteVersion publishedVersion, ObjectContext objectContext)
    {
        WebSitePublisher publisher = new WebSitePublisher();
        publisher.context = objectContext;
        publisher.draftVersion = objectContext.localObject(webSiteVersion);
        publisher.publishedVersion = objectContext.localObject(publishedVersion);
        return publisher;
    }


}
