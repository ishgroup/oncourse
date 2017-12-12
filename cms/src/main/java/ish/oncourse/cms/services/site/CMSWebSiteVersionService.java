/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cms.services.site;

import ish.oncourse.cms.services.access.IAuthenticationService;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebSiteVersion;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.*;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * CMS implementation of {@link IWebSiteVersionService}.
 * Current version is determined as staged version (WebSiteVersion.deployedOn == null).
 */
public class CMSWebSiteVersionService extends AbstractWebSiteVersionService {

	private static final Logger logger = LogManager.getLogger();
	
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
		return ObjectSelect.query(WebSiteVersion.class).
				localCache(WebSiteVersion.class.getSimpleName()).
				where(WebSiteVersion.WEB_SITE.eq(webSite)).
				and(WebSiteVersion.DEPLOYED_ON.isNull()).
				selectOne(webSite.getObjectContext());
	}

	@Override
	public void publish() {
        WebSitePublisher.valueOf(getCurrentVersion(),
				authenticationService.getSystemUser(),
				authenticationService.getUserEmail(),
				cayenneService.newContext()).publish();
	}

	@Override
	public void delete(WebSiteVersion webSiteVersionToDelte) {
		WebSiteVersionDelete.valueOf(webSiteVersionToDelte,
				getCurrentVersion(),
				GetDeployedVersion.valueOf(cayenneService.sharedContext(), webSiteService.getCurrentWebSite(), false).get(),
				cayenneService.newContext()).delete();
	}
	
	/**
		delete all revisions older than 60 days, but always keep at least 5 revisions, even if they are older
	 */
	@Override
	public void removeOldWebSiteVersions(WebSite webSite) {
		WebSiteVersionsDelete.valueOf(webSite,
				getCurrentVersion(),
				GetDeployedVersion.valueOf(cayenneService.sharedContext(), webSite, false).get(),
				cayenneService.newContext()).delete();
	}

	public WebSiteVersion getDeployedVersion(WebSite webSite) {
		return ObjectSelect.query(WebSiteVersion.class)
				.localCache(WebSiteVersion.class.getSimpleName())
				.and(WebSiteVersion.WEB_SITE.eq(webSite))
				.orderBy(WebSiteVersion.DEPLOYED_ON.desc())
				.limit(1).selectFirst(webSite.getObjectContext());
	}

	@Override
	public String getApplicationKey() {
		return null;
	}
}
