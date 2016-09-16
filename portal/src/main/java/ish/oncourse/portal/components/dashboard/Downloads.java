/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.components.dashboard;

import ish.oncourse.model.AttachmentType;
import ish.oncourse.model.Document;
import ish.oncourse.model.DocumentVersion;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.PortalUtils;
import ish.oncourse.services.binary.IBinaryDataService;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

public class Downloads {
	
	@Inject
	private IPortalService portalService;

	@Property
	private Document document;

	@Property
	private List<Document> documents;
	
	@Property
	private int documentsCount;

	@Inject
	private IBinaryDataService binaryDataService;
	
	
	@SetupRender
	public void setupRender() {
		documents = portalService.getResources();
		documentsCount = documents.size();
		if (documentsCount > 3) {
			documents = Document.MODIFIED.desc().orderedList(documents).subList(0, 3);
		}
	}

	public boolean needExtension() {
		return PortalUtils.needExtension(document);
	}

	public String getUrl() {
		return binaryDataService.getUrl(document);
	}

	public String getExtension() {
		DocumentVersion currentVersion = document.getCurrentVersion();

		if (currentVersion.getMimeType() == null) {
			return "";
		}
		return AttachmentType.getExtentionByMimeType(currentVersion.getMimeType());
	}
	
}
