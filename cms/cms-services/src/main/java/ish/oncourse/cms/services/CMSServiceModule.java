package ish.oncourse.cms.services;

import ish.oncourse.cms.services.access.AuthenticationService;
import ish.oncourse.cms.services.access.IAuthenticationService;
import ish.oncourse.cms.services.access.PageAccessDispatcher;
import ish.oncourse.services.node.CmsWebNodeServiceOverride;
import ish.oncourse.services.node.IWebNodeService;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Local;

/**
 * A Tapestry IoC module definition for all common services.
 */
public class CMSServiceModule {

	public static void bind(ServiceBinder binder) {
		binder.bind(IAuthenticationService.class, AuthenticationService.class);
		binder.bind(IWebNodeService.class, CmsWebNodeServiceOverride.class).withId("CmsWebNodeServiceOverride");
		binder.bind(PageAccessDispatcher.class).withId("PageAccessDispatcher");
	}
	
	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local IWebNodeService webNodeServiceOverride) {
		configuration.add(IWebNodeService.class, webNodeServiceOverride);
	}
}
