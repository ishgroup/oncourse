/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.enrol.services.linktransform;

import ish.oncourse.linktransform.PageIdentifier;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.log4j.Logger;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.internal.EmptyEventContext;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderRequestParameters;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.linktransform.PageRenderLinkTransformer;

import javax.servlet.http.HttpServletResponse;

public class EnrolPageLinkTransformer implements PageRenderLinkTransformer {

	private static final Logger LOGGER = Logger.getLogger(EnrolPageLinkTransformer.class);

	@Inject
	RequestGlobals requestGlobals;

	@Inject
	private IWebSiteService webSiteService;
	
	@Override
	public Link transformPageRenderLink(Link defaultLink, PageRenderRequestParameters parameters) {
		LOGGER.info("Rewrite OutBound: path is: " + defaultLink.getBasePath());

		return defaultLink;
	}

	@Override
	public PageRenderRequestParameters decodePageRenderRequest(Request request) {
		
		if (webSiteService.getCurrentWebSite() == null) {
			requestGlobals.getResponse().setStatus(HttpServletResponse.SC_NOT_FOUND);
			return new PageRenderRequestParameters(PageIdentifier.SiteNotFound.getPageName(), new EmptyEventContext(), false);
		}
		return null;
	}
}
