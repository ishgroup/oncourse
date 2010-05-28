package ish.oncourse.website.services;

import ish.oncourse.services.ServiceModule;
import ish.oncourse.ui.services.UIModule;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.got5.tapestry5.clientresources.ClientResourcesConstants;
import org.got5.tapestry5.jquery.JQueryClientResourcesConstants;
import org.got5.tapestry5.jquery.services.JQueryModule;

/**
 * The module that is automatically included as part of the Tapestry IoC
 * registry.
 */
@SubModule( { JQueryModule.class, ServiceModule.class, UIModule.class })
public class AppModule {

	public static void contributeApplicationDefaults(
			MappedConfiguration<String, String> configuration) {
		configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
		configuration.add(ClientResourcesConstants.JAVASCRIPT_STACK,
				JQueryClientResourcesConstants.JAVASCRIPT_STACK_JQUERY);
	}
}
