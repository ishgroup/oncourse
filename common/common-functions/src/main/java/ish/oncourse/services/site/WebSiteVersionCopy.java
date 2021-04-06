/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.site;

import ish.oncourse.model.*;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WebSiteVersionCopy {
    
    private ObjectContext context;
    private WebSiteVersion fromVersion;
    private WebSiteVersion toVersion;

    private Map<WebNodeType, WebNodeType> webNodeTypeMap = new HashMap<>();
    private Map<WebNode, WebNode> webNodeMap = new HashMap<>();
    private Map<WebMenu, WebMenu> webMenuMap = new HashMap<>();
    private Map<WebSiteLayout, WebSiteLayout> layoutMap = new HashMap<>();

    private WebSiteVersionCopy() {}

    public static WebSiteVersionCopy valueOf(ObjectContext context, WebSiteVersion fromVersion, WebSiteVersion toVersion) {
        WebSiteVersionCopy obj = new WebSiteVersionCopy();
        obj.context = context;
        obj.fromVersion = fromVersion;
        obj.toVersion = toVersion;
        return obj;
    }

    public void copyContent() {
        copyLayouts();
        copyWebNodeTypes();
        copyWebNodes();
        copyWebUrlAliases();
        copyWebContents();
        copyWebMenus();
        context.commitChanges();
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

        ObjectSelect.query(WebMenu.class)
                .where(WebMenu.WEB_SITE_VERSION.eq(fromVersion))
                .select(context)
                .forEach(menu -> {

            WebMenu newMenu = context.newObject(WebMenu.class);

            newMenu.setCreated(menu.getCreated());
            newMenu.setModified(menu.getModified());
            newMenu.setName(menu.getName());
            newMenu.setUrl(menu.getUrl());
            newMenu.setWebNode(webNodeMap.get(menu.getWebNode()));
            newMenu.setWebSiteVersion(toVersion);
            newMenu.setWeight(menu.getWeight());

            webMenuMap.put(menu, newMenu);
        });

        updateParentMenus();
    }

    private void copyWebContents() {
        ObjectSelect.query(WebContent.class)
                .where(WebContent.WEB_SITE_VERSION.eq(fromVersion))
                .select(context)
                .forEach(content -> {
            WebContent newContent = context.newObject(WebContent.class);

            newContent.setCreated(content.getCreated());
            newContent.setModified(content.getModified());
            newContent.setName(content.getName());
            newContent.setContent(content.getContent());
            newContent.setContentTextile(content.getContentTextile());
            newContent.setWebSiteVersion(toVersion);

            copyWebContentVisibilities(content, newContent);
        });
    }

    private void copyWebContentVisibilities(WebContent oldContent, WebContent newContent) {
        
        ObjectSelect.query(WebContentVisibility.class)
                .where(WebContentVisibility.WEB_CONTENT.eq(oldContent))
                .select(context)
                .forEach(visibility -> {
            WebContentVisibility newVisibility = context.newObject(WebContentVisibility.class);

            newVisibility.setRegionKey(visibility.getRegionKey());
            newVisibility.setWeight(visibility.getWeight());
            newVisibility.setWebContent(newContent);
            newVisibility.setWebNode(webNodeMap.get(visibility.getWebNode()));
            newVisibility.setWebNodeType(webNodeTypeMap.get(visibility.getWebNodeType())); 
        });
    }

    private void copyWebUrlAliases() {
        ObjectSelect.query(WebUrlAlias.class)
                .where(WebUrlAlias.WEB_SITE_VERSION.eq(fromVersion))
                .select(context)
                .forEach(webUrlAlias -> {
                    
            WebUrlAlias newWebUrlAlias = context.newObject(WebUrlAlias.class);

            newWebUrlAlias.setCreated(webUrlAlias.getCreated());
            newWebUrlAlias.setModified(webUrlAlias.getModified());
            newWebUrlAlias.setUrlPath(webUrlAlias.getUrlPath());
            newWebUrlAlias.setDefault(webUrlAlias.isDefault());
            newWebUrlAlias.setWebNode(webNodeMap.get(webUrlAlias.getWebNode()));
            newWebUrlAlias.setWebSiteVersion(toVersion);
            newWebUrlAlias.setRedirectTo(webUrlAlias.getRedirectTo());
            newWebUrlAlias.setSpecialPage(webUrlAlias.getSpecialPage());
            newWebUrlAlias.setMatchType(webUrlAlias.getMatchType());
        });
    }

    private void copyWebNodes() {
        ObjectSelect.query(WebNode.class)
                .where(WebNode.WEB_SITE_VERSION.eq(fromVersion))
                .select(context)
                .forEach(node -> {
            WebNode newNode = context.newObject(WebNode.class);

            newNode.setCreated(node.getCreated());
            newNode.setModified(node.getModified());
            newNode.setName(node.getName());
            newNode.setNodeNumber(node.getNodeNumber());
            newNode.setPublished(node.isPublished());
            newNode.setWebSiteVersion(toVersion);
            newNode.setSuppressOnSitemap(node.isSuppressOnSitemap());
            webNodeMap.put(node, newNode);
        });
    }

    private void copyWebNodeTypes() {
        ObjectSelect.query(WebNodeType.class)
                .where(WebNodeType.WEB_SITE_VERSION.eq(fromVersion))
                .select(context)
                .forEach(webNodeType ->  {
                    
            WebNodeType newWebNodeType = context.newObject(WebNodeType.class);

            newWebNodeType.setCreated(webNodeType.getCreated());
            newWebNodeType.setModified(webNodeType.getModified());
            newWebNodeType.setWebSiteLayout(layoutMap.get(webNodeType.getWebSiteLayout()));
            newWebNodeType.setName(webNodeType.getName());
            newWebNodeType.setWebSiteVersion(toVersion);
            webNodeType.getWebLayoutPaths().forEach( path -> {
                WebLayoutPath newPath = context.newObject(WebLayoutPath.class);
                newPath.setCreated(new Date());
                newPath.setModified(new Date());
                newPath.setWebSiteVersion(toVersion);
                newPath.setWebNodeType(newWebNodeType);
                newPath.setPath(path.getPath());
                newPath.setMatchType(path.getMatchType());

            });

            webNodeTypeMap.put(webNodeType, newWebNodeType);
        });
    }

    private void copyLayouts() {
        ObjectSelect.query(WebSiteLayout.class)
                .where(WebSiteLayout.WEB_SITE_VERSION.eq(fromVersion))
                .select(context)
                .forEach(oldLayout -> {
            
            WebSiteLayout newLayout = context.newObject(WebSiteLayout.class);

            newLayout.setLayoutKey(oldLayout.getLayoutKey());
            newLayout.setWebSiteVersion(toVersion);

            copyTemplates(oldLayout, newLayout);

            layoutMap.put(oldLayout, newLayout);
        });
        
    }

    private void copyTemplates(WebSiteLayout oldLayout, WebSiteLayout newLayout) {

        ObjectSelect.query(WebTemplate.class)
                .where(WebTemplate.LAYOUT.eq(oldLayout))
                .select(context)
                .forEach(template -> {
                    
            WebTemplate newTemplate = context.newObject(WebTemplate.class);
            newTemplate.setLayout(newLayout);
            newTemplate.setName(template.getName());
            newTemplate.setContent(template.getContent());

            // need to change modified date of every template to make
            // tapestry rendering logic to reload them
            template.setModified(new Date());
        });
    }

}
