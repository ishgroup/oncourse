package ish.oncourse.ui.pages;


import static org.junit.Assert.*;

import java.io.InputStream;

import javax.sql.DataSource;

import org.apache.tapestry5.dom.Document;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ish.oncourse.services.voucher.IVoucherService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.ui.services.TestModule;

public class ProductsTest extends ServiceTest {
	
	public static final String APP_PACKAGE = "ish.oncourse.website";
	public static final String CONTEXT_PATH = "src/main/resources/ish/oncourse/ui/pages";
	
	//@Before
	public void setup() throws Exception {
		initTest(APP_PACKAGE, "", CONTEXT_PATH, TestModule.class);
		InputStream st = ProductsTest.class.getClassLoader().getResourceAsStream("ish/oncourse/ui/pages/products/voucherDataSet.xml");
		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DataSource refDataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);
	}
	
	@Test
	public void testAvailableForProductsRenderVersion() throws Exception {
		System.setProperty("oncourse.test.server.angelversion", "4.0-development");
		setup();
		IVoucherService voucherService = getService(IVoucherService.class);
		assertNotNull("Voucher service should be inited", voucherService);
		assertTrue("4.0-development is a correct for render version", voucherService.isAbleToPurchaseProductsOnline());
	}
	
	@Test
	public void testUnAvailableForProductsRenderVersion() throws Exception {
		System.setProperty("oncourse.test.server.angelversion", "3.0-SNAPSHOT");
		setup();
		IVoucherService voucherService = getService(IVoucherService.class);
		assertNotNull("Voucher service should be inited", voucherService);
		assertFalse("3.0-SNAPSHOT is a less then required for render version", voucherService.isAbleToPurchaseProductsOnline());
	}
	
	//@Test
	public void testProductsPageLoad() throws Exception {
		System.setProperty("oncourse.test.server.angelversion", "4.0-development");
		setup();
		Document doc = getPageTester().renderPage("Products");
		//assertNotNull(doc.getElementById("unsubscribeForm"));
	}
	
	@After
	public void tearDown() {
		
	}

}
