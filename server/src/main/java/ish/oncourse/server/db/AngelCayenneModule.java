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
package ish.oncourse.server.db;

import ish.oncourse.cache.ExpirableMapQueryCacheProvider;
import org.apache.cayenne.cache.QueryCache;
import org.apache.cayenne.configuration.Constants;
import org.apache.cayenne.configuration.ObjectContextFactory;
import org.apache.cayenne.configuration.server.ServerModule;
import org.apache.cayenne.di.Binder;
import org.apache.cayenne.di.Module;


/**
 */
public class AngelCayenneModule implements Module {
	/**
	 * @see org.apache.cayenne.di.Module#configure(Binder)
	 */
	@Override
	public void configure(Binder binder) {
		// since cayenne 4.1.B2 sync between contexts is turned off, it should be turned on
		ServerModule.contributeProperties(binder)
				.put(Constants.SERVER_CONTEXTS_SYNC_PROPERTY,
						String.valueOf(true));
		binder.bind(ObjectContextFactory.class).to(AngelDataContextFactory.class);

		// QueryCache will be bound to singleton instance of ExpirableMapQueryCache
		binder.bind(QueryCache.class).toProviderInstance(new ExpirableMapQueryCacheProvider(100));
	}
}
