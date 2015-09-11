package ish.oncourse.services.persistence;

import ish.oncourse.services.cache.EHQueryCacheProvider;
import ish.oncourse.services.cache.NoopQueryCache;
import ish.oncourse.util.ContextUtil;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.management.ManagementService;
import org.apache.cayenne.cache.QueryCache;
import org.apache.cayenne.configuration.ObjectContextFactory;
import org.apache.cayenne.di.Binder;
import org.apache.cayenne.di.Key;
import org.apache.cayenne.di.Module;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;

public class ISHModule implements Module {
	private static final Logger logger = LogManager.getLogger();

	@Override
	public void configure(Binder binder) {
		binder.bind(ObjectContextFactory.class).to(ISHObjectContextFactory.class);

		if (ContextUtil.isQueryCacheEnabled()) {
			binder.bind(CacheManager.class).toInstance(buildCacheManager());

			binder.bind(QueryCache.class).toProvider(EHQueryCacheProvider.class);
			binder.bind(Key.get(QueryCache.class, ISHObjectContextFactory.QUERY_CACHE_INJECTION_KEY)).toProvider(EHQueryCacheProvider.class);
		} else {
			binder.bind(QueryCache.class).toInstance(new NoopQueryCache());
			binder.bind(Key.get(QueryCache.class, ISHObjectContextFactory.QUERY_CACHE_INJECTION_KEY)).toInstance(new NoopQueryCache());
		}
	}

	private CacheManager buildCacheManager() {

		CacheManager cacheManager = CacheManager.create(ISHModule.class.getClassLoader().getResource("ehcache.xml"));

		Integer cacheCapacity = ContextUtil.getCacheCapacity();

		if (cacheCapacity != null) {
			cacheManager.getConfiguration().getDefaultCacheConfiguration().setMaxEntriesLocalHeap(cacheCapacity);
		}
		try {
			MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
			UnregisterMBeans.valueOf(cacheManager, mBeanServer).unregister();
			ManagementService.registerMBeans(cacheManager, mBeanServer, true, true, true, true);
		} catch (Exception e) {
			logger.error("Cannot register MBeans for  cacheManager \"{}\".",cacheManager.getName(), e);
		}
		return cacheManager;
	}
}
