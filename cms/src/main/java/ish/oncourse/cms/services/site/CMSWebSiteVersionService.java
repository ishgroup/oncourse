/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cms.services.site;

import ish.oncourse.cms.services.access.IAuthenticationService;
import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.AbstractWebSiteVersionService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.services.site.WebSitePublisher;
import ish.oncourse.util.ContextUtil;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

import javax.naming.Context;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * CMS implementation of {@link IWebSiteVersionService}.
 * Current version is determined as staged version (WebSiteVersion.deployedOn == null).
 */
public class CMSWebSiteVersionService extends AbstractWebSiteVersionService {

	private static final int KEEP_AT_LEAST = 4;
	
	private static final Logger logger = Logger.getLogger(CMSWebSiteVersionService.class);
	
	@Inject
	private IWebSiteVersionService webSiteVersionService;
	
	@Inject
	private ICayenneService cayenneService;
	
	@Inject
	private IAuthenticationService authenticationService;

    @Inject
    private IWebSiteService webSiteService;

	@Override
	public WebSiteVersion getCurrentVersion() {
        WebSite webSite = webSiteService.getCurrentWebSite();
		WebSiteVersion currentVersion = getDraftVersion(webSite);
		return currentVersion;
	}
	
	private WebSiteVersion getDraftVersion(WebSite webSite) {
		SelectQuery query = new SelectQuery(WebSiteVersion.class);

		query.andQualifier(ExpressionFactory.matchExp(WebSiteVersion.WEB_SITE_PROPERTY, webSite));
		query.andQualifier(ExpressionFactory.matchExp(WebSiteVersion.DEPLOYED_ON_PROPERTY, null));

		return (WebSiteVersion) Cayenne.objectForQuery(webSite.getObjectContext(), query);
	}

	@Override
	public void publish() {
		ObjectContext context = cayenneService.newContext();

		WebSiteVersion draft = context.localObject(webSiteVersionService.getCurrentVersion());

        WebSitePublisher publisher = WebSitePublisher.valueOf(draft);
        publisher.publish();
        WebSiteVersion published = publisher.getPublishedVersion();

        SystemUser systemUser = authenticationService.getSystemUser();
        if (systemUser != null) {
            published.setDeployedBy(context.localObject(systemUser));
        }

		context.commitChanges();

		executeDeployScript(published);
	}

	@Override
	public void delete(WebSiteVersion webSiteVersionToDelte) {
		
		if (getCurrentVersion().getId().equals(webSiteVersionToDelte.getId())
				|| getDeployedVersion(webSiteService.getCurrentWebSite()).getId().equals(webSiteVersionToDelte.getId())) {
			// prevent the deletion of the current live site or the draft site!
			throw new RuntimeException("Attempt to delete current live site or the draft site version");
		}

		ObjectContext context = cayenneService.newContext();
		WebSiteVersion versionToDelete = context.localObject(webSiteVersionToDelte);

		for (WebSiteLayout layoutToDelete : versionToDelete.getLayouts()) {
			context.deleteObjects(layoutToDelete.getTemplates());
		}
		context.deleteObjects(versionToDelete.getLayouts());

		for (WebContent contentToDelete : versionToDelete.getContents()) {
			context.deleteObjects(contentToDelete.getWebContentVisibilities());
		}
		context.deleteObjects(versionToDelete.getContents());

		
		//find root menu for entry in menus tree
		if (!versionToDelete.getMenus().isEmpty()) {
			WebMenu rootMenu = versionToDelete.getMenus().get(0);
			while (rootMenu.getParentWebMenu() != null) {
				rootMenu = rootMenu.getParentWebMenu();
			}
			
			deleteChildrenMenus(rootMenu.getChildrenMenus(), context);
			context.deleteObject(rootMenu);
		}

		context.deleteObjects(versionToDelete.getMenus());

		context.deleteObjects(versionToDelete.getWebURLAliases());
		context.deleteObjects(versionToDelete.getWebNodes());
		context.deleteObjects(versionToDelete.getWebNodeTypes());

		context.deleteObjects(versionToDelete);

		context.commitChanges();
	}
	
	//recursively remove all childrenMenus then remove parent
	private void deleteChildrenMenus(List<WebMenu> webMenus, ObjectContext context) {
		List<WebMenu> copyList = webMenus;
		for (WebMenu webMenu : copyList) {
			if (!webMenu.getChildrenMenus().isEmpty()) {
				deleteChildrenMenus(webMenu.getChildrenMenus(), context);
			}
			context.deleteObject(webMenu);
		}
	}


	//delete all revisions older than 60 days, but always to keep at least 5 revisions, even if they are older
	@Override
	public void removeOldWebSiteVersions(WebSite webSite) {
		ObjectContext context = cayenneService.newContext();
		//delete all revisions older than 60 days
		Date deleteBeforeDate = DateUtils.addDays(new Date(), -60);

		SelectQuery query = new SelectQuery(WebSiteVersion.class);
		query.andQualifier(ExpressionFactory.matchExp(WebSiteVersion.WEB_SITE_PROPERTY, webSite));
		//exclude unpublished revisions yet
		query.andQualifier(ExpressionFactory.noMatchExp(WebSiteVersion.DEPLOYED_ON_PROPERTY, null));
		query.addOrdering(WebSiteVersion.DEPLOYED_ON_PROPERTY, SortOrder.DESCENDING);

		List<WebSiteVersion> allVersions = context.performQuery(query);

		//if number of revisions less than 5 (4 + 1 unpublished) - nothing to delete 
		if (allVersions.size() > KEEP_AT_LEAST) {
		
			List<WebSiteVersion> versionsToDelete = new ArrayList<>();

			//don't delete the last few
			versionsToDelete = allVersions.subList(KEEP_AT_LEAST, allVersions.size());
			//delete all revisions older than 60 days
			versionsToDelete = ExpressionFactory.lessExp(WebSiteVersion.DEPLOYED_ON_PROPERTY, deleteBeforeDate).filterObjects(versionsToDelete);

			for (WebSiteVersion version : versionsToDelete) {
				delete(version);
			}
		}
	}

	/**
	 * Executes deploySite.sh script passing specified file as a parameter.
	 * E.g.:
	 * 		/var/onCourse/scripts/deploySite.sh -s {siteVersion.getId()} -c {site.getSiteKey()}
	 */
	private void executeDeployScript(WebSiteVersion siteVersion) {
		String scriptPath = ContextUtil.getCmsDeployScriptPath();
		
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
		
		String userEmail = authenticationService.getUserEmail();
		
		if (userEmail != null) {
			scriptCommand.add("-e");
			scriptCommand.add(userEmail);
		}
		
		ProcessBuilder processBuilder = new ProcessBuilder(scriptCommand);
		
		try {
			processBuilder.start();
		} catch (Exception e) {
			logger.error(String.format("Error executing script '%s'", scriptPath), e);
		}
	}
	
}
