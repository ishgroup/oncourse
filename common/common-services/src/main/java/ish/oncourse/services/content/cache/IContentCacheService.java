/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.content.cache;

/**
 * User: akoiro
 * Date: 8/09/2016
 */
public interface IContentCacheService<K, V> {
	void put(K key, V value);

	V get(K key);

	void remove(K key);
}
