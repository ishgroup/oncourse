package ish.oncourse.website.services;

import ish.oncourse.model.services.ModelModule;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.ui.services.UIModule;
import ish.oncourse.website.linktransforms.PageLinkTransformer;
import ish.oncourse.website.linktransforms.URLRewriteRequestFilter;

import org.apache.log4j.Logger;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.services.HttpServletRequestFilter;
import org.apache.tapestry5.services.linktransform.PageRenderLinkTransformer;

/**
 * The module that is automatically included as part of the Tapestry IoC
 * registry.
 */
@SubModule({ ModelModule.class, ServiceModule.class, UIModule.class })
public class AppModule {

	private static final Logger LOGGER = Logger.getLogger(AppModule.class);

	public static void contributeApplicationDefaults(
			MappedConfiguration<String, String> configuration) {

		configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
		configuration.add(SymbolConstants.COMPACT_JSON, "false");
		configuration.add(SymbolConstants.COMPRESS_WHITESPACE, "false");
	}

	public static void contributeIgnoredPathsFilter(
			Configuration<String> configuration) {
		configuration.add("/servlet/binarydata");
		configuration.add("/servlet/autosuggest");
	}

	@Contribute(PageRenderLinkTransformer.class)
	@Primary
	public static void provideURLRewriting(
			OrderedConfiguration<PageRenderLinkTransformer> configuration) {
		configuration.addInstance("PageLinkRule", PageLinkTransformer.class);
	}

	public static void contributeHttpServletRequestHandler(
			OrderedConfiguration<HttpServletRequestFilter> configuration) {
		configuration.addInstance("URLRewriteFilter",
				URLRewriteRequestFilter.class);
	}

}
