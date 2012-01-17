package ish.oncourse.cms.services;

import ish.oncourse.cms.services.access.AuthenticationService;
import ish.oncourse.cms.services.access.IAuthenticationService;
import ish.oncourse.cms.services.access.PageAccessDispatcher;

import org.apache.tapestry5.ioc.ServiceBinder;

/**
 * A Tapestry IoC module definition for all common services.
 */
public class CMSServiceModule {

	public static void bind(ServiceBinder binder) {
		binder.bind(IAuthenticationService.class, AuthenticationService.class);
		binder.bind(PageAccessDispatcher.class).withId("PageAccessDispatcher");
	}
}
