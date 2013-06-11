package ish.oncourse.website.services.html;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.html.ICacheMetaProvider;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.website.services.AppModule;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class CacheMetaProviderTest extends ServiceTest{

	private ICacheMetaProvider cacheMetaProvider;
	private ICookiesService cookiesService;
	@Before
	public void setup() throws Exception {

		initTest("ish.oncourse.website", "", "/", AppModule.class);
		InputStream st = CacheMetaProviderTest.class.getClassLoader().getResourceAsStream("ish/oncourse/website/services/html/CacheMetaProviderTest.xml");
		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DataSource refDataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);
		cacheMetaProvider = getService("WebCacheMetaProvider", ICacheMetaProvider.class);
		cookiesService = getService(ICookiesService.class);
	}

	@Test
	public void test()
	{
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



}
