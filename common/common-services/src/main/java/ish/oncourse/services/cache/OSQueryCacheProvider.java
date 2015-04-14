package ish.oncourse.services.cache;

import com.opensymphony.oscache.general.GeneralCacheAdministrator;
import ish.oncourse.util.ContextUtil;
import org.apache.cayenne.ConfigurationException;
import org.apache.cayenne.cache.OSQueryCache;
import org.apache.cayenne.di.Provider;

import java.util.Properties;

public class OSQueryCacheProvider implements Provider<OSQueryCache> {
	
	private static final String CACHE_CAPACITY_KEY = "cache.capacity";

	@Override
	public OSQueryCache get() throws ConfigurationException {
		OSCacheAdministrator admin = new OSCacheAdministrator();
		
		Properties props = admin.getProperties();
		
		Integer cacheCapacity = ContextUtil.getOSCacheCapacity();
		
		if (cacheCapacity != null) {
			props.setProperty(CACHE_CAPACITY_KEY, String.valueOf(cacheCapacity));
		}
		
		return new OSQueryCache(admin, props);
	}
	
	static class OSCacheAdministrator extends GeneralCacheAdministrator {
		
		public OSCacheAdministrator() {
			super();
		}
		
		public OSCacheAdministrator(Properties properties) {
			super(properties);
		}
		
		public Properties getProperties() {
			return config.getProperties();
		}
		
	}

}
