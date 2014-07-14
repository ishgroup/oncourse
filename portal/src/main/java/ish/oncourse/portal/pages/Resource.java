/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.pages;

import ish.oncourse.model.Document;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.s3.IS3Service;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.net.MalformedURLException;
import java.net.URL;

public class Resource {

	public static final Long URL_EXPIRE_TIMEOUT = 3600000L;

	@Inject
	private IWebSiteService webSiteService;
	
	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IS3Service s3Service;
	
	@Inject
	private PreferenceController preferenceController;
	
	Object onActivate(String fileUuid) {
		Document document = getDocumentByUuid(fileUuid);
		
		if (document == null) {
			return PageNotFound.class;
		}

		try {
			return new URL(s3Service.getTemporaryUrl(
					preferenceController.getStorageBucketName(), document.getFileUUID(), URL_EXPIRE_TIMEOUT));
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	private Document getDocumentByUuid(String uuid) {
		SelectQuery query = new SelectQuery(Document.class, 
				ExpressionFactory.matchExp(Document.FILE_UUID_PROPERTY, uuid).andExp(
				ExpressionFactory.matchExp(Document.COLLEGE_PROPERTY, webSiteService.getCurrentCollege())));
		
		return (Document) Cayenne.objectForQuery(cayenneService.sharedContext(), query);
	}
}
