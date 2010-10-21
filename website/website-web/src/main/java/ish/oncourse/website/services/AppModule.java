/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.website.services;

import org.apache.log4j.Logger;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.services.HttpServletRequestFilter;
import org.apache.tapestry5.services.linktransform.PageRenderLinkTransformer;

import ish.oncourse.linktransform.PageLinkTransformer;
import ish.oncourse.linktransform.URLRewriteRequestFilter;
import ish.oncourse.model.services.ModelModule;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.ui.services.UIModule;

/**
 * The module that is automatically included as part of the Tapestry IoC registry.
 */
@SubModule({ ModelModule.class, ServiceModule.class, UIModule.class })
public class AppModule {

	private static final Logger LOGGER = Logger.getLogger(AppModule.class);

	public static void contributeApplicationDefaults(MappedConfiguration<String, String> configuration) {
		configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
		configuration.add(SymbolConstants.COMPACT_JSON, "false");
		configuration.add(SymbolConstants.COMPRESS_WHITESPACE, "false");
	}

	@Contribute(PageRenderLinkTransformer.class)
	@Primary
	public static void provideURLRewriting(OrderedConfiguration<PageRenderLinkTransformer> configuration) {
		configuration.addInstance("PageLinkRule", PageLinkTransformer.class);
	}

	public static void contributeHttpServletRequestHandler(OrderedConfiguration<HttpServletRequestFilter> configuration) {
		configuration.addInstance("URLRewriteFilter", URLRewriteRequestFilter.class);
	}
}
