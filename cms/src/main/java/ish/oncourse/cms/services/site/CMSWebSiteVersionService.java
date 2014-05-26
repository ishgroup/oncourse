/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cms.services.site;

import ish.oncourse.cms.services.access.IAuthenticationService;
import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.AbstractWebSiteVersionService;
import ish.oncourse.services.site.IWebSiteVersionService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * CMS implementation of {@link IWebSiteVersionService}.
 * Current version is determined as staged version (WebSiteVersion.deployedOn == null).
 */
public class CMSWebSiteVersionService extends AbstractWebSiteVersionService {
	
	@Inject
	private IWebSiteVersionService webSiteVersionService;
	
	@Inject
	private ICayenneService cayenneService;
	
	@Inject
	private IAuthenticationService authenticationService;

	@Override
	public WebSiteVersion getCurrentVersion(WebSite webSite) {
		WebSiteVersion currentVersion = getStagedVersion(webSite);
		
		// if there is no staged version for specified website then try to create one
		if (currentVersion == null) {
			ObjectContext context = cayenneService.newContext();
			WebSiteVersion oldVersion = context.localObject(getDeployedVersion(webSite));
			
			copyVersion(oldVersion);
			
			context.commitChanges();
			
			currentVersion = getStagedVersion(webSite);
		}
		
		return currentVersion;
	}
	
	private WebSiteVersion getStagedVersion(WebSite webSite) {
		SelectQuery query = new SelectQuery(WebSiteVersion.class);

		query.andQualifier(ExpressionFactory.matchExp(WebSiteVersion.WEB_SITE_PROPERTY, webSite));
		query.andQualifier(ExpressionFactory.matchExp(WebSiteVersion.DEPLOYED_ON_PROPERTY, null));

		return (WebSiteVersion) Cayenne.objectForQuery(webSite.getObjectContext(), query);
	}
	
	private WebSiteVersion copyVersion(WebSiteVersion oldVersion) {
		ObjectContext context = oldVersion.getObjectContext();
		
		WebSiteVersion newVersion = context.newObject(WebSiteVersion.class);
		newVersion.setWebSite(oldVersion.getWebSite());

		for (WebSiteLayout oldLayout : oldVersion.getLayouts()) {

			WebSiteLayout newLayout = context.newObject(WebSiteLayout.class);

			newLayout.setLayoutKey(oldLayout.getLayoutKey());
			newLayout.setWebSiteVersion(newVersion);

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

		for (WebNodeType webNodeType : oldVersion.getWebNodeTypes()) {
			WebNodeType newWebNodeType = context.newObject(WebNodeType.class);

			newWebNodeType.setCreated(webNodeType.getCreated());
			newWebNodeType.setModified(webNodeType.getModified());
			newWebNodeType.setLayoutKey(webNodeType.getLayoutKey());
			newWebNodeType.setName(webNodeType.getName());
			newWebNodeType.setWebSiteVersion(newVersion);

			webNodeTypeMap.put(webNodeType, newWebNodeType);
		}

		Map<WebNode, WebNode> webNodeMap = new HashMap<>();

		for (WebNode node : oldVersion.getWebNodes()) {
			WebNode newNode = context.newObject(WebNode.class);

			newNode.setCreated(node.getCreated());
			newNode.setModified(node.getModified());
			newNode.setName(node.getName());
			newNode.setNodeNumber(node.getNodeNumber());
			newNode.setPublished(node.isPublished());
			newNode.setWebNodeType(webNodeTypeMap.get(node.getWebNodeType()));
			newNode.setWebSiteVersion(newVersion);

			webNodeMap.put(node, newNode);
		}

		for (WebUrlAlias webUrlAlias : oldVersion.getWebURLAliases()) {
			WebUrlAlias newWebUrlAlias = context.newObject(WebUrlAlias.class);

			newWebUrlAlias.setCreated(webUrlAlias.getCreated());
			newWebUrlAlias.setModified(webUrlAlias.getModified());
			newWebUrlAlias.setUrlPath(webUrlAlias.getUrlPath());
			newWebUrlAlias.setDefault(webUrlAlias.isDefault());
			newWebUrlAlias.setWebNode(webNodeMap.get(webUrlAlias.getWebNode()));
			newWebUrlAlias.setWebSiteVersion(newVersion);
		}

		for (WebContent content : oldVersion.getContents()) {
			WebContent newContent = context.newObject(WebContent.class);

			newContent.setCreated(content.getCreated());
			newContent.setModified(content.getModified());
			newContent.setName(content.getName());
			newContent.setContent(content.getContent());
			newContent.setContentTextile(content.getContentTextile());
			newContent.setWebSiteVersion(newVersion);

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
		for (WebMenu menu : oldVersion.getMenus()) {

			WebMenu newMenu = context.newObject(WebMenu.class);

			newMenu.setCreated(menu.getCreated());
			newMenu.setModified(menu.getModified());
			newMenu.setName(menu.getName());
			newMenu.setUrl(menu.getUrl());
			newMenu.setWebNode(webNodeMap.get(menu.getWebNode()));
			newMenu.setWebSiteVersion(newVersion);
			newMenu.setWeight(menu.getWeight());

			webMenuMap.put(menu, newMenu);
		}

		// ... then once we duplicated all the menus we can set up child-parent relations between them
		for (WebMenu menu : webMenuMap.keySet()) {
			WebMenu newMenu = webMenuMap.get(menu);

			newMenu.setParentWebMenu(webMenuMap.get(menu.getParentWebMenu()));
		}

		return newVersion;
	}

	@Override
	public void deploy(WebSite siteToDeploy) {
		ObjectContext context = cayenneService.newContext();
		
		WebSite webSite = context.localObject(siteToDeploy);
		
		WebSiteVersion oldVersion = webSiteVersionService.getCurrentVersion(webSite);
		oldVersion.setDeployedOn(new Date());
		oldVersion.setDeployedBy(authenticationService.getSystemUser());

		copyVersion(oldVersion);

		context.commitChanges();
	}
	
}
