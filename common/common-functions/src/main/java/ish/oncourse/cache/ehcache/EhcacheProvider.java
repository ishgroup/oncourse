/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cache.ehcache;

import ish.oncourse.cache.ICacheFactory;
import ish.oncourse.cache.jcache.JCacheProvider;
import org.apache.cayenne.configuration.RuntimeProperties;
import org.apache.cayenne.di.Inject;

/**
 * User: akoiro
 * Date: 3/4/18
 */
public class EhcacheProvider extends JCacheProvider {
	public EhcacheProvider(@Inject RuntimeProperties properties) {
		super(properties);
	}

	@Override
	public <K, V> ICacheFactory<K, V> createFactory(Class<K> keyType, Class<V> valueType) {
		return new EhcacheFactory<>(cacheManager, keyType, valueType);
	}
}
