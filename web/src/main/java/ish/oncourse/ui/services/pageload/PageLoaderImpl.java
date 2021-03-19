/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.services.pageload;

import org.apache.tapestry5.internal.pageload.ComponentAssembler;
import org.apache.tapestry5.internal.pageload.ComponentAssemblerSource;
import org.apache.tapestry5.internal.services.PageLoader;
import org.apache.tapestry5.internal.services.PersistentFieldManager;
import org.apache.tapestry5.internal.structure.ComponentPageElement;
import org.apache.tapestry5.internal.structure.Page;
import org.apache.tapestry5.internal.structure.PageImpl;
import org.apache.tapestry5.ioc.OperationTracker;
import org.apache.tapestry5.ioc.services.PerthreadManager;
import org.apache.tapestry5.services.ComponentClassResolver;
import org.apache.tapestry5.services.MetaDataLocator;
import org.apache.tapestry5.services.pageload.ComponentResourceSelector;

import static ish.oncourse.ui.services.pageload.PageLoadModule.PACKAGE;

/**
 * User: akoiro
 * Date: 23/11/17
 */
public class PageLoaderImpl implements PageLoader, ComponentAssemblerSource {

	private final ComponentClassResolver componentClassResolver;
	private final OperationTracker tracker;
	private final PersistentFieldManager persistentFieldManager;
	private final PerthreadManager perThreadManager;
	private final MetaDataLocator metaDataLocator;

	private final org.apache.tapestry5.internal.pageload.PageLoaderImpl pageLoaderDelegate;
	private final PageLoadService pageLoadService;


	public PageLoaderImpl(ComponentClassResolver componentClassResolver, OperationTracker tracker,
						  PersistentFieldManager persistentFieldManager,
						  PerthreadManager perThreadManager, MetaDataLocator metaDataLocator,
						  PageLoader pageLoaderDelegate, PageLoadService pageLoadService) {
		this.componentClassResolver = componentClassResolver;
		this.tracker = tracker;
		this.persistentFieldManager = persistentFieldManager;
		this.perThreadManager = perThreadManager;
		this.metaDataLocator = metaDataLocator;
		this.pageLoaderDelegate = (org.apache.tapestry5.internal.pageload.PageLoaderImpl) pageLoaderDelegate;
		this.pageLoadService = pageLoadService;
	}

	@Override
	public ComponentAssembler getAssembler(String className, ComponentResourceSelector selector) {
		return pageLoadService.getAssembler(className, selector,
				() -> {
					if (className.startsWith(PACKAGE)) pageLoaderDelegate.clearCache();
					return pageLoaderDelegate.getAssembler(className, selector);
				});
	}

	@Override
	public Page loadPage(final String logicalPageName, final ComponentResourceSelector selector) {
		final String pageClassName = componentClassResolver.resolvePageNameToClassName(logicalPageName);

		final long startTime = System.nanoTime();

		return tracker.invoke("Constructing instance of page class " + pageClassName, () -> {
			Page page = new PageImpl(logicalPageName, selector, persistentFieldManager, perThreadManager, metaDataLocator);

			ComponentAssembler assembler = getAssembler(pageClassName, selector);

			ComponentPageElement rootElement = assembler.assembleRootComponent(page);

			page.setRootElement(rootElement);

			// The page is *loaded* before it is attached to the request.
			// This is to help ensure that no client-specific information leaks
			// into the page's default state.

			page.loaded();

			long elapsedTime = System.nanoTime() - startTime;

			double elapsedMS = elapsedTime * 10E-7d;

			Page.Stats roughStats = page.getStats();

			page.setStats(new Page.Stats(elapsedMS, roughStats.componentCount, roughStats.weight));

			return page;
		});
	}

}
