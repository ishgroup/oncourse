package ish.oncourse.cms.services;

import ish.oncourse.cms.services.access.AuthenticationService;
import ish.oncourse.cms.services.access.IAuthenticationService;
import ish.oncourse.cms.services.access.PageAccessDispatcher;
import ish.oncourse.cms.services.persistence.CMSCayenneService;
import ish.oncourse.cms.services.state.ISessionStoreService;
import ish.oncourse.cms.services.state.SessionStoreService;
import ish.oncourse.model.services.persistence.ICayenneService;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Local;

/**
 * A Tapestry IoC module definition for all common services.
 */
public class CMSServiceModule {

	public static void bind(ServiceBinder binder) {
		binder.bind(IAuthenticationService.class, AuthenticationService.class);
		binder.bind(PageAccessDispatcher.class).withId("PageAccessDispatcher");
		binder.bind(ISessionStoreService.class, SessionStoreService.class);
		binder.bind(ICayenneService.class, CMSCayenneService.class).withId(
				"ICayenneServiceOverride");
	}

	public static void contributeServiceOverride(
			MappedConfiguration<Class<?>, Object> configuration,
			@Local ICayenneService cayenneServiceOverride) {
		configuration.add(ICayenneService.class, cayenneServiceOverride);
	}
}
