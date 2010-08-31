package ish.oncourse.website.services;

import ish.oncourse.model.services.ModelModule;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.ui.services.UIModule;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.SubModule;

/**
 * The module that is automatically included as part of the Tapestry IoC
 * registry.
 */
@SubModule( { ModelModule.class, ServiceModule.class, UIModule.class /*, JQueryModule.class*/})
public class AppModule {

	public static void contributeApplicationDefaults(
			MappedConfiguration<String, String> configuration) {

		configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
		configuration.add(SymbolConstants.COMPACT_JSON, "false");
		configuration.add(SymbolConstants.COMPRESS_WHITESPACE, "false");

		// TODO: Reinstate when Tapestry-jQuery suports 5.2
/*
		configuration.add(
				ClientResourcesConstants.JAVASCRIPT_STACK,
				JQueryClientResourcesConstants.JAVASCRIPT_STACK_JQUERY);
 */
	}
	
	public static void contributeIgnoredPathsFilter(Configuration<String> configuration){
	    configuration.add("/servlet/binarydata");
	    configuration.add("/servlet/autosuggest");
	}
	
}
