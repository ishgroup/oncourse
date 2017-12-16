package ish.oncourse.cayenne;

import ish.oncourse.model.Country;
import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.tapestry.ServiceTest;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TestCayenneCache extends ServiceTest {

    private static String TEST_COUNTRY_NAME = "TestCountryName";
    private ICayenneService cayenneService;

    @Before
    public void setup() throws Exception {
        initTest("ish.oncourse.services", "service", ServiceTestModule.class);

        cayenneService = getService(ICayenneService.class);
    }

    @Test
    public void testLocalCache() throws InterruptedException {
        ObjectContext sharedContext = cayenneService.newContext();

        Country country = get(sharedContext, QueryCacheStrategy.SHARED_CACHE);
        assertNull(country);
        createNewObject();

        //default ehcache.timeToLiveSeconds is 1 sec
        Thread.sleep(1000L);
        country = get(sharedContext, QueryCacheStrategy.SHARED_CACHE);
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
