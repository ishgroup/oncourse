package ish.oncourse.portal.access;

import ish.oncourse.model.Contact;
import ish.oncourse.portal.access.validate.AccessLinksValidatorFactory;
import ish.oncourse.services.cookies.ICookiesOverride;
import ish.oncourse.services.cookies.ICookiesService;
import ish.util.UrlUtil;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.services.Request;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static ish.oncourse.portal.access.ProcessSignedRequest.Case.*;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class ProcessSignedRequest {
	private static final Logger logger = LogManager.getLogger();

	private final static String PARAM_contactId = "contactId";

	public final static String SESSION_ATTR_currentSignedPath = "prevSignedPath";
	private IAuthenticationService authenticationService;
	private AccessLinksValidatorFactory accessLinksValidatorFactory;
	private ICookiesOverride cookiesService;
	private ObjectContext context;
	private HttpServletRequest httpRequest;
	private Request request;

	private String prevPath;
	private Contact prevContact;

	private String currentPath;
	private Contact currentContact;

	private void parseRequest() {
		currentPath = request.getPath();
		String value = request.getParameter(PARAM_contactId);
		if (isNotBlank(value)) {
			currentContact = ObjectSelect.query(Contact.class).where(Contact.UNIQUE_CODE.eq(value)).selectOne(context);
			if (currentContact != null) {
				String signedUrl = httpRequest.getRequestURL() + "?" + httpRequest.getQueryString();
				boolean signed = UrlUtil.validateSignedPortalUrl(signedUrl, currentContact.getCollege().getWebServicesSecurityCode(), new Date());
				if (!signed) {
					logger.debug("Url {} is unsigned", signedUrl);
					currentContact = null;
				}
			} else {
				logger.debug("Cannot find user with UNIQUE_CODE {}", value);
			}
		}
	}

	public boolean process() {
		Case c = GetCase.valueOf(accessLinksValidatorFactory, prevPath, prevContact, currentPath, currentContact, request).getCase();
		switch (c) {
			case FIRST:
				authenticationService.storeCurrentUser(currentContact);
				cookiesService.writeCookieValue(SESSION_ATTR_currentSignedPath, currentPath);
				return true;
			case REFRESH:
			case LINKED:
				return true;
			case ANOTHER:
				authenticationService.logout();
				authenticationService.storeCurrentUser(currentContact);
				cookiesService.writeCookieValue(SESSION_ATTR_currentSignedPath, currentPath);
				return true;
			case INVALID:
				authenticationService.logout();
				cookiesService.removeCookieValue(ProcessSignedRequest.SESSION_ATTR_currentSignedPath);
				return false;
			case REGULAR:
			case AJAX:
			case POST:
				return false;
			default:
				throw new IllegalArgumentException();
		}
	}


	public static ProcessSignedRequest valueOf(IAuthenticationService authenticationService,
											   AccessLinksValidatorFactory accessLinksValidatorFactory,
	                                           ObjectContext context,
	                                           HttpServletRequest httpRequest,
	                                           Request request,
											   ICookiesOverride cookiesService) {
		ProcessSignedRequest result = new ProcessSignedRequest();
		result.cookiesService = cookiesService;
		result.accessLinksValidatorFactory = accessLinksValidatorFactory;
		result.authenticationService = authenticationService;
		result.context = context;
		result.httpRequest = httpRequest;
		result.request = request;
		result.prevPath = StringUtils.trimToNull(cookiesService.readCookieValue(SESSION_ATTR_currentSignedPath));
		result.prevContact = authenticationService.getUser();
		result.parseRequest();
		return result;
	}

	public enum Case {
		AJAX,
		POST,
		FIRST,
		REFRESH,
		LINKED,
		ANOTHER,
		INVALID,
		REGULAR
	}


	public static class GetCase {
		private AccessLinksValidatorFactory accessLinksValidatorFactory;

		private String prevPath;
		private Contact prevContact;

		private String currentPath;
		private Contact currentContact;
		private Request request;

		public Case getCase() {

			if (isAjax()) {
				return AJAX;
			}

			if (isPost()) {
				return POST;
			}

			if (isFirst()) {
				return FIRST;
			}

			if (isLinked()) {
				return LINKED;
			}

			if (isRefresh()) {
				return REFRESH;
			}

			if (isAnother()) {
				return ANOTHER;
			}

			if (isInvalid()) {
				return INVALID;
			}

			if (isReqular()) {
				return REGULAR;
			}

			throw new IllegalStateException();
		}

		private boolean isAjax() {
			return prevContact != null && request.isXHR();
		}

		private boolean isPost() {
			return prevContact != null && prevPath != null && "POST".equals(request.getMethod());
		}

		private boolean isReqular() {
			return prevPath == null && currentContact == null;
		}

		private boolean isInvalid() {
			return prevContact != null && prevPath != null && !prevPath.equals(currentPath) && currentContact == null;
		}

		private boolean isAnother() {
			return currentContact != null &&
					(prevContact != null && !currentContact.getId().equals(prevContact.getId()) ||
					(currentPath != null && prevPath != null && !currentPath.equals(prevPath) || prevPath == null));
		}

		private boolean isRefresh() {
			return isTheSamePath() && (isTheSameContact() || currentContact == null);
		}

		private boolean isFirst() {
			return prevContact == null && prevPath == null &&
					currentContact != null;
		}

		private boolean isTheSameContact() {
			return currentContact != null && prevContact != null && currentContact.getId().equals(prevContact.getId());
		}

		private boolean isTheSamePath() {
			return currentPath != null && prevPath != null && currentPath.equals(prevPath);
		}

		private boolean isLinked() {
			return prevPath != null && prevContact != null && accessLinksValidatorFactory.getBy(prevPath, currentPath).validate();
		}

		public static GetCase valueOf(AccessLinksValidatorFactory accessLinksValidatorFactory, String prevPath, Contact prevContact,
		                              String currentPath, Contact currentContact,
		                              Request request) {
			GetCase getCase = new GetCase();
			getCase.accessLinksValidatorFactory = accessLinksValidatorFactory;
			getCase.prevPath = prevPath;
			getCase.prevContact = prevContact;
			getCase.currentPath = currentPath;
			getCase.currentContact = currentContact;
			getCase.request = request;
			return getCase;
		}

	}
}
