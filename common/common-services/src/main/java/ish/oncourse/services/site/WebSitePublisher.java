/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.services.site;

import ish.oncourse.model.*;
import org.apache.cayenne.ObjectContext;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WebSitePublisher {
    private WebSiteVersion draftVersion;
    private WebSiteVersion publishedVersion;


    //create new published version as copy of draftVersion
    public void publish()
    {
        ObjectContext context = draftVersion.getObjectContext();

        publishedVersion = context.newObject(WebSiteVersion.class);
        publishedVersion.setWebSite(draftVersion.getWebSite());
        publishedVersion.setDeployedOn(new Date());

        for (WebSiteLayout oldLayout : draftVersion.getLayouts()) {

            WebSiteLayout newLayout = context.newObject(WebSiteLayout.class);

            newLayout.setLayoutKey(oldLayout.getLayoutKey());
            newLayout.setWebSiteVersion(publishedVersion);

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

        Map<WebNodeType, WebNodeType> webNodeTypeMap = new HashMap<>();

        for (WebNodeType webNodeType : draftVersion.getWebNodeTypes()) {
            WebNodeType newWebNodeType = context.newObject(WebNodeType.class);

            newWebNodeType.setCreated(webNodeType.getCreated());
            newWebNodeType.setModified(webNodeType.getModified());
            newWebNodeType.setLayoutKey(webNodeType.getLayoutKey());
            newWebNodeType.setName(webNodeType.getName());
            newWebNodeType.setWebSiteVersion(publishedVersion);

            webNodeTypeMap.put(webNodeType, newWebNodeType);
        }

        Map<WebNode, WebNode> webNodeMap = new HashMap<>();

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

        for (WebContent content : draftVersion.getContents()) {
            WebContent newContent = context.newObject(WebContent.class);

            newContent.setCreated(content.getCreated());
            newContent.setModified(content.getModified());
            newContent.setName(content.getName());
            newContent.setContent(content.getContent());
            newContent.setContentTextile(content.getContentTextile());
            newContent.setWebSiteVersion(publishedVersion);

            for (WebContentVisibility visibility : content.getWebContentVisibilities()) {
                WebContentVisibility newVisibility = context.newObject(WebContentVisibility.class);

                newVisibility.setRegionKey(visibility.getRegionKey());
                newVisibility.setWeight(visibility.getWeight());
                newVisibility.setWebContent(newContent);
                newVisibility.setWebNode(webNodeMap.get(visibility.getWebNode()));
                newVisibility.setWebNodeType(webNodeTypeMap.get(visibility.getWebNodeType()));
            }
        }

        Map<WebMenu, WebMenu> webMenuMap = new HashMap<>();

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

        // ... then once we duplicated all the menus we can set up child-parent relations between them
        for (WebMenu menu : webMenuMap.keySet()) {
            WebMenu newMenu = webMenuMap.get(menu);

            newMenu.setParentWebMenu(webMenuMap.get(menu.getParentWebMenu()));
        }

    }

    public void setDraftVersion(WebSiteVersion draftVersion) {
        this.draftVersion = draftVersion;
    }

    public WebSiteVersion getDraftVersion()
    {
        return this.draftVersion;
    }

    public WebSiteVersion getPublishedVersion() {
        return publishedVersion;
    }

    public static WebSitePublisher valueOf(WebSiteVersion webSiteVersion)
    {
        WebSitePublisher publisher = new WebSitePublisher();
        publisher.setDraftVersion(webSiteVersion);
        return publisher;
    }
}
