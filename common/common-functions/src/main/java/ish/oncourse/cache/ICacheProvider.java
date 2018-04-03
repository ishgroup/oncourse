/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cache;

import javax.cache.CacheManager;

/**
 * User: akoiro
 * Date: 3/4/18
 */
public interface ICacheProvider {
	CacheManager getCacheManager();

	<K, V> ICacheFactory<K, V> createFactory(Class<K> keyType, Class<V> valueType);
}
