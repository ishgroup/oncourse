package ish.oncourse.services.voucher;

import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Voucher;
import ish.oncourse.model.VoucherProduct;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.WebSiteService;
import ish.oncourse.test.ServiceTest;
import org.apache.cayenne.query.SelectById;
import org.apache.tapestry5.services.Request;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VoucherServiceTest extends ServiceTest {
	
	private ICayenneService cayenneService;

	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.services", "service", ServiceModule.class);
		InputStream st = VoucherServiceTest.class.getClassLoader().getResourceAsStream("ish/oncourse/services/voucher/voucherDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DataSource dataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(dataSource.getConnection(), null), dataSet);
		
		this.cayenneService = getService(ICayenneService.class);
	}
	
	@Test
	public void testGetAvailableVoucherProducts() {
		Request request = mock(Request.class);
		IWebSiteService webSiteService = new WebSiteService(request, cayenneService);

		when(request.getServerName()).thenReturn("scc.staging1.oncourse.net.au");
		final VoucherService service = new VoucherService(webSiteService, cayenneService);
		assertEquals("Checking site key.", "scc", webSiteService.getCurrentWebSite().getSiteKey());
		assertEquals("SCC should contain six defined Voucher Products", 6, service.getProductCount().intValue());
		
		Request request2 = mock(Request.class);
		when(request2.getServerName()).thenReturn("tae.test.oncourse.net.au");
		final VoucherService service2 = new VoucherService(webSiteService, cayenneService);
		assertEquals("Checking site key.", "tae",webSiteService.getCurrentWebSite().getSiteKey());
		assertEquals("Tae have no defined Voucher Products", 1, service2.getProductCount().intValue());
	}
	
	@Test
	public void testGetVoucherByCode() {
		Request request = mock(Request.class);
		IWebSiteService webSiteService = new WebSiteService(request, cayenneService);

		when(request.getServerName()).thenReturn("scc.staging1.oncourse.net.au");
		final VoucherService service = new VoucherService(webSiteService, cayenneService);
		assertEquals("Checking site key.", "scc", webSiteService.getCurrentWebSite().getSiteKey());
		Voucher voucher = service.getVoucherByCode("qwerty");
		assertNotNull("Voucher for code qwerty should be founded", voucher);
		assertEquals("Voucher value should be 100$", new Money("100.00"), voucher.getRedemptionValue());
		assertEquals("Voucher product name should be 'my test membership product'", "my test membership product", voucher.getProduct().getName());
		assertEquals("Voucher product price should be 10$", new Money("10.00"), voucher.getProduct().getPriceExTax());
		assertNull("Voucher product we found not limited to some student", voucher.getContact());
	}
	
	@Test
	public void testGetAvailableVoucherProductsForUser() {
		Request request = mock(Request.class);
		when(request.getServerName()).thenReturn("scc.staging1.oncourse.net.au");

		IWebSiteService webSiteService = new WebSiteService(request, cayenneService);

		final VoucherService service = new VoucherService(webSiteService, cayenneService);
		assertEquals("Checking site key.", "scc", webSiteService.getCurrentWebSite().getSiteKey());
		Voucher voucher = service.getVoucherByCode("qwerty_2");
		assertNotNull("Voucher for code qwerty_2 should be founded", voucher);
		assertEquals("Voucher value should be 101$", new Money("101.00"), voucher.getRedemptionValue());
		assertEquals("Voucher product name should be 'my test membership product part 2'", "my test membership product part 2", 
			voucher.getProduct().getName());
		assertEquals("Voucher product price should be 11$", new Money("11.00"), voucher.getProduct().getPriceExTax());
		Contact contact = SelectById.query(Contact.class,1L).selectOne(cayenneService.sharedContext());
		assertNotNull("Contact with id=1 should exist", contact);
		//now link this Voucher with this contact
		voucher.setContact(contact);
		voucher.getObjectContext().commitChanges();
		voucher = service.getVoucherByCode("qwerty_2");
		assertNotNull("Voucher product we found should be limited to a student", voucher.getContact());
		assertEquals("Contact 1 should be linked with voucher", contact, voucher.getContact());
		assertEquals("Contact 1 should be linked with voucher", contact.getProducts().get(0), voucher);
		assertEquals("This voucher should be only 1 for this contact", 1, contact.getProducts().size());
	}

	@Test
	public void tesCreateVoucher() {
		Request request = mock(Request.class);
		IWebSiteService webSiteService = new WebSiteService(request, cayenneService);
		when(request.getServerName()).thenReturn("scc.staging1.oncourse.net.au");

		VoucherProduct product = SelectById.query(VoucherProduct.class, 1).selectOne(cayenneService.sharedContext());
		VoucherService service = new VoucherService(webSiteService, cayenneService);
		Voucher voucher = service.createVoucher(product);
		assertNull("Willow voucher always can be redeemed by anyone", voucher.getContact());
	}
}
