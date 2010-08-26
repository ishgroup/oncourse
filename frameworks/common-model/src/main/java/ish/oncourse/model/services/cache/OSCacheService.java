package ish.oncourse.model.services.cache;

import java.util.Properties;

import org.apache.cayenne.cache.OSQueryCache;
import org.apache.cayenne.cache.QueryCache;

import com.opensymphony.oscache.base.CacheEntry;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;
import org.apache.log4j.Logger;

public class OSCacheService implements ICacheService {

	private static final Logger LOGGER = Logger.getLogger(OSCacheService.class);

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

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Attempting to retrieve resource with key '"
					+ key + "' from cache (refreshPeriod: " + refreshPeriod
					+ ", cron: " + cron + ")");
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
