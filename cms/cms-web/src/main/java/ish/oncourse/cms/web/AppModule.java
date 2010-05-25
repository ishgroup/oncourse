package ish.oncourse.cms.web;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.services.Dispatcher;
import org.got5.tapestry5.clientresources.ClientResourcesConstants;
import org.got5.tapestry5.jquery.JQueryClientResourcesConstants;
import org.got5.tapestry5.jquery.services.JQueryModule;

import ish.oncourse.cms.services.security.AuthenticationService;
import ish.oncourse.cms.services.security.ComponentAccessDispatcher;
import ish.oncourse.cms.services.security.IAuthenticationService;
import ish.oncourse.cms.services.security.PageAccessDispatcher;
import ish.oncourse.ui.services.UIModule;

import ish.oncourse.services.ServiceModule;


/**
 * The module that is automatically included as part of the Tapestry IoC
 * registry.
 */
@SubModule({ JQueryModule.class, ServiceModule.class, UIModule.class })
public class AppModule {

	public static void bind(ServiceBinder binder) {
		binder.bind(IAuthenticationService.class, AuthenticationService.class);
		binder.bind(PageAccessDispatcher.class).withId("PageAccessDispatcher");
		binder.bind(ComponentAccessDispatcher.class).withId("ComponentAccessDispatcher");
	}

	public static void contributeApplicationDefaults(
			MappedConfiguration<String, String> configuration) {
		configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
		configuration.add(ClientResourcesConstants.JAVASCRIPT_STACK,
				JQueryClientResourcesConstants.JAVASCRIPT_STACK_JQUERY);
	}

	/**
	 * Contribute access control checker.
	 */
	public void contributeMasterDispatcher(
			OrderedConfiguration<Dispatcher> configuration,
			@InjectService("PageAccessDispatcher") Dispatcher pageAccessDispatcher) {

		configuration.add("PageAccessDispatcher", pageAccessDispatcher,
				"before:PageRender");
	}
}
