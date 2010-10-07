package ish.oncourse.cms.services;

import ish.oncourse.cms.services.access.PageAccessDispatcher;
import ish.oncourse.services.security.AuthenticationService;
import ish.oncourse.services.security.IAuthenticationService;

import org.apache.tapestry5.ioc.ServiceBinder;

/**
 * A Tapestry IoC module definition for all common services.
 */
public class CMSServiceModule {
	public static void bind(ServiceBinder binder) {
		binder.bind(PageAccessDispatcher.class).withId("PageAccessDispatcher");
	}
}
