/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.cache;

import org.apache.cayenne.ConfigurationException;
import org.apache.cayenne.di.Provider;

/**
 * Default {@link Provider} implementation for {@link ExpirableMapQueryCache}.
 */
public class ExpirableMapQueryCacheProvider implements Provider<ExpirableMapQueryCache> {

	private int queryCacheSize;

	public ExpirableMapQueryCacheProvider(int queryCacheSize) {
		this.queryCacheSize = queryCacheSize;
	}

	@Override
	public ExpirableMapQueryCache get() throws ConfigurationException {
		return new ExpirableMapQueryCache(queryCacheSize, ExpirableMapQueryCache.DEFAULT_REFRESH_TIMEOUT);
	}
}
