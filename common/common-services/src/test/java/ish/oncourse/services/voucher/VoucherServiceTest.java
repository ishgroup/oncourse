package ish.oncourse.services.voucher;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentSource;
import ish.common.types.ProductStatus;
import ish.math.Money;
import ish.oncourse.model.*;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.WebSiteService;
import ish.oncourse.test.ServiceTest;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.services.Request;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
		when(request.getServerName()).thenReturn("scc.staging1.oncourse.net.au");
		final VoucherService service = new VoucherService(new WebSiteService(request, cayenneService), cayenneService);
		assertEquals("Checking site key.", "scc", service.takeWebSiteService().getCurrentWebSite().getSiteKey());
		assertEquals("SCC should contain six defined Voucher Products", 6, service.getAvailableProducts().size());
		
		Request request2 = mock(Request.class);
		when(request2.getServerName()).thenReturn("tae.test.oncourse.net.au");
		final VoucherService service2 = new VoucherService(new WebSiteService(request2, cayenneService), cayenneService);
		assertEquals("Checking site key.", "tae", service2.takeWebSiteService().getCurrentWebSite().getSiteKey());
		assertEquals("Tae have no defined Voucher Products", 1, service2.getAvailableProducts().size());
	}
	
	@Test
	public void testGetVoucherByCode() {
		Request request = mock(Request.class);
		when(request.getServerName()).thenReturn("scc.staging1.oncourse.net.au");
		final VoucherService service = new VoucherService(new WebSiteService(request, cayenneService), cayenneService);
		assertEquals("Checking site key.", "scc", service.takeWebSiteService().getCurrentWebSite().getSiteKey());
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
		final VoucherService service = new VoucherService(new WebSiteService(request, cayenneService), cayenneService);
		assertEquals("Checking site key.", "scc", service.takeWebSiteService().getCurrentWebSite().getSiteKey());
		Voucher voucher = service.getVoucherByCode("qwerty_2");
		assertNotNull("Voucher for code qwerty_2 should be founded", voucher);
		assertEquals("Voucher value should be 101$", new Money("101.00"), voucher.getRedemptionValue());
		assertEquals("Voucher product name should be 'my test membership product part 2'", "my test membership product part 2", 
			voucher.getProduct().getName());
		assertEquals("Voucher product price should be 11$", new Money("11.00"), voucher.getProduct().getPriceExTax());
		Contact contact = (Contact) voucher.getObjectContext().performQuery(new SelectQuery(Contact.class, 
			ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 1L))).get(0);
		assertNotNull("Contact with id=1 should exist", contact);
		//now link this Voucher with this contact
		voucher.setContact(contact);
		voucher.getObjectContext().commitChanges();
		voucher = service.getVoucherByCode("qwerty_2");
		assertNotNull("Voucher product we found should be limited to a student", voucher.getContact());
		assertEquals("Contact 1 should be linked with voucher", contact, voucher.getContact());
		assertEquals("Contact 1 should be linked with voucher", contact.getVouchers().get(0), voucher);
		assertEquals("This voucher should be only 1 for this contact", 1, contact.getVouchers().size());
		List<Voucher> availableVouchers = service.getAvailableVouchersForUser(contact);
		assertFalse("Available vouchers list should not be empty", availableVouchers.isEmpty());
		assertEquals("Available vouchers list should contain 4 vouchers", 4, availableVouchers.size());
		assertEquals("Available vouchers list should contain selected voucher", voucher, availableVouchers.get(1));
	}
	
	@Test
	public void testVoucherRedemptionHelperForSingleMoneyVoucher() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		//load contact
		Contact contact = (Contact) context.performQuery(new SelectQuery(Contact.class,
			ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 1L))).get(0);
		assertNotNull("Contact with id=1 should exist", contact);
		assertNotNull("Contact with id=1 should have linked student", contact.getStudent());
		//load voucher
		Voucher voucher = (Voucher) context.performQuery(new SelectQuery(Voucher.class,ExpressionFactory.matchDbExp(Voucher.ID_PK_COLUMN, 2L))).get(0);
		assertNotNull("Voucher should be created", voucher);
		assertEquals("Payment amount should be the voucher price because the voucher product have no price", 
			voucher.getRedemptionValue(), new Money("101.00"));
		assertNull("Voucher should not be linked to any contact because the payer and owner equal", voucher.getContact());
		//load courseclass for enrolment
		CourseClass courseClass = (CourseClass) context.performQuery(new SelectQuery(CourseClass.class, 
			ExpressionFactory.matchDbExp(CourseClass.ID_PK_COLUMN, 1L))).get(0);
		//prepare invoice for helper
		Invoice invoice = createInvoice(context);
		
		InvoiceLine invoiceLine = context.newObject(InvoiceLine.class);
		invoiceLine.setCollege(invoice.getCollege());
		invoiceLine.setInvoice(invoice);
		invoiceLine.setDescription("test invoiceline 1");
		invoiceLine.setTitle(invoiceLine.getDescription());
		invoiceLine.setDiscountEachExTax(Money.ZERO);
		invoiceLine.setPriceEachExTax(new Money("90.00"));
		invoiceLine.setTaxEach(new Money("10.00"));
		invoiceLine.setQuantity(BigDecimal.ONE);
		invoice.setTotalGst(invoiceLine.getDiscountedPriceTotalIncTax());
		invoice.setTotalExGst(invoiceLine.getPriceTotalExTax());
		
		Enrolment enrolment = context.newObject(Enrolment.class);
		enrolment.setCollege(invoiceLine.getCollege());
		enrolment.setCourseClass(courseClass);
		enrolment.setInvoiceLine(invoiceLine);
		enrolment.setSource(invoice.getSource());
		enrolment.setStatus(EnrolmentStatus.NEW);
		enrolment.setStudent(contact.getStudent());
		enrolment.setReasonForStudy(1);
		
		//context.commitChanges();
		//start helper
		VoucherRedemptionHelper helper = new VoucherRedemptionHelper(invoice, Arrays.asList(voucher));
		helper.calculateVouchersRedeemPayment();
		assertNotNull("One payment should be created", helper.getPayments());
		assertEquals("One payment should be created", 1, helper.getPayments().size());
		assertEquals("After calculation voucher should be still active", ProductStatus.ACTIVE, voucher.getStatus());
		assertEquals("After calculation voucher should contain 1 $ value", Money.ONE, voucher.getRedemptionValue());
		invoice.updateAmountOwing();
		assertEquals("Amount owing for invoice should be 0", Money.ZERO, 
			invoice.getAmountOwing().subtract(helper.getPayments().get(0).getAmount()));
		//call re-calculate to check that the result will be the same
		helper.calculateVouchersRedeemPayment();
		assertNotNull("One payment should be created", helper.getPayments());
		assertEquals("One payment should be created", 1, helper.getPayments().size());
		assertEquals("After re-calculation voucher should be still active", ProductStatus.ACTIVE, voucher.getStatus());
		assertEquals("After re-calculation voucher should contain 1 $ value", Money.ONE, voucher.getRedemptionValue());
		invoice.updateAmountOwing();
		assertEquals("Amount owing for invoice should be 0", Money.ZERO, 
			invoice.getAmountOwing().subtract(helper.getPayments().get(0).getAmount()));
	}
	
	@Test
	public void testVoucherRedemptionHelperForSingleMoneyVoucherWithoutEnougthMoney() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		//load contact
		Contact contact = (Contact) context.performQuery(new SelectQuery(Contact.class, 
			ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 1L))).get(0);
		assertNotNull("Contact with id=1 should exist", contact);
		assertNotNull("Contact with id=1 should have linked student", contact.getStudent());
		//load voucher
		Voucher voucher = (Voucher) context.performQuery(new SelectQuery(Voucher.class,ExpressionFactory.matchDbExp(Voucher.ID_PK_COLUMN, 2L))).get(0);
		assertNotNull("Voucher should be created", voucher);
		assertEquals("Payment amount should be the voucher price because the voucher product have no price", 
			voucher.getRedemptionValue(), new Money("101.00"));
		assertNull("Voucher should not be linked to any contact because the payer and owner equal", voucher.getContact());
		//load courseclass for enrolment
		CourseClass courseClass = (CourseClass) context.performQuery(new SelectQuery(CourseClass.class, 
			ExpressionFactory.matchDbExp(CourseClass.ID_PK_COLUMN, 1L))).get(0);
		//prepare invoice for helper
		Invoice invoice = createInvoice(context);
		
		InvoiceLine invoiceLine = context.newObject(InvoiceLine.class);
		invoiceLine.setCollege(invoice.getCollege());
		invoiceLine.setInvoice(invoice);
		invoiceLine.setDescription("test invoiceline 1");
		invoiceLine.setTitle(invoiceLine.getDescription());
		invoiceLine.setDiscountEachExTax(Money.ZERO);
		invoiceLine.setPriceEachExTax(new Money("180.00"));
		invoiceLine.setTaxEach(new Money("20.00"));
		invoiceLine.setQuantity(BigDecimal.ONE);
		invoice.setTotalGst(invoiceLine.getDiscountedPriceTotalIncTax());
		invoice.setTotalExGst(invoiceLine.getPriceTotalExTax());
		
		Enrolment enrolment = context.newObject(Enrolment.class);
		enrolment.setCollege(invoiceLine.getCollege());
		enrolment.setCourseClass(courseClass);
		enrolment.setInvoiceLine(invoiceLine);
		enrolment.setSource(invoice.getSource());
		enrolment.setStatus(EnrolmentStatus.NEW);
		enrolment.setStudent(contact.getStudent());
		enrolment.setReasonForStudy(1);
		
		//context.commitChanges();
		//start helper
		VoucherRedemptionHelper helper = new VoucherRedemptionHelper(invoice, Arrays.asList(voucher));
		helper.calculateVouchersRedeemPayment();
		assertNotNull("One payment should be created", helper.getPayments());
		assertEquals("One payment should be created", 1, helper.getPayments().size());
		assertEquals("After calculation voucher should be redeemed", ProductStatus.REDEEMED, voucher.getStatus());
		assertEquals("After calculation voucher should have no money left", Money.ZERO, voucher.getRedemptionValue());

		invoice.updateAmountOwing();
		assertEquals("Amount owing for invoice should be correct", new Money("99.00"), 
			invoice.getAmountOwing().subtract(helper.getPayments().get(0).getAmount()));
		//call re-calculate to check that the result will be the same
		helper.calculateVouchersRedeemPayment();
		assertNotNull("One payment should be created", helper.getPayments());
		assertEquals("One payment should be created", 1, helper.getPayments().size());
		assertEquals("After re-calculation voucher should be redeemed", ProductStatus.REDEEMED, voucher.getStatus());
		assertEquals("After re-calculation voucher should have no money left", Money.ZERO, voucher.getRedemptionValue());
		invoice.updateAmountOwing();
		assertEquals("Amount owing for invoice should be correct", new Money("99.00"), 
			invoice.getAmountOwing().subtract(helper.getPayments().get(0).getAmount()));
	}
	
	@Test
	public void testVoucherRedemptionHelperForManyMoneyVouchers() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		//load contacts
		Contact contact = (Contact) context.performQuery(new SelectQuery(Contact.class, 
			ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 1L))).get(0);
		assertNotNull("Contact with id=1 should exist", contact);
		assertNotNull("Contact with id=1 should have linked student", contact.getStudent());
		Contact contact2 = (Contact) context.performQuery(new SelectQuery(Contact.class, 
			ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 3L))).get(0);
		assertNotNull("Contact with id=3 should exist", contact2);
		assertNotNull("Contact with id=3 should have linked student", contact2.getStudent());
		//load voucher
		Voucher voucher = (Voucher) context.performQuery(new SelectQuery(Voucher.class,ExpressionFactory.matchDbExp(Voucher.ID_PK_COLUMN, 2L))).get(0);
		assertNotNull("Voucher should be created", voucher);
		assertEquals("Payment amount should be the voucher price because the voucher product have no price", 
			voucher.getRedemptionValue(), new Money("101.00"));
		assertNull("Voucher should not be linked to any contact because the payer and owner equal", voucher.getContact());
		//load voucher2
		Voucher voucher2 = (Voucher) context.performQuery(new SelectQuery(Voucher.class,ExpressionFactory.matchDbExp(Voucher.ID_PK_COLUMN, 1L))).get(0);
		assertNotNull("Voucher should be created", voucher2);
		assertEquals("Payment amount should be the voucher price because the voucher product have no price", 
				voucher2.getRedemptionValue(), new Money("100.00"));
		assertNull("Voucher should not be linked to any contact because the payer and owner equal", voucher2.getContact());
		//load courseclass for enrolment
		CourseClass courseClass = (CourseClass) context.performQuery(new SelectQuery(CourseClass.class, 
			ExpressionFactory.matchDbExp(CourseClass.ID_PK_COLUMN, 1L))).get(0);
		//prepare invoice for helper
		Invoice invoice = createInvoice(context);

		InvoiceLine invoiceLine = context.newObject(InvoiceLine.class);
		invoiceLine.setCollege(invoice.getCollege());
		invoiceLine.setInvoice(invoice);
		invoiceLine.setDescription("test invoiceline 1");
		invoiceLine.setTitle(invoiceLine.getDescription());
		invoiceLine.setDiscountEachExTax(Money.ZERO);
		invoiceLine.setPriceEachExTax(new Money("90.00"));
		invoiceLine.setTaxEach(new Money("10.00"));
		invoiceLine.setQuantity(BigDecimal.ONE);
		
		Enrolment enrolment = context.newObject(Enrolment.class);
		enrolment.setCollege(invoiceLine.getCollege());
		enrolment.setCourseClass(courseClass);
		enrolment.setInvoiceLine(invoiceLine);
		enrolment.setSource(invoice.getSource());
		enrolment.setStatus(EnrolmentStatus.NEW);
		enrolment.setStudent(contact.getStudent());
		enrolment.setReasonForStudy(1);
		
		InvoiceLine invoiceLine2 = context.newObject(InvoiceLine.class);
		invoiceLine2.setCollege(invoice.getCollege());
		invoiceLine2.setInvoice(invoice);
		invoiceLine2.setDescription("test invoiceline 1");
		invoiceLine2.setTitle(invoiceLine.getDescription());
		invoiceLine2.setDiscountEachExTax(Money.ZERO);
		invoiceLine2.setPriceEachExTax(new Money("90.00"));
		invoiceLine2.setTaxEach(new Money("10.00"));
		invoiceLine2.setQuantity(BigDecimal.ONE);
		invoice.setTotalGst(invoiceLine2.getDiscountedPriceTotalIncTax());
		invoice.setTotalExGst(invoiceLine2.getPriceTotalExTax());
		
		Enrolment enrolment2 = context.newObject(Enrolment.class);
		enrolment2.setCollege(invoiceLine2.getCollege());
		enrolment2.setCourseClass(courseClass);
		enrolment2.setInvoiceLine(invoiceLine2);
		enrolment2.setSource(invoice.getSource());
		enrolment2.setStatus(EnrolmentStatus.NEW);
		enrolment2.setStudent(contact2.getStudent());
		enrolment2.setReasonForStudy(1);
		
		invoice.setTotalGst(invoiceLine.getDiscountedPriceTotalIncTax().add(invoiceLine2.getDiscountedPriceTotalIncTax()));
		invoice.setTotalExGst(invoiceLine.getPriceTotalExTax().add(invoiceLine2.getPriceTotalExTax()));
		
		//start helper
		VoucherRedemptionHelper helper = new VoucherRedemptionHelper(invoice, Arrays.asList(voucher,voucher2));
		helper.calculateVouchersRedeemPayment();
		assertNotNull("Two payments should be created", helper.getPayments());
		assertEquals("Two payments should be created", 2, helper.getPayments().size());
		List<Voucher> vouchers = helper.getVouchers();
		assertEquals("After calculation 2 vouchers should be used", 2, vouchers.size());
		assertEquals("After calculation 2 vouchers should be used and first should be redeemed", ProductStatus.REDEEMED, 
			vouchers.get(0).getStatus());
		assertEquals("After calculation 2 vouchers should be used and second should be active", ProductStatus.ACTIVE, 
				vouchers.get(1).getStatus());
		assertEquals("After calculation 2 vouchers should be used and second should have 1 $ value", Money.ONE, 
			vouchers.get(1).getRedemptionValue());
		assertEquals("Invoice and redeemed shoudl be equal", invoice.getTotalGst(), new Money("200.00"));
		invoice.updateAmountOwing();
		Money paymentAmounts = helper.getPayments().get(0).getAmount().add(helper.getPayments().get(1).getAmount());
		assertEquals("Amount owing for invoice should be 0", Money.ZERO, 
			invoice.getAmountOwing().subtract(paymentAmounts));
		
		//call re-calculate to check that the result will be the same
		helper.calculateVouchersRedeemPayment();
		assertNotNull("Two payments should be created", helper.getPayments());
		assertEquals("Two payments should be created", 2, helper.getPayments().size());
		vouchers = helper.getVouchers();
		assertEquals("After calculation 2 vouchers should be used", 2, vouchers.size());
		assertEquals("After calculation 2 vouchers should be used and first should be redeemed", ProductStatus.REDEEMED, 
			vouchers.get(0).getStatus());
		assertEquals("After calculation 2 vouchers should be used and second should be active", ProductStatus.ACTIVE, 
				vouchers.get(1).getStatus());
		assertEquals("After calculation 2 vouchers should be used and second should have 1 $ value", Money.ONE, 
			vouchers.get(1).getRedemptionValue());
		assertEquals("Invoice and redeemed shoudl be equal", invoice.getTotalGst(), new Money("200.00"));
		invoice.updateAmountOwing();
		paymentAmounts = helper.getPayments().get(0).getAmount().add(helper.getPayments().get(1).getAmount());
		assertEquals("Amount owing for invoice should be 0", Money.ZERO, 
			invoice.getAmountOwing().subtract(paymentAmounts));
	}
	
	@Test
	public void testVoucherRedemptionHelperForManyMoneyVouchersWithoutEnougthMoney() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		//load contacts
		Contact contact = (Contact) context.performQuery(new SelectQuery(Contact.class, 
			ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 1L))).get(0);
		WebSite webSite = Cayenne.objectForPK(context,WebSite.class, 1);

		assertNotNull("Contact with id=1 should exist", contact);
		assertNotNull("Contact with id=1 should have linked student", contact.getStudent());
		Contact contact2 = (Contact) context.performQuery(new SelectQuery(Contact.class, 
			ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 3L))).get(0);
		assertNotNull("Contact with id=3 should exist", contact2);
		assertNotNull("Contact with id=3 should have linked student", contact2.getStudent());
		//load voucher
		Voucher voucher = (Voucher) context.performQuery(new SelectQuery(Voucher.class,ExpressionFactory.matchDbExp(Voucher.ID_PK_COLUMN, 2L))).get(0);
		assertNotNull("Voucher should be created", voucher);
		assertEquals("Payment amount should be the voucher price because the voucher product have no price", 
			voucher.getRedemptionValue(), new Money("101.00"));
		assertNull("Voucher should not be linked to any contact because the payer and owner equal", voucher.getContact());
		//load voucher2
		Voucher voucher2 = (Voucher) context.performQuery(new SelectQuery(Voucher.class,ExpressionFactory.matchDbExp(Voucher.ID_PK_COLUMN, 1L))).get(0);
		assertNotNull("Voucher should be created", voucher2);
		assertEquals("Payment amount should be the voucher price because the voucher product have no price", 
				voucher2.getRedemptionValue(), new Money("100.00"));
		assertNull("Voucher should not be linked to any contact because the payer and owner equal", voucher2.getContact());
		//load courseclass for enrolment
		CourseClass courseClass = (CourseClass) context.performQuery(new SelectQuery(CourseClass.class, 
			ExpressionFactory.matchDbExp(CourseClass.ID_PK_COLUMN, 1L))).get(0);
		//prepare invoice for helper
		Invoice invoice = createInvoice(context);

		InvoiceLine invoiceLine = context.newObject(InvoiceLine.class);
		invoiceLine.setCollege(invoice.getCollege());
		invoiceLine.setInvoice(invoice);
		invoiceLine.setDescription("test invoiceline 1");
		invoiceLine.setTitle(invoiceLine.getDescription());
		invoiceLine.setDiscountEachExTax(Money.ZERO);
		invoiceLine.setPriceEachExTax(new Money("180.00"));
		invoiceLine.setTaxEach(new Money("20.00"));
		invoiceLine.setQuantity(BigDecimal.ONE);
		
		Enrolment enrolment = context.newObject(Enrolment.class);
		enrolment.setCollege(invoiceLine.getCollege());
		enrolment.setCourseClass(courseClass);
		enrolment.setInvoiceLine(invoiceLine);
		enrolment.setSource(invoice.getSource());
		enrolment.setStatus(EnrolmentStatus.NEW);
		enrolment.setStudent(contact.getStudent());
		enrolment.setReasonForStudy(1);
		
		InvoiceLine invoiceLine2 = context.newObject(InvoiceLine.class);
		invoiceLine2.setCollege(invoice.getCollege());
		invoiceLine2.setInvoice(invoice);
		invoiceLine2.setDescription("test invoiceline 1");
		invoiceLine2.setTitle(invoiceLine.getDescription());
		invoiceLine2.setDiscountEachExTax(Money.ZERO);
		invoiceLine2.setPriceEachExTax(new Money("45.00"));
		invoiceLine2.setTaxEach(new Money("5.00"));
		invoiceLine2.setQuantity(BigDecimal.ONE);
		invoice.setTotalGst(invoiceLine2.getDiscountedPriceTotalIncTax());
		invoice.setTotalExGst(invoiceLine2.getPriceTotalExTax());
		
		Enrolment enrolment2 = context.newObject(Enrolment.class);
		enrolment2.setCollege(invoiceLine2.getCollege());
		enrolment2.setCourseClass(courseClass);
		enrolment2.setInvoiceLine(invoiceLine2);
		enrolment2.setSource(invoice.getSource());
		enrolment2.setStatus(EnrolmentStatus.NEW);
		enrolment2.setStudent(contact2.getStudent());
		enrolment2.setReasonForStudy(1);
		
		invoice.setTotalGst(invoiceLine.getDiscountedPriceTotalIncTax().add(invoiceLine2.getDiscountedPriceTotalIncTax()));
		invoice.setTotalExGst(invoiceLine.getPriceTotalExTax().add(invoiceLine2.getPriceTotalExTax()));
		
		//start helper
		VoucherRedemptionHelper helper = new VoucherRedemptionHelper(invoice, Arrays.asList(voucher,voucher2));
		helper.calculateVouchersRedeemPayment();
		assertNotNull("Two payments should be created", helper.getPayments());
		assertEquals("Two payments should be created", 2, helper.getPayments().size());
		List<Voucher> vouchers = helper.getVouchers();
		assertEquals("After calculation 2 vouchers should be used", 2, vouchers.size());
		assertEquals("After calculation 2 vouchers should be used and first should be redeemed", ProductStatus.REDEEMED, 
			vouchers.get(0).getStatus());
		assertEquals("After calculation 2 vouchers should be used and first should have 0 $ value", Money.ZERO, 
			vouchers.get(0).getRedemptionValue());
		assertEquals("After calculation 2 vouchers should be used and second should be active", ProductStatus.REDEEMED, 
				vouchers.get(1).getStatus());
		assertEquals("After calculation 2 vouchers should be used and second should have 0 $ value", Money.ZERO, 
			vouchers.get(1).getRedemptionValue());
		
		invoice.updateAmountOwing();
		Money paymentAmounts = helper.getPayments().get(0).getAmount().add(helper.getPayments().get(1).getAmount());
		assertEquals("Amount owing for invoice should be 49 $", new Money("49.00"),
			invoice.getAmountOwing().subtract(paymentAmounts));
		//call re-calculate to check that the result will be the same
		helper.calculateVouchersRedeemPayment();
		assertNotNull("Two payments should be created", helper.getPayments());
		assertEquals("Two payments should be created", 2, helper.getPayments().size());
		vouchers = helper.getVouchers();
		assertEquals("After calculation 2 vouchers should be used", 2, vouchers.size());
		assertEquals("After calculation 2 vouchers should be used and first should be redeemed", ProductStatus.REDEEMED, 
			vouchers.get(0).getStatus());
		assertEquals("After calculation 2 vouchers should be used and first should have 0 $ value", Money.ZERO, 
			vouchers.get(0).getRedemptionValue());
		assertEquals("After calculation 2 vouchers should be used and second should be active", ProductStatus.REDEEMED, 
				vouchers.get(1).getStatus());
		assertEquals("After calculation 2 vouchers should be used and second should have 0 $ value", Money.ZERO, 
			vouchers.get(1).getRedemptionValue());
		
		invoice.updateAmountOwing();
		paymentAmounts = helper.getPayments().get(0).getAmount().add(helper.getPayments().get(1).getAmount());
		assertEquals("Amount owing for invoice should be 49 $", new Money("49.00"),
			invoice.getAmountOwing().subtract(paymentAmounts));
	}
	
	@Test
	public void testVoucherRedemptionHelperForSingleCourseVoucher() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		//load contact
		Contact contact = (Contact) context.performQuery(new SelectQuery(Contact.class, 
			ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 1L))).get(0);
		assertNotNull("Contact with id=1 should exist", contact);
		assertNotNull("Contact with id=1 should have linked student", contact.getStudent());
		//load voucher
		Voucher voucher = (Voucher) context.performQuery(new SelectQuery(Voucher.class,ExpressionFactory.matchDbExp(Voucher.ID_PK_COLUMN, 3L))).get(0);
		assertNotNull("Voucher should be created", voucher);
		assertEquals("Payment amount should be the voucher price because the voucher product have no price", 
			voucher.getRedemptionValue(), new Money("10.00"));
		assertNull("Voucher should not be linked to any contact because the payer and owner equal", voucher.getContact());
		//load courseclass for enrolment
		CourseClass courseClass = (CourseClass) context.performQuery(new SelectQuery(CourseClass.class, 
			ExpressionFactory.matchDbExp(CourseClass.ID_PK_COLUMN, 1L))).get(0);
		VoucherProductCourse voucherProductCourse = context.newObject(VoucherProductCourse.class);
		voucherProductCourse.setCollege(contact.getCollege());
		voucherProductCourse.setCourse(courseClass.getCourse());
		voucherProductCourse.setVoucherProduct(voucher.getVoucherProduct());
		//commit the changes now to affect this changes
		context.commitChanges();
		voucher = (Voucher) context.performQuery(new SelectQuery(Voucher.class,ExpressionFactory.matchDbExp(Voucher.ID_PK_COLUMN, 3L))).get(0);
		assertNotNull("Voucher should be created", voucher);
		assertNotNull("This voucher should be the course voucher", voucher.getVoucherProduct().getRedemptionCourses());
		assertEquals("This voucher should be the course voucher and have 1 linked course", 1, 
			voucher.getVoucherProduct().getRedemptionCourses().size());
		assertNull("Voucher should not be linked to any contact because the payer and owner equal", voucher.getContact());
		
		//prepare invoice for helper
		Invoice invoice = createInvoice(context);

		InvoiceLine invoiceLine = context.newObject(InvoiceLine.class);
		invoiceLine.setCollege(invoice.getCollege());
		invoiceLine.setInvoice(invoice);
		invoiceLine.setDescription("test invoiceline 1");
		invoiceLine.setTitle(invoiceLine.getDescription());
		invoiceLine.setDiscountEachExTax(Money.ZERO);
		invoiceLine.setPriceEachExTax(new Money("90.00"));
		invoiceLine.setTaxEach(new Money("10.00"));
		invoiceLine.setQuantity(BigDecimal.ONE);
		invoice.setTotalGst(invoiceLine.getDiscountedPriceTotalIncTax());
		invoice.setTotalExGst(invoiceLine.getPriceTotalExTax());
		
		Enrolment enrolment = context.newObject(Enrolment.class);
		enrolment.setCollege(invoiceLine.getCollege());
		enrolment.setCourseClass(courseClass);
		enrolment.setInvoiceLine(invoiceLine);
		enrolment.setSource(invoice.getSource());
		enrolment.setStatus(EnrolmentStatus.NEW);
		enrolment.setStudent(contact.getStudent());
		enrolment.setReasonForStudy(1);
		
		//context.commitChanges();
		//start helper
		VoucherRedemptionHelper helper = new VoucherRedemptionHelper(invoice, Arrays.asList(voucher));
		helper.calculateVouchersRedeemPayment();
		assertNotNull("One payment should be created", helper.getPayments());
		assertEquals("One payment should be created", 1, helper.getPayments().size());
		List<Voucher> vouchers = helper.getVouchers();
		assertEquals("After calculation 1 voucher should be used", 1, vouchers.size());
		assertEquals("After calculation 1 voucher should be used and should be active", ProductStatus.ACTIVE, 
			voucher.getStatus());
		assertEquals("After calculation 1 voucher should be used and should have 1 extra course place", 1, 
			voucher.getClassesRemaining().intValue());
		invoice.updateAmountOwing();
		assertEquals("Amount owing should be 0 for course voucher", Money.ZERO, 
			invoice.getAmountOwing().subtract(helper.getPayments().get(0).getAmount()));
		//call re-calculate to check that the result will be the same
		helper.calculateVouchersRedeemPayment();
		assertNotNull("One payment should be created", helper.getPayments());
		assertEquals("One payment should be created", 1, helper.getPayments().size());
		vouchers = helper.getVouchers();
		assertEquals("After calculation 1 voucher should be used", 1, vouchers.size());
		assertEquals("After calculation 1 voucher should be used and should be active", ProductStatus.ACTIVE, 
			voucher.getStatus());
		assertEquals("After calculation 1 voucher should be used and should have 1 extra course place", 1, 
			voucher.getClassesRemaining().intValue());
		invoice.updateAmountOwing();
		assertEquals("Amount owing should be 0 for course voucher", Money.ZERO, 
			invoice.getAmountOwing().subtract(helper.getPayments().get(0).getAmount()));
	}

	private Invoice createInvoice(ObjectContext context) {
		Contact contact = Cayenne.objectForPK(context,Contact.class, 1);
		WebSite webSite = Cayenne.objectForPK(context,WebSite.class, 1);

		Invoice invoice = context.newObject(Invoice.class);
		context.registerNewObject(invoice);
		invoice.setSource(PaymentSource.SOURCE_WEB);
		invoice.setCollege(contact.getCollege());
		invoice.setContact(contact);
		invoice.setAmountOwing(Money.ZERO);
		invoice.setDateDue(new Date());
		invoice.setInvoiceDate(invoice.getDateDue());
		invoice.setWebSite(webSite);
		return invoice;
	}

	@Test
	public void testVoucherRedemptionHelperForSingleCourseVoucherWithManyEnrolments() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		//load contact
		Contact contact = (Contact) context.performQuery(new SelectQuery(Contact.class, 
			ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 1L))).get(0);
		assertNotNull("Contact with id=1 should exist", contact);
		assertNotNull("Contact with id=1 should have linked student", contact.getStudent());
		Contact contact2 = (Contact) context.performQuery(new SelectQuery(Contact.class, 
			ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 3L))).get(0);
		assertNotNull("Contact with id=3 should exist", contact2);
		assertNotNull("Contact with id=3 should have linked student", contact2.getStudent());
		//load voucher
		Voucher voucher = (Voucher) context.performQuery(new SelectQuery(Voucher.class,ExpressionFactory.matchDbExp(Voucher.ID_PK_COLUMN, 3L))).get(0);
		assertNotNull("Voucher should be created", voucher);
		assertEquals("Payment amount should be the voucher price because the voucher product have no price", 
			voucher.getRedemptionValue(), new Money("10.00"));
		assertNull("Voucher should not be linked to any contact because the payer and owner equal", voucher.getContact());
		//load courseclass for enrolment
		CourseClass courseClass = (CourseClass) context.performQuery(new SelectQuery(CourseClass.class, 
			ExpressionFactory.matchDbExp(CourseClass.ID_PK_COLUMN, 1L))).get(0);
		VoucherProductCourse voucherProductCourse = context.newObject(VoucherProductCourse.class);
		voucherProductCourse.setCollege(contact.getCollege());
		voucherProductCourse.setCourse(courseClass.getCourse());
		voucherProductCourse.setVoucherProduct(voucher.getVoucherProduct());
		//commit the changes now to affect this changes
		context.commitChanges();
		voucher = (Voucher) context.performQuery(new SelectQuery(Voucher.class,ExpressionFactory.matchDbExp(Voucher.ID_PK_COLUMN, 3L))).get(0);
		assertNotNull("Voucher should be created", voucher);
		assertNotNull("This voucher should be the course voucher", voucher.getVoucherProduct().getRedemptionCourses());
		assertEquals("This voucher should be the course voucher and have 1 linked course", 1, 
			voucher.getVoucherProduct().getRedemptionCourses().size());
		assertNull("Voucher should not be linked to any contact because the payer and owner equal", voucher.getContact());
		
		//prepare invoice for helper
		Invoice invoice = createInvoice(context);
		
		InvoiceLine invoiceLine = context.newObject(InvoiceLine.class);
		invoiceLine.setCollege(invoice.getCollege());
		invoiceLine.setInvoice(invoice);
		invoiceLine.setDescription("test invoiceline 1");
		invoiceLine.setTitle(invoiceLine.getDescription());
		invoiceLine.setDiscountEachExTax(Money.ZERO);
		invoiceLine.setPriceEachExTax(new Money("90.00"));
		invoiceLine.setTaxEach(new Money("10.00"));
		invoiceLine.setQuantity(BigDecimal.ONE);
		
		Enrolment enrolment = context.newObject(Enrolment.class);
		enrolment.setCollege(invoiceLine.getCollege());
		enrolment.setCourseClass(courseClass);
		enrolment.setInvoiceLine(invoiceLine);
		enrolment.setSource(invoice.getSource());
		enrolment.setStatus(EnrolmentStatus.NEW);
		enrolment.setStudent(contact.getStudent());
		enrolment.setReasonForStudy(1);
		
		InvoiceLine invoiceLine2 = context.newObject(InvoiceLine.class);
		invoiceLine2.setCollege(invoice.getCollege());
		invoiceLine2.setInvoice(invoice);
		invoiceLine2.setDescription("test invoiceline 1");
		invoiceLine2.setTitle(invoiceLine.getDescription());
		invoiceLine2.setDiscountEachExTax(Money.ZERO);
		invoiceLine2.setPriceEachExTax(new Money("45.00"));
		invoiceLine2.setTaxEach(new Money("5.00"));
		invoiceLine2.setQuantity(BigDecimal.ONE);
		invoice.setTotalGst(invoiceLine2.getDiscountedPriceTotalIncTax());
		invoice.setTotalExGst(invoiceLine2.getPriceTotalExTax());
		
		Enrolment enrolment2 = context.newObject(Enrolment.class);
		enrolment2.setCollege(invoiceLine2.getCollege());
		enrolment2.setCourseClass(courseClass);
		enrolment2.setInvoiceLine(invoiceLine2);
		enrolment2.setSource(invoice.getSource());
		enrolment2.setStatus(EnrolmentStatus.NEW);
		enrolment2.setStudent(contact2.getStudent());
		enrolment2.setReasonForStudy(1);
		
		invoice.setTotalGst(invoiceLine.getDiscountedPriceTotalIncTax().add(invoiceLine2.getDiscountedPriceTotalIncTax()));
		invoice.setTotalExGst(invoiceLine.getPriceTotalExTax().add(invoiceLine2.getPriceTotalExTax()));
		
		//context.commitChanges();
		//start helper
		VoucherRedemptionHelper helper = new VoucherRedemptionHelper(invoice, Arrays.asList(voucher));
		helper.calculateVouchersRedeemPayment();
		assertNotNull("One payment should be created", helper.getPayments());
		assertEquals("One payment should be created", 1, helper.getPayments().size());
		List<Voucher> vouchers = helper.getVouchers();
		assertEquals("After calculation 1 voucher should be used", 1, vouchers.size());
		assertEquals("After calculation 1 voucher should be used and should be redeemed", ProductStatus.REDEEMED, 
				voucher.getStatus());
		
		invoice.updateAmountOwing();
		assertEquals("Amount owing should be 0 for course voucher", Money.ZERO, 
			invoice.getAmountOwing().subtract(helper.getPayments().get(0).getAmount()));
		//call re-calculate to check that the result will be the same
		helper.calculateVouchersRedeemPayment();
		assertNotNull("One payment should be created", helper.getPayments());
		assertEquals("One payment should be created", 1, helper.getPayments().size());
		vouchers = helper.getVouchers();
		assertEquals("After calculation 1 voucher should be used", 1, vouchers.size());
		assertEquals("After calculation 1 voucher should be used and should be redeemed", ProductStatus.REDEEMED, 
				voucher.getStatus());
		
		invoice.updateAmountOwing();
		assertEquals("Amount owing should be 0 for course voucher", Money.ZERO, 
			invoice.getAmountOwing().subtract(helper.getPayments().get(0).getAmount()));
	}
	
	@Test
	public void testVoucherRedemptionHelperForManyCourseVouchers() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		//load contact
		Contact contact = (Contact) context.performQuery(new SelectQuery(Contact.class, 
			ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 1L))).get(0);
		assertNotNull("Contact with id=1 should exist", contact);
		assertNotNull("Contact with id=1 should have linked student", contact.getStudent());
		Contact contact2 = (Contact) context.performQuery(new SelectQuery(Contact.class, 
			ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 3L))).get(0);
		assertNotNull("Contact with id=3 should exist", contact2);
		assertNotNull("Contact with id=3 should have linked student", contact2.getStudent());
		//load voucher
		Voucher voucher = (Voucher) context.performQuery(new SelectQuery(Voucher.class,ExpressionFactory.matchDbExp(Voucher.ID_PK_COLUMN, 3L))).get(0);
		assertNotNull("Voucher should be created", voucher);
		assertEquals("Payment amount should be the voucher price because the voucher product have no price", 
			voucher.getRedemptionValue(), new Money("10.00"));
		assertNull("Voucher should not be linked to any contact because the payer and owner equal", voucher.getContact());
		Voucher voucher2 = (Voucher) context.performQuery(new SelectQuery(Voucher.class,ExpressionFactory.matchDbExp(Voucher.ID_PK_COLUMN, 4L))).get(0);
		assertNotNull("Voucher should be created", voucher2);
		assertEquals("Payment amount should be the voucher price because the voucher product have no price", 
				voucher2.getRedemptionValue(), new Money("10.00"));
		assertNull("Voucher should not be linked to any contact because the payer and owner equal", voucher.getContact());
		//load courseclass for enrolment
		CourseClass courseClass = (CourseClass) context.performQuery(new SelectQuery(CourseClass.class, 
			ExpressionFactory.matchDbExp(CourseClass.ID_PK_COLUMN, 1L))).get(0);
		assertNotNull("CourseClass should be created", courseClass);
		CourseClass courseClass2 = (CourseClass) context.performQuery(new SelectQuery(CourseClass.class, 
			ExpressionFactory.matchDbExp(CourseClass.ID_PK_COLUMN, 2L))).get(0);
		assertNotNull("CourseClass should be created", courseClass2);
		VoucherProductCourse voucherProductCourse = context.newObject(VoucherProductCourse.class);
		voucherProductCourse.setCollege(contact.getCollege());
		voucherProductCourse.setCourse(courseClass.getCourse());
		voucherProductCourse.setVoucherProduct(voucher.getVoucherProduct());
		
		VoucherProductCourse voucherProductCourse2 = context.newObject(VoucherProductCourse.class);
		voucherProductCourse2.setCollege(contact.getCollege());
		voucherProductCourse2.setCourse(courseClass2.getCourse());
		voucherProductCourse2.setVoucherProduct(voucher2.getVoucherProduct());
		//commit the changes now to affect this changes
		context.commitChanges();
		voucher = (Voucher) context.performQuery(new SelectQuery(Voucher.class,ExpressionFactory.matchDbExp(Voucher.ID_PK_COLUMN, 3L))).get(0);
		assertNotNull("Voucher should be created", voucher);
		assertNotNull("This voucher should be the course voucher", voucher.getVoucherProduct().getRedemptionCourses());
		assertEquals("This voucher should be the course voucher and have 1 linked course", 1, 
			voucher.getVoucherProduct().getRedemptionCourses().size());
		assertNull("Voucher should not be linked to any contact because the payer and owner equal", voucher.getContact());
		
		voucher2 = (Voucher) context.performQuery(new SelectQuery(Voucher.class,ExpressionFactory.matchDbExp(Voucher.ID_PK_COLUMN, 4L))).get(0);
		assertNotNull("Voucher should be created", voucher2);
		assertNotNull("This voucher should be the course voucher", voucher2.getVoucherProduct().getRedemptionCourses());
		assertEquals("This voucher should be the course voucher and have 1 linked course", 1, 
			voucher2.getVoucherProduct().getRedemptionCourses().size());
		assertNull("Voucher should not be linked to any contact because the payer and owner equal", voucher2.getContact());
		
		//prepare invoice for helper
		Invoice invoice = createInvoice(context);

		InvoiceLine invoiceLine = context.newObject(InvoiceLine.class);
		invoiceLine.setCollege(invoice.getCollege());
		invoiceLine.setInvoice(invoice);
		invoiceLine.setDescription("test invoiceline 1");
		invoiceLine.setTitle(invoiceLine.getDescription());
		invoiceLine.setDiscountEachExTax(Money.ZERO);
		invoiceLine.setPriceEachExTax(new Money("90.00"));
		invoiceLine.setTaxEach(new Money("10.00"));
		invoiceLine.setQuantity(BigDecimal.ONE);
		
		Enrolment enrolment = context.newObject(Enrolment.class);
		enrolment.setCollege(invoiceLine.getCollege());
		enrolment.setCourseClass(courseClass);
		enrolment.setInvoiceLine(invoiceLine);
		enrolment.setSource(invoice.getSource());
		enrolment.setStatus(EnrolmentStatus.NEW);
		enrolment.setStudent(contact.getStudent());
		enrolment.setReasonForStudy(1);
		
		InvoiceLine invoiceLine2 = context.newObject(InvoiceLine.class);
		invoiceLine2.setCollege(invoice.getCollege());
		invoiceLine2.setInvoice(invoice);
		invoiceLine2.setDescription("test invoiceline 1");
		invoiceLine2.setTitle(invoiceLine.getDescription());
		invoiceLine2.setDiscountEachExTax(Money.ZERO);
		invoiceLine2.setPriceEachExTax(new Money("45.00"));
		invoiceLine2.setTaxEach(new Money("5.00"));
		invoiceLine2.setQuantity(BigDecimal.ONE);
		invoice.setTotalGst(invoiceLine2.getDiscountedPriceTotalIncTax());
		invoice.setTotalExGst(invoiceLine2.getPriceTotalExTax());
		
		Enrolment enrolment2 = context.newObject(Enrolment.class);
		enrolment2.setCollege(invoiceLine2.getCollege());
		enrolment2.setCourseClass(courseClass2);
		enrolment2.setInvoiceLine(invoiceLine2);
		enrolment2.setSource(invoice.getSource());
		enrolment2.setStatus(EnrolmentStatus.NEW);
		enrolment2.setStudent(contact2.getStudent());
		enrolment2.setReasonForStudy(1);
		
		invoice.setTotalGst(invoiceLine.getDiscountedPriceTotalIncTax().add(invoiceLine2.getDiscountedPriceTotalIncTax()));
		invoice.setTotalExGst(invoiceLine.getPriceTotalExTax().add(invoiceLine2.getPriceTotalExTax()));
		
		//context.commitChanges();
		//start helper
		VoucherRedemptionHelper helper = new VoucherRedemptionHelper(invoice, Arrays.asList(voucher, voucher2));
		helper.calculateVouchersRedeemPayment();
		assertNotNull("Two payments should be created", helper.getPayments());
		assertEquals("Two payments should be created", 2, helper.getPayments().size());
		List<Voucher> vouchers = helper.getVouchers();
		assertEquals("After calculation 2 vouchers should be used", 2, vouchers.size());
		assertEquals("After calculation 2 vouchers should be used and first should be active", ProductStatus.ACTIVE, 
			vouchers.get(0).getStatus());
		assertEquals("After calculation 2 vouchers should be used and second should be redemed", ProductStatus.REDEEMED, 
			vouchers.get(1).getStatus());
		assertEquals("After calculation 2 vouchers should be used and first should have 1 extra course place", 1, 
			vouchers.get(0).getClassesRemaining().intValue());
		
		invoice.updateAmountOwing();
		assertEquals("Amount owing should be 0 for course voucher", Money.ZERO, 
			invoice.getAmountOwing().subtract(helper.getPayments().get(0).getAmount()).subtract(helper.getPayments().get(1).getAmount()));
		
		//call re-calculate to check that the result will be the same
		helper.calculateVouchersRedeemPayment();
		assertNotNull("Two payments should be created", helper.getPayments());
		assertEquals("Two payments should be created", 2, helper.getPayments().size());
		vouchers = helper.getVouchers();
		assertEquals("After calculation 2 vouchers should be used", 2, vouchers.size());
		assertEquals("After calculation 2 vouchers should be used and first should be active", ProductStatus.ACTIVE, 
			vouchers.get(0).getStatus());
		assertEquals("After calculation 2 vouchers should be used and second should be redemed", ProductStatus.REDEEMED, 
			vouchers.get(1).getStatus());
		assertEquals("After calculation 2 vouchers should be used and first should have 1 extra course place", 1, 
			vouchers.get(0).getClassesRemaining().intValue());
		
		invoice.updateAmountOwing();
		assertEquals("Amount owing should be 0 for course voucher", Money.ZERO, 
			invoice.getAmountOwing().subtract(helper.getPayments().get(0).getAmount()).subtract(helper.getPayments().get(1).getAmount()));
	}
	
	@Test
	public void testVoucherRedemptionHelperForMoneyAndCourseVouchers() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		//load contact
		Contact contact = (Contact) context.performQuery(new SelectQuery(Contact.class, 
			ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 1L))).get(0);

		assertNotNull("Contact with id=1 should exist", contact);
		assertNotNull("Contact with id=1 should have linked student", contact.getStudent());
		Contact contact2 = (Contact) context.performQuery(new SelectQuery(Contact.class, 
			ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 3L))).get(0);
		assertNotNull("Contact with id=3 should exist", contact2);
		assertNotNull("Contact with id=3 should have linked student", contact2.getStudent());
		//load voucher
		Voucher voucher = (Voucher) context.performQuery(new SelectQuery(Voucher.class,ExpressionFactory.matchDbExp(Voucher.ID_PK_COLUMN, 2L))).get(0);
		assertNotNull("Voucher should be created", voucher);
		assertEquals("Payment amount should be the voucher price because the voucher product have a price", 
			voucher.getRedemptionValue(), new Money("101.00"));
		assertNull("Voucher should not be linked to any contact because the payer and owner equal", voucher.getContact());

		Voucher voucher2 = (Voucher) context.performQuery(new SelectQuery(Voucher.class,ExpressionFactory.matchDbExp(Voucher.ID_PK_COLUMN, 4L))).get(0);
		assertNotNull("Voucher should be created", voucher2);
		assertEquals("Payment amount should be the voucher price because the voucher product have no price", 
				voucher2.getRedemptionValue(), new Money("10.00"));
		assertNull("Voucher should not be linked to any contact because the payer and owner equal", voucher.getContact());
		//load courseclass for enrolment
		CourseClass courseClass = (CourseClass) context.performQuery(new SelectQuery(CourseClass.class, 
			ExpressionFactory.matchDbExp(CourseClass.ID_PK_COLUMN, 1L))).get(0);
		assertNotNull("CourseClass should be created", courseClass);
		CourseClass courseClass2 = (CourseClass) context.performQuery(new SelectQuery(CourseClass.class, 
			ExpressionFactory.matchDbExp(CourseClass.ID_PK_COLUMN, 2L))).get(0);
		assertNotNull("CourseClass should be created", courseClass2);
		
		VoucherProductCourse voucherProductCourse2 = context.newObject(VoucherProductCourse.class);
		voucherProductCourse2.setCollege(contact.getCollege());
		voucherProductCourse2.setCourse(courseClass2.getCourse());
		voucherProductCourse2.setVoucherProduct(voucher2.getVoucherProduct());
		//commit the changes now to affect this changes
		context.commitChanges();
		voucher = (Voucher) context.performQuery(new SelectQuery(Voucher.class,ExpressionFactory.matchDbExp(Voucher.ID_PK_COLUMN, 2L))).get(0);
		assertNotNull("Voucher should be created", voucher);
		assertNotNull("This voucher should be the course voucher", voucher.getVoucherProduct().getRedemptionCourses());
		assertEquals("This voucher should be the course voucher and have no linked course", 0, 
			voucher.getVoucherProduct().getRedemptionCourses().size());
		assertNull("Voucher should not be linked to any contact because the payer and owner equal", voucher.getContact());
		
		voucher2 = (Voucher) context.performQuery(new SelectQuery(Voucher.class,ExpressionFactory.matchDbExp(Voucher.ID_PK_COLUMN, 4L))).get(0);
		assertNotNull("Voucher should be created", voucher2);
		assertNotNull("This voucher should be the course voucher", voucher2.getVoucherProduct().getRedemptionCourses());
		assertEquals("This voucher should be the course voucher and have 1 linked course", 1, 
			voucher2.getVoucherProduct().getRedemptionCourses().size());
		assertNull("Voucher should not be linked to any contact because the payer and owner equal", voucher2.getContact());
		
		//prepare invoice for helper
		Invoice invoice = createInvoice(context);

		InvoiceLine invoiceLine = context.newObject(InvoiceLine.class);
		invoiceLine.setCollege(invoice.getCollege());
		invoiceLine.setInvoice(invoice);
		invoiceLine.setDescription("test invoiceline 1");
		invoiceLine.setTitle(invoiceLine.getDescription());
		invoiceLine.setDiscountEachExTax(Money.ZERO);
		invoiceLine.setPriceEachExTax(new Money("90.00"));
		invoiceLine.setTaxEach(new Money("10.00"));
		invoiceLine.setQuantity(BigDecimal.ONE);
		
		Enrolment enrolment = context.newObject(Enrolment.class);
		enrolment.setCollege(invoiceLine.getCollege());
		enrolment.setCourseClass(courseClass);
		enrolment.setInvoiceLine(invoiceLine);
		enrolment.setSource(invoice.getSource());
		enrolment.setStatus(EnrolmentStatus.NEW);
		enrolment.setStudent(contact.getStudent());
		enrolment.setReasonForStudy(1);
		
		InvoiceLine invoiceLine2 = context.newObject(InvoiceLine.class);
		invoiceLine2.setCollege(invoice.getCollege());
		invoiceLine2.setInvoice(invoice);
		invoiceLine2.setDescription("test invoiceline 1");
		invoiceLine2.setTitle(invoiceLine.getDescription());
		invoiceLine2.setDiscountEachExTax(Money.ZERO);
		invoiceLine2.setPriceEachExTax(new Money("45.00"));
		invoiceLine2.setTaxEach(new Money("5.00"));
		invoiceLine2.setQuantity(BigDecimal.ONE);
		invoice.setTotalGst(invoiceLine2.getDiscountedPriceTotalIncTax());
		invoice.setTotalExGst(invoiceLine2.getPriceTotalExTax());
		
		Enrolment enrolment2 = context.newObject(Enrolment.class);
		enrolment2.setCollege(invoiceLine2.getCollege());
		enrolment2.setCourseClass(courseClass2);
		enrolment2.setInvoiceLine(invoiceLine2);
		enrolment2.setSource(invoice.getSource());
		enrolment2.setStatus(EnrolmentStatus.NEW);
		enrolment2.setStudent(contact2.getStudent());
		enrolment2.setReasonForStudy(1);
		
		invoice.setTotalGst(invoiceLine.getDiscountedPriceTotalIncTax().add(invoiceLine2.getDiscountedPriceTotalIncTax()));
		invoice.setTotalExGst(invoiceLine.getPriceTotalExTax().add(invoiceLine2.getPriceTotalExTax()));

		//start helper
		VoucherRedemptionHelper helper = new VoucherRedemptionHelper(invoice, Arrays.asList(voucher, voucher2));
		helper.calculateVouchersRedeemPayment();
		assertNotNull("Two payments should be created", helper.getPayments());
		assertEquals("Two payments should be created", 2, helper.getPayments().size());
		List<Voucher> vouchers = helper.getVouchers();
		assertEquals("After calculation 2 vouchers should be used", 2, vouchers.size());
		assertEquals("After calculation 2 vouchers should be used and first should be redeemed", ProductStatus.REDEEMED, 
			vouchers.get(0).getStatus());
		assertEquals("After calculation 2 vouchers should be used and second should be active", ProductStatus.ACTIVE, 
			vouchers.get(1).getStatus());
		assertEquals("After calculation 2 vouchers should be used and second should have 1 $ value", Money.ONE, 
			vouchers.get(1).getRedemptionValue());
		
		invoice.updateAmountOwing();
		assertEquals("Amount owing should be 0 for course voucher", Money.ZERO, 
			invoice.getAmountOwing().subtract(helper.getPayments().get(0).getAmount()).subtract(helper.getPayments().get(1).getAmount()));
		
		//call re-calculate to check that the result will be the same
		helper.calculateVouchersRedeemPayment();
		assertNotNull("Two payments should be created", helper.getPayments());
		assertEquals("Two payments should be created", 2, helper.getPayments().size());
		vouchers = helper.getVouchers();
		assertEquals("After calculation 2 vouchers should be used", 2, vouchers.size());
		assertEquals("After calculation 2 vouchers should be used and first should be redeemed", ProductStatus.REDEEMED, 
			vouchers.get(0).getStatus());
		assertEquals("After calculation 2 vouchers should be used and second should be active", ProductStatus.ACTIVE, 
			vouchers.get(1).getStatus());
		assertEquals("After calculation 2 vouchers should be used and second should have 1 $ value", Money.ONE, 
			vouchers.get(1).getRedemptionValue());
		
		invoice.updateAmountOwing();
		assertEquals("Amount owing should be 0 for course voucher", Money.ZERO, 
			invoice.getAmountOwing().subtract(helper.getPayments().get(0).getAmount()).subtract(helper.getPayments().get(1).getAmount()));
	}

}
