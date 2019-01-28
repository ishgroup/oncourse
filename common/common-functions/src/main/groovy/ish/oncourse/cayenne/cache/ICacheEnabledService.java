/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cayenne.cache;

import static ish.oncourse.cayenne.cache.ICacheEnabledService.CacheDisableReason.NONE;

/**
 * @see JCacheQueryCache#get
 * @see ish.oncourse.services.site.WebSiteVersionService#isEditor
 */
public interface ICacheEnabledService {

	default boolean isCacheEnabled() {
		return true;
	}

	void setCacheEnabled(Boolean enabled);

	void setCacheEnabled(CacheDisableReason reason, Boolean enabled);

	default CacheDisableReason getDisableReason() {
		return NONE;
	}

	enum CacheDisableReason {
		EDITOR,
		PUBLISHER,
		NONE
	}
}
