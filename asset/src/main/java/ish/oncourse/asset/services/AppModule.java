package ish.oncourse.asset.services;

import ish.oncourse.model.services.ModelModule;
import ish.oncourse.services.ServiceModule;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.annotations.SubModule;

/**
 * The module that is automatically included as part of the Tapestry IoC
 * registry.
 */
@SubModule({ModelModule.class, ServiceModule.class})
public class AppModule {
	
	public static void contributeIgnoredPathsFilter(Configuration<String> configuration) {
		configuration.add("/binarydata");
		configuration.add("/autosuggest");
	}
}
