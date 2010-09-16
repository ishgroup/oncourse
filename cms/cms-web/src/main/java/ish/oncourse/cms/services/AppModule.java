package ish.oncourse.cms.services;

import ish.oncourse.model.services.ModelModule;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.ui.services.UIModule;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.services.Dispatcher;

/**
 * The module that is automatically included as part of the Tapestry IoC
 * registry.
 */
@SubModule({ ModelModule.class, ServiceModule.class, CMSServiceModule.class,
		UIModule.class })
		
public class AppModule {

	public static void contributeApplicationDefaults(
			MappedConfiguration<String, String> configuration) {

		configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
		configuration.add(SymbolConstants.COMPRESS_WHITESPACE, "false");
		configuration.add(SymbolConstants.COMPACT_JSON, "false");
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
