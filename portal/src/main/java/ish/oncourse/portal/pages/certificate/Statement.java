/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.pages.certificate;

import ish.oncourse.components.ISHCommon;
import ish.oncourse.portal.certificate.Model;
import ish.oncourse.portal.certificate.ModelBuilder;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceControllerFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.Request;

/**
 * User: akoiro
 * Date: 8/08/2016
 */
public class Statement extends ISHCommon {

	@Inject
	private Request request;

	@Inject
	private PreferenceControllerFactory preferenceControllerFactory;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private PageRenderLinkSource pageRenderLinkSource;

	@Property
	private Model model;

	@Property
	private Model.Module module;

	private ModelBuilder builder;


	public Object onActivate() {
		if (model == null) {
			return pageRenderLinkSource.createPageRenderLink(Verify.class);
		} else {
			return null;
		}
	}

	public Object onActivate(String code) {
		code =  StringUtils.trimToNull(code);
		if (code == null) {
			return pageRenderLinkSource.createPageRenderLink(Verify.class);
		}

		builder = ModelBuilder.valueOf(code, cayenneService, preferenceControllerFactory);
		switch (builder.build()) {
			case emptyCode:
				return pageRenderLinkSource.createPageRenderLink(Verify.class);
			case certificateNotFound:
				return pageRenderLinkSource.createPageRenderLinkWithContext(Invalid.class);
			case successFull:
				model = builder.getModel();
				return null;
			default:
				throw new IllegalArgumentException();
		}
	}
}
