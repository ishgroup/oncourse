package ish.oncourse.cayenne;

import ish.oncourse.cache.ICacheFactory;
import ish.oncourse.cache.ICacheProvider;
import ish.oncourse.cache.caffeine.CaffeineFactory;
import ish.oncourse.cache.caffeine.CaffeineProvider;
import ish.oncourse.cayenne.cache.ICacheEnabledService;
import ish.oncourse.cayenne.cache.JCacheQueryCache;
import ish.oncourse.model.Country;
import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.tapestry.ServiceTest;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.junit.Before;
import org.junit.Test;

import javax.cache.CacheManager;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TestCayenneCache extends ServiceTest {

	private static String TEST_COUNTRY_NAME = "TestCountryName";
	private ICayenneService cayenneService;

	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.services", "service",
				createQueryCache(),
				ServiceTestModule.class);

		cayenneService = getService(ICayenneService.class);
	}

	private JCacheQueryCache createQueryCache() {
		return new JCacheQueryCache(new ICacheProvider() {
			@Override
			public CacheManager getCacheManager() {
				return new CaffeineProvider().getCacheManager();
			}

			@Override
			public <K, V> ICacheFactory<K, V> createFactory(Class<K> keyType, Class<V> valueType) {
				return new CaffeineFactory<K, V>(getCacheManager(), keyType, valueType,
						CaffeineFactory.createDefaultConfig(keyType, valueType, 10, 1, TimeUnit.SECONDS)) {
				};
			}
		}, new ICacheEnabledService() {
			@Override
			public boolean isCacheEnabled() {
				return true;
			}

			@Override
			public void setCacheEnabled(Boolean enabled) {

			}
		});
	}

	@Test
	public void testLocalCache() throws InterruptedException {
		ObjectContext sharedContext = cayenneService.newContext();

		Country country = get(sharedContext, QueryCacheStrategy.LOCAL_CACHE);
		assertNull(country);
		createNewObject();

		//default ehcache.timeToLiveSeconds is 1 sec
		Thread.sleep(1000L);
		country = get(sharedContext, QueryCacheStrategy.LOCAL_CACHE);
		assertNotNull(country);

	}


	@Test
	public void testSharedCache() throws InterruptedException {
		ObjectContext sharedContext = cayenneService.newContext();

		Country country = get(sharedContext, QueryCacheStrategy.SHARED_CACHE);
		assertNull(country);
		createNewObject();

		//default ehcache.timeToLiveSeconds is 1 sec
		Thread.sleep(1000L);
		country = get(sharedContext, QueryCacheStrategy.SHARED_CACHE);
		assertNotNull(country);

	}


	private Country get(ObjectContext sharedContext, QueryCacheStrategy queryCacheStrategy) {
		return ObjectSelect.query(Country.class)
				.where(Country.NAME.eq(TEST_COUNTRY_NAME))
				.cacheStrategy(queryCacheStrategy, Country.class.getSimpleName())
				.selectOne(sharedContext);
	}


	private void createNewObject() {
		ObjectContext newContext = cayenneService.newContext();

		Country country = newContext.newObject(Country.class);
		country.setName(TEST_COUNTRY_NAME);
		country.setIshVersion(999L);

		newContext.commitChanges();
	}
}
