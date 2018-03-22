/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cayenne;

import ish.math.MoneyType;
import org.apache.cayenne.cache.QueryCache;
import org.apache.cayenne.configuration.Constants;
import org.apache.cayenne.configuration.ObjectContextFactory;
import org.apache.cayenne.configuration.server.ServerModule;
import org.apache.cayenne.di.Module;

/**
 * User: akoiro
 * Date: 8/12/17
 */
public class WillowCayenneModule implements Module {
	private QueryCache queryCache;

	WillowCayenneModule(QueryCache queryCache) {
		this.queryCache = queryCache;
	}


	@Override
	public void configure(org.apache.cayenne.di.Binder binder) {
		binder.bindMap(String.class, Constants.PROPERTIES_MAP).put(Constants.CI_PROPERTY, "true");
		ServerModule.contributeUserTypes(binder).add(MoneyType.class);
		binder.bind(ObjectContextFactory.class).toInstance(new WillowDataContextFactory());

		if (queryCache != null) {
			binder.bind(QueryCache.class).toInstance(queryCache);
			binder.bind(org.apache.cayenne.di.Key.get(QueryCache.class, "local")).toInstance(queryCache);
		}
	}
}