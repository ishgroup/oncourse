/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.components.certificate;

import ish.oncourse.portal.pages.certificate.Invalid;
import ish.oncourse.portal.pages.certificate.Statement;
import ish.oncourse.portal.pages.certificate.Verify;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceControllerFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.Request;

/**
 * User: akoiro
 * Date: 10/08/2016
 */
public class Form {
	public static final String PARAM_code = "code";

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private PreferenceControllerFactory preferenceControllerFactory;

	@Inject
	private PageRenderLinkSource pageRenderLinkSource;

	@Inject
	private Request request;

	@InjectPage
	private Invalid invalid;

	@InjectPage
	private Verify verify;

	@InjectPage
	private Statement statement;

	Object onSuccess() {
		String code = StringUtils.trimToNull(request.getParameter(PARAM_code));
		if (code == null) {
			return null;
		} else {
			return pageRenderLinkSource.createPageRenderLinkWithContext(Statement.class, code);
		}
	}
}
