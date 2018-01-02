package ish.oncourse.website.services.html;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.html.ICacheMetaProvider;
import ish.oncourse.test.LoadDataSet;
import ish.oncourse.test.tapestry.ServiceTest;
import ish.oncourse.test.tapestry.TestModule;
import ish.oncourse.website.services.AppModule;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CacheMetaProviderTest extends ServiceTest {

	private ICacheMetaProvider cacheMetaProvider;
	private ICookiesService cookiesService;

	@Before
	public void setup() throws Exception {

		initTest("ish.oncourse.website", "app", Module.class, AppModule.class);
		new LoadDataSet().dataSetFile("ish/oncourse/website/services/html/CacheMetaProviderTest.xml").load(getDataSource());
		cacheMetaProvider = getService(ICacheMetaProvider.class);
		cookiesService = getService(ICookiesService.class);
	}

	@Test
	public void test() {
		String value = cacheMetaProvider.getMetaContent();
		assertEquals("public", value);

		cookiesService.writeCookieValue(CourseClass.SHORTLIST_COOKIE_KEY, "1,2,3,4");
		value = cacheMetaProvider.getMetaContent();
		assertEquals("no-cache", value);

		cookiesService.removeCookieValue(CourseClass.SHORTLIST_COOKIE_KEY);
		value = cacheMetaProvider.getMetaContent();
		assertEquals("public", value);

		cookiesService.writeCookieValue(Discount.PROMOTIONS_KEY, "1,2,4,5");
		value = cacheMetaProvider.getMetaContent();
		assertEquals("no-cache", value);

		cookiesService.writeCookieValue(Discount.PROMOTIONS_KEY, "");
		value = cacheMetaProvider.getMetaContent();
		assertEquals("public", value);

	}


	public static class Module {
		public static void bind(ServiceBinder binder) {
			binder.bind(ServerRuntime.class, resources -> TestModule.serverRuntime.get());
		}
	}


}
