/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.services.pageload;

import ish.oncourse.tapestry.IWillowComponentRequestSelectorAnalyzer;
import ish.oncourse.ui.services.pageload.template.ComponentTemplateSourceImpl;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.services.ComponentTemplateSource;
import org.apache.tapestry5.internal.services.PageLoader;
import org.apache.tapestry5.internal.services.PageSource;
import org.apache.tapestry5.internal.services.PersistentFieldManager;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OperationTracker;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Decorate;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.PerthreadManager;
import org.apache.tapestry5.services.ComponentClassResolver;
import org.apache.tapestry5.services.MetaDataLocator;
import org.apache.tapestry5.services.UpdateListenerHub;
import org.apache.tapestry5.services.pageload.ComponentRequestSelectorAnalyzer;

/**
 * User: akoiro
 * Date: 23/11/17
 */
public class PageLoadModule {
	public static final String PACKAGE = "ish.oncourse";

	public static void bind(ServiceBinder binder) {
		binder.bind(PageLoadService.class, PageLoadService.class);
		binder.bind(IWillowComponentRequestSelectorAnalyzer.class, WillowComponentRequestSelectorAnalyzer.class).withId("WillowComponentRequestSelectorAnalyzer");
	}


	@Decorate(serviceInterface = PageLoader.class)
	public PageLoader decoratePageLoader(ComponentClassResolver componentClassResolver, OperationTracker tracker,
										 PersistentFieldManager persistentFieldManager, PerthreadManager perThreadManager,
										 MetaDataLocator metaDataLocator, PageLoader pageLoader, PageLoadService pageLoadService) {
		return new PageLoaderImpl(componentClassResolver,
				tracker,
				persistentFieldManager,
				perThreadManager,
				metaDataLocator,
				pageLoader, pageLoadService);
	}


	public PageSource buildPageSourceOverride(PageLoadService pageLoadService) {
		return new PageSourceImpl(pageLoadService);
	}

	public ComponentTemplateSource buildComponentTemplateSourceOverride(@Inject @Symbol(SymbolConstants.PRODUCTION_MODE)
																				boolean productionMode, @Inject PageLoadService webCacheService, UpdateListenerHub hub) {
		ComponentTemplateSourceImpl templateSource = new ComponentTemplateSourceImpl(productionMode, webCacheService);
		hub.addUpdateListener(templateSource);
		return templateSource;
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
										  @Local ComponentTemplateSource componentTemplateSourceOverride) {
		configuration.add(ComponentTemplateSource.class, componentTemplateSourceOverride);
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
										  @Local PageSource pageSource) {
		configuration.add(PageSource.class, pageSource);
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
										  @Local IWillowComponentRequestSelectorAnalyzer analyzer) {

		configuration.add(ComponentRequestSelectorAnalyzer.class, analyzer);
	}
}
