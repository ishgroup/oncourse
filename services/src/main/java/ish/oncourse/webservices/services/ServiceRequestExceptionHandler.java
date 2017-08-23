/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.services;

import ish.oncourse.util.UIRequestExceptionHandler;
import ish.oncourse.webservices.exception.PaymentNotFoundException;
import ish.oncourse.webservices.pages.PaymentNotFound;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ComponentSource;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.ResponseRenderer;

/**
 * User: akoiro
 * Date: 23/8/17
 */
public class ServiceRequestExceptionHandler extends UIRequestExceptionHandler {
	@Inject
	public ServiceRequestExceptionHandler(ComponentSource componentSource,
										  ResponseRenderer renderer,
										  Request request, Response response) {
		super(componentSource, renderer, request, response,
				UIRequestExceptionHandler.DEFAULT_ERROR_PAGE,
				StringUtils.EMPTY, false);
	}

	@Override
	public String getErrorPageName(Throwable exception) {
		Throwable cause = exception.getCause();
		if (cause != null) {
			// Trying to get possible PaymentNotFoundException, which is
			// enclosed by TapestryException and RenderQueueException
			cause = cause.getCause();
		}
		String exceptionPageName = null;
		if (cause != null && cause instanceof PaymentNotFoundException) {
			exceptionPageName = PaymentNotFound.class.getSimpleName();
			exception = cause;
		} else {
			exceptionPageName = UIRequestExceptionHandler.DEFAULT_ERROR_PAGE;
		}
		return exceptionPageName;
	}
}
