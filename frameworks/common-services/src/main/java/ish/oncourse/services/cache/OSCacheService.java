package ish.oncourse.services.cache;

import java.util.Properties;

import org.apache.cayenne.cache.OSQueryCache;
import org.apache.cayenne.cache.QueryCache;

import com.opensymphony.oscache.base.CacheEntry;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;

public class OSCacheService implements ICacheService {

	private ExtendedGeneralCacheAdministrator cache;
	private OSQueryCache cayenneCache;

	public OSCacheService() {
		cache = new ExtendedGeneralCacheAdministrator();
		cayenneCache = new OSQueryCache(cache, cache.getProperties());
	}

	public QueryCache cayenneCache() {
		return cayenneCache;
	}

	public <T> T get(String key, CachedObjectProvider<T> objectProvider,
			CacheGroup... cacheGroups) {

		if (key == null) {
			throw new NullPointerException("Null cache key");
		}

		int refreshPeriod = CacheEntry.INDEFINITE_EXPIRY;
		String cron = null;

		int cacheGroupsSize = cacheGroups != null ? cacheGroups.length : 0;
		String[] cacheGroupNames = new String[cacheGroupsSize];

		// use the first cache group settings to infer refresh policy
		if (cacheGroups != null && cacheGroups.length > 0) {

			for (int i = 0; i < cacheGroupsSize; i++) {
				cacheGroupNames[i] = cacheGroups[i].name();
			}
			
			refreshPeriod = cayenneCache.getRrefreshPeriod(cacheGroupNames[0]);
			cron = cayenneCache.getCronExpression(cacheGroupNames[0]);
		}

		try {
			return (T) cache.getFromCache(key, refreshPeriod, cron);
		} catch (NeedsRefreshException e) {

			T object;

			try {
				object = objectProvider.create();

			} catch (Exception providerException) {
				cache.cancelUpdate(key);
				throw new RuntimeException(providerException);
			}

			cache.putInCache(key, object, cacheGroupNames);
			return object;
		}
	}

	// an extension of GeneralCacheAdministrator that exposes cache properties
	final class ExtendedGeneralCacheAdministrator extends
			GeneralCacheAdministrator {
		Properties getProperties() {
			return config.getProperties();
		}
	}
}
