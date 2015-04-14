/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.enrol.services.linktransform;

import ish.oncourse.linktransform.PageIdentifier;
import ish.oncourse.model.Contact;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.internal.EmptyEventContext;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderRequestParameters;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.linktransform.PageRenderLinkTransformer;

import javax.servlet.http.HttpServletResponse;

public class EnrolPageLinkTransformer implements PageRenderLinkTransformer {

	private static final Logger logger = LogManager.getLogger();

	@Inject
	RequestGlobals requestGlobals;

	@Inject
	private IWebSiteService webSiteService;
	
	@Inject
	private ICookiesService cookiesService;
	
	@Override
	public Link transformPageRenderLink(Link defaultLink, PageRenderRequestParameters parameters) {
		logger.info("Rewrite OutBound: path is: {}", defaultLink.getBasePath());

		return defaultLink;
	}

	@Override
	public PageRenderRequestParameters decodePageRenderRequest(Request request) {

		final String path = request.getPath().toLowerCase();
		PageIdentifier pageIdentifier = PageIdentifier.getPageIdentifierByPath(path);

		String studentUniqCode = cookiesService.getCookieValue(Contact.STUDENT_PROPERTY);
		if (StringUtils.trimToNull(studentUniqCode) != null) {
			request.setAttribute(Contact.STUDENT_PROPERTY, studentUniqCode);
		}
		
		if (webSiteService.getCurrentWebSite() == null) {
			requestGlobals.getResponse().setStatus(HttpServletResponse.SC_NOT_FOUND);
			return new PageRenderRequestParameters(PageIdentifier.SiteNotFound.getPageName(), new EmptyEventContext(), false);
		}
		if (PageIdentifier.WaitingListForm.equals(pageIdentifier)) {
			//get courseId parameter from request path 
			String courseId = path.substring(path.lastIndexOf('/') + 1);
			//check that courseId is number, else abort WaitingListForm rendering
			if (!courseId.matches("\\d+")) {
				logger.warn("Wrong attribute:courseId for WaitingList Form, it should be correct course ID");
				return new PageRenderRequestParameters(PageIdentifier.PageNotFound.getPageName(), new EmptyEventContext(), false);
			} 
		}
		return null;
	}
}
