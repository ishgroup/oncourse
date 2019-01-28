/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.website.services;

import ish.oncourse.cayenne.cache.ICacheEnabledService;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;

import static ish.oncourse.cayenne.cache.ICacheEnabledService.CacheDisableReason.NONE;

/**
 * By default cache is enabled.
 * When disabling cache need to set reason
 *
 * @see CacheDisableReason
 */
public class CacheEnabledService implements ICacheEnabledService {

	private static final ThreadLocal<Entry<CacheDisableReason, Boolean>> threadLocal =
			ThreadLocal.withInitial(() -> new SimpleImmutableEntry<>(NONE, true));

	@Override
	public boolean isCacheEnabled() {
		return threadLocal.get().getValue();
	}

	@Override
	public void setCacheEnabled(Boolean enabled) {
		setCacheEnabled(NONE, enabled);
	}

	public void setCacheEnabled(CacheDisableReason reason, Boolean enabled) {
		threadLocal.set(new SimpleImmutableEntry<>(reason, enabled));
	}

	@Override
	public CacheDisableReason getDisableReason() {
		return threadLocal.get().getKey();
	}
}
