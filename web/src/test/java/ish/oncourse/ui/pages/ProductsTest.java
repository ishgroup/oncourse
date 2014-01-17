package ish.oncourse.ui.pages;

import ish.oncourse.ui.services.DbEnabledTestModule;
import ish.oncourse.services.voucher.IVoucherService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.ui.services.TestModule;
import org.apache.tapestry5.dom.Document;
import org.apache.tapestry5.dom.Element;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;

import static org.junit.Assert.*;

public class ProductsTest extends ServiceTest {
	private static final String NO_PRODUCTS_AVAILABLE_CONTENT_BODY = "<h2>No products available</h2>";
	private static final String CONTENT_ELEMENT_ID = "content";
	private static final String PRODUCTS_PAGE = "ui/Products";
	private static final String AVAILABLE_FOR_PRODUCT_RENDER_VERSION = "development";
	private static final String AVAILABLE_FOR_PRODUCT_RENDER_BETA_VERSION = "5.0b1";
	private static final String UNAVAILABLE_FOR_PRODUCT_RENDER_VERSION = "4.0-SNAPSHOT";
	private static final String PAGE_NOT_FOUND_CONTENT_BODY = "<h2>Page Not Found</h2><p>The page you are looking for was not found. You may have used an outdated link or may have typed the address (URL) incorrectly.</p>";
	public static final String APP_PACKAGE = "ish.oncourse.website";
	public static final String CONTEXT_PATH = "src/main/resources/ish/oncourse/ui/pages";
	
	public void setup() throws Exception {
		initTest(APP_PACKAGE, "", CONTEXT_PATH, DbEnabledTestModule.class);
		InputStream st = ProductsTest.class.getClassLoader().getResourceAsStream("ish/oncourse/ui/pages/products/voucherDataSet.xml");
		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DataSource refDataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);
	}
	
	@Test
	public void testAvailableForProductsRenderVersion() throws Exception {
		System.setProperty(TestModule.TEST_COLLEGE_ANGEL_VERSION_PROPERTY, AVAILABLE_FOR_PRODUCT_RENDER_VERSION);
		setup();
		IVoucherService voucherService = getService("IVoucherServiceOverride", IVoucherService.class);
		assertNotNull("Voucher service should be inited", voucherService);
		assertTrue("5.0-development is a correct for render version", voucherService.isAbleToPurchaseProductsOnline());

		System.setProperty(TestModule.TEST_COLLEGE_ANGEL_VERSION_PROPERTY, AVAILABLE_FOR_PRODUCT_RENDER_BETA_VERSION);
		setup();
		voucherService = getService("IVoucherServiceOverride", IVoucherService.class);
		assertNotNull("Voucher service should be inited", voucherService);
		assertTrue("5.0b1 is a correct for render version", voucherService.isAbleToPurchaseProductsOnline());
	}
	
	@Test
	public void testUnAvailableForProductsRenderVersion() throws Exception {
		System.setProperty(TestModule.TEST_COLLEGE_ANGEL_VERSION_PROPERTY, UNAVAILABLE_FOR_PRODUCT_RENDER_VERSION);
		setup();
		IVoucherService voucherService = getService("IVoucherServiceOverride", IVoucherService.class);
		assertNotNull("Voucher service should be inited", voucherService);
		assertFalse("4.0-SNAPSHOT is a less then required for render version", voucherService.isAbleToPurchaseProductsOnline());
	}
	
	@Test
	public void testUnAvailableProductsPageLoad() throws Exception {
		System.setProperty(TestModule.TEST_COLLEGE_ANGEL_VERSION_PROPERTY, UNAVAILABLE_FOR_PRODUCT_RENDER_VERSION);
		setup();
		Document response = getPageTester().renderPage(PRODUCTS_PAGE);
		assertNotNull("Response can't be null", response);
		Element contextElement = response.getElementById(CONTENT_ELEMENT_ID);
		assertNotNull("Content element should exist", contextElement);
		assertEquals("Incorrect page not found content body", PAGE_NOT_FOUND_CONTENT_BODY, contextElement.getChildMarkup());
	}
	
	@Test
	public void testAvailableProductsPageLoad() throws Exception {
		System.setProperty(TestModule.TEST_COLLEGE_ANGEL_VERSION_PROPERTY, AVAILABLE_FOR_PRODUCT_RENDER_VERSION);
		setup();
		Document response = getPageTester().renderPage(PRODUCTS_PAGE);
		assertNotNull("Response can't be null", response);
		Element contextElement = response.getElementById(CONTENT_ELEMENT_ID);
		assertNotNull("Content element should exist", contextElement);
		assertEquals("Correct no products available content body", NO_PRODUCTS_AVAILABLE_CONTENT_BODY, contextElement.getChildMarkup());
	}
	
	@After
	public void tearDown() {
		
	}

}
