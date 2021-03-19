/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.components;

import ish.oncourse.services.content.cache.IContentCacheService;
import ish.oncourse.services.content.cache.IContentKeyFactory;
import ish.oncourse.services.content.cache.WillowContentKey;
import org.apache.cayenne.PersistentObject;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * User: akoiro
 * Date: 8/09/2016
 */
public class Cache {
	@Inject
	private IContentCacheService<WillowContentKey, String> contentCacheService;

	@Inject
	private IContentKeyFactory<PersistentObject, WillowContentKey> contentKeyFactory;


	@Inject
	private ComponentResources componentResources;

	@Parameter(required = true, allowNull = false)
	private PersistentObject key;

	private String element = componentResources.getElementName();

	private org.apache.tapestry5.dom.Element currentElement;

	private WillowContentKey cacheKey;

	@SetupRender
	public boolean setupRender(MarkupWriter markupWriter) {
		cacheKey = contentKeyFactory.createKey(componentResources.getNestedId(), key);

		String cachedContent = contentCacheService.get(cacheKey);
		if (cachedContent != null) {
			markupWriter.writeRaw(cachedContent);
			return false;
		} else {
			return true;
		}
	}

	@BeginRender
	public void beginRender(MarkupWriter markupWriter) {
		currentElement = markupWriter.element(element);
		componentResources.renderInformalParameters(markupWriter);
	}


	@AfterRender
	public void afterRender(MarkupWriter markupWriter) {
		// end markup
		markupWriter.end();
		contentCacheService.put(cacheKey, currentElement.toString());
	}

	@CleanupRender
	public void cleanupRender() {
	}

}
