/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cayenne;

import org.apache.cayenne.cache.QueryCache;

/**
 * User: akoiro
 * Date: 8/12/17
 */
public class WillowCayenneModuleBuilder {

	private QueryCache queryCache;

	public WillowCayenneModuleBuilder queryCache(QueryCache queryCache) {
		this.queryCache = queryCache;
		return this;
	}

	public WillowCayenneModule build() {
		return new WillowCayenneModule(queryCache);
	}
}
