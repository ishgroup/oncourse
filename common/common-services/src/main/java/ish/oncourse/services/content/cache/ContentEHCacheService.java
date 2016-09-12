/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.content.cache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * User: akoiro
 * Date: 8/09/2016
 */
public class ContentEHCacheService implements IContentCacheService<WillowContentKey, String> {

	@Inject
	private CacheManager cacheManager;

	@Override
	public void put(WillowContentKey key, String value) {
		Ehcache ehcache = cacheManager.addCacheIfAbsent(key.getElementName());
		ehcache.put(new Element(key, value));
	}

	@Override
	public String get(WillowContentKey key) {
		Ehcache ehcache = cacheManager.getCache(key.getElementName());
		if (ehcache != null) {
			Element element = ehcache.get(key);
			if (element != null) {
				return (String) element.getObjectValue();
			}
		}
		return null;
	}


	@Override
	public void remove(WillowContentKey key) {
		Ehcache ehcache = cacheManager.getCache(key.getElementName());
		if (ehcache != null) {
			ehcache.remove(key);
		}
	}
}
