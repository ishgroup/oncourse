/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.site;

import ish.oncourse.model.*;
import org.apache.cayenne.ObjectContext;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class WebSiteVersionCopy {
    
    protected ObjectContext context;
    WebSiteVersion fromVersion;
    WebSiteVersion toVersion;

    private Map<WebNodeType, WebNodeType> webNodeTypeMap = new HashMap<>();
    private Map<WebNode, WebNode> webNodeMap = new HashMap<>();
    private Map<WebMenu, WebMenu> webMenuMap = new HashMap<>();
    private Map<WebSiteLayout, WebSiteLayout> layoutMap = new HashMap<>();

    
    void copyContent() {
        
        copyLayouts();

        copyWebNodeTypes();

        copyWebNodes();

        copyWebUrlAliases();

        copyWebContents();

        copyWebMenus();
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
        for (WebMenu menu : fromVersion.getMenus()) {

            WebMenu newMenu = context.newObject(WebMenu.class);

            newMenu.setCreated(menu.getCreated());
            newMenu.setModified(menu.getModified());
            newMenu.setName(menu.getName());
            newMenu.setUrl(menu.getUrl());
            newMenu.setWebNode(webNodeMap.get(menu.getWebNode()));
            newMenu.setWebSiteVersion(toVersion);
            newMenu.setWeight(menu.getWeight());

            webMenuMap.put(menu, newMenu);
        }

        updateParentMenus();
    }

    private void copyWebContents() {
        for (WebContent content : fromVersion.getContents()) {
            WebContent newContent = context.newObject(WebContent.class);

            newContent.setCreated(content.getCreated());
            newContent.setModified(content.getModified());
            newContent.setName(content.getName());
            newContent.setContent(content.getContent());
            newContent.setContentTextile(content.getContentTextile());
            newContent.setWebSiteVersion(toVersion);

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
        for (WebUrlAlias webUrlAlias : fromVersion.getWebURLAliases()) {
            WebUrlAlias newWebUrlAlias = context.newObject(WebUrlAlias.class);

            newWebUrlAlias.setCreated(webUrlAlias.getCreated());
            newWebUrlAlias.setModified(webUrlAlias.getModified());
            newWebUrlAlias.setUrlPath(webUrlAlias.getUrlPath());
            newWebUrlAlias.setDefault(webUrlAlias.isDefault());
            newWebUrlAlias.setWebNode(webNodeMap.get(webUrlAlias.getWebNode()));
            newWebUrlAlias.setWebSiteVersion(toVersion);
            newWebUrlAlias.setRedirectTo(webUrlAlias.getRedirectTo());
        }
    }

    private void copyWebNodes() {
        for (WebNode node : fromVersion.getWebNodes()) {
            WebNode newNode = context.newObject(WebNode.class);

            newNode.setCreated(node.getCreated());
            newNode.setModified(node.getModified());
            newNode.setName(node.getName());
            newNode.setNodeNumber(node.getNodeNumber());
            newNode.setPublished(node.isPublished());
            newNode.setWebNodeType(webNodeTypeMap.get(node.getWebNodeType()));
            newNode.setWebSiteVersion(toVersion);

            webNodeMap.put(node, newNode);
        }
    }

    private void copyWebNodeTypes() {
        for (WebNodeType webNodeType : fromVersion.getWebNodeTypes()) {
            WebNodeType newWebNodeType = context.newObject(WebNodeType.class);

            newWebNodeType.setCreated(webNodeType.getCreated());
            newWebNodeType.setModified(webNodeType.getModified());
            newWebNodeType.setWebSiteLayout(layoutMap.get(webNodeType.getWebSiteLayout()));
            newWebNodeType.setName(webNodeType.getName());
            newWebNodeType.setWebSiteVersion(toVersion);

            webNodeTypeMap.put(webNodeType, newWebNodeType);
        }
    }

    private void copyLayouts() {
        for (WebSiteLayout oldLayout : fromVersion.getLayouts()) {

            WebSiteLayout newLayout = context.newObject(WebSiteLayout.class);

            newLayout.setLayoutKey(oldLayout.getLayoutKey());
            newLayout.setWebSiteVersion(toVersion);

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

}
