package ish.oncourse.services.lifecycle;

import ish.math.Money;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import org.apache.cayenne.ObjectContext;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class InvoiceListenerTest extends ServiceTest {
	private ICayenneService cayenneService;

	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.services", "service", ServiceTestModule.class);

		this.cayenneService = getService(ICayenneService.class);
	}

	@Test
	public void testReplicatedContext() {
		ObjectContext context = this.cayenneService.newContext();
		College college = createCollege(context);
		Contact contact = createContact(college, context);


		//Test commit Invoice in replicated context
		Invoice invoice = createInvoice(context, college, contact);

		InvoiceLine invoiceLine = createInvoiceLine(context, college, invoice);
		context.commitChanges();
		assertEquals("amoutOwing should be recalculated when we perssist invoice in replicated context.", new Money(new BigDecimal(110)), invoice.getAmountOwing());

		invoice.setAmountOwing(Money.ZERO);
		context.commitChanges();
		assertEquals("amoutOwing should be recalculated when we update invoice in replicated context.", new Money(new BigDecimal(110)), invoice.getAmountOwing());

	}


	@Test
	public void testNotReplicatedContext() {
		ObjectContext context = this.cayenneService.newNonReplicatingContext();
		College college = createCollege(context);
		Contact contact = createContact(college, context);


		//Test commit Invoice in replicated context
		Invoice invoice = createInvoice(context, college, contact);

		InvoiceLine invoiceLine = createInvoiceLine(context, college, invoice);
		context.commitChanges();
		assertEquals("amoutOwing should be not recalculated when we perssist invoice in not replicated context.", Money.ZERO, invoice.getAmountOwing());

		invoice.setAmountOwing(Money.ZERO);
		context.commitChanges();
		assertEquals("amoutOwing should be not recalculated when we update invoice in not replicated context.", Money.ZERO, invoice.getAmountOwing());

	}

	private InvoiceLine createInvoiceLine(ObjectContext context, College college, Invoice invoice) {
		InvoiceLine invoiceLine = context.newObject(InvoiceLine.class);
		invoiceLine.setTitle(UUID.randomUUID().toString());
		invoiceLine.setCollege(college);
		invoiceLine.setInvoice(invoice);
		invoiceLine.setPriceEachExTax(new Money(new BigDecimal(100)));
		invoiceLine.setTaxEach(new Money(new BigDecimal(10)));
		invoiceLine.setDiscountEachExTax(Money.ZERO);
		invoiceLine.setQuantity(new BigDecimal(1));
		return invoiceLine;
	}

	private Invoice createInvoice(ObjectContext context, College college, Contact contact) {
		Invoice invoice = context.newObject(Invoice.class);
		invoice.setCollege(college);
		invoice.setContact(contact);
		invoice.setAmountOwing(Money.ZERO);
		invoice.setTotalExGst(new Money("100"));
		invoice.setTotalGst(new Money("110"));
		invoice.setDateDue(new Date());
		invoice.setInvoiceDate(new Date());
		return invoice;
	}

	public Contact createContact(College college, ObjectContext context) {
		Contact contact = context.newObject(Contact.class);
		contact.setGivenName(UUID.randomUUID().toString());
		contact.setFamilyName(UUID.randomUUID().toString());
		contact.setCollege(college);
		return contact;
	}

	public College createCollege(ObjectContext context) {
		College college = context.newObject(College.class);
		college.setFirstRemoteAuthentication(new Date());
		college.setName(UUID.randomUUID().toString());
		college.setTimeZone("Australia/Sydney");
		college.setWebServicesSecurityCode(UUID.randomUUID().toString().substring(0, 15));
		college.setRequiresAvetmiss(false);
		return college;
	}

}
