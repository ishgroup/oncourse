/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.website.services;

import ish.oncourse.cayenne.cache.ICacheEnabledService;

public class CacheEnabledService implements ICacheEnabledService {

    private static final ThreadLocal<Boolean> ThreadLocalBoolean = new ThreadLocal<Boolean>();

    @Override
    public boolean isCacheEnabled() {
        return ThreadLocalBoolean.get();
    }

    @Override
    public void setCacheEnabled(Boolean enabled) {
        ThreadLocalBoolean.set(enabled);
    }
}
