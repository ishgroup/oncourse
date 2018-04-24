package ish.oncourse.ui.services;

import ish.oncourse.services.visitor.ParsedContentVisitor;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.ImportModule;

@ImportModule({UIModule.class})
public class TestModule {
	public static void bind(ServiceBinder binder) {
		ish.oncourse.test.tapestry.TestModule.bind(binder);
	}

	public static void contributeApplicationDefaults(
			MappedConfiguration<String, String> configuration) {
		configuration.add(SymbolConstants.COMPACT_JSON, "false");
		configuration.add(SymbolConstants.COMPRESS_WHITESPACE, "false");
		configuration.add(ParsedContentVisitor.WEB_CONTENT_CACHE, "false");
	}
}
