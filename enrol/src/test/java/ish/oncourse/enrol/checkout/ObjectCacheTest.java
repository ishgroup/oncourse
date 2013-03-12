package ish.oncourse.enrol.checkout;

import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Invoice;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ObjectCacheTest extends ACheckoutTest {

	@Before
	public void setup() throws Exception {
		super.setup("ish/oncourse/enrol/checkout/ObjectCacheTest.xml");
	}

	/**
	 * This test emulates update of invoice amountOwing from other application (services application) and checkes
	 * that getting value is actual.
	 */
	@Test
	public void testInvalidateInvoice() throws Exception {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		Contact contact = Cayenne.objectForPK(context, Contact.class, 1001);
		List<Invoice> invoices = contact.getInvoices();
		Invoice invoice = invoices.get(0);
		invoice.setAmountOwing(Money.ZERO.toBigDecimal());


		ObjectContext anotherContext = cayenneService.newNonReplicatingContext();
		contact = Cayenne.objectForPK(anotherContext, Contact.class, 1001);
		invoices = contact.getInvoices();
		Invoice invoice1 = invoices.get(0);
		anotherContext.invalidateObjects(Collections.singleton(invoice1));

		context.commitChanges();

	}


		/**
		 * This test emulates update of invoice amountOwing from other application (services application) and checkes
		 * that getting value is actual.
		 */
	@Test
	public void test() throws Exception {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		Contact contact = Cayenne.objectForPK(context, Contact.class, 1001);
		List<Invoice> invoices = contact.getInvoices();
		assertEquals(3, invoices.size());
		for (Invoice invoice : invoices) {
			assertEquals(new Money("-100.00"), Money.valueOf(invoice.getAmountOwing()));
		}

		//the code updates amountOwing by JDBC request, to exclude Cayenne functionality
		DataSource refDataSource = getDataSource("jdbc/oncourse");
		Connection connection = refDataSource.getConnection();
		PreparedStatement statement = connection.prepareStatement("update Invoice set amountOwing = ? where id = ?");
		statement.setLong(1, 0);
		statement.setLong(2, 1000);
		statement.execute();
		connection.commit();
		statement.close();
		connection.close();

		//the code load amountOwing by JDBC request, to check value.
		connection = refDataSource.getConnection();
		statement = connection.prepareStatement("select amountOwing from Invoice where id = ?");
		statement.setLong(1, 1000);
		statement.execute();
		ResultSet resultSet =  statement.getResultSet();
		while (resultSet.next())
		{
			long value = resultSet.getLong(1);
			assertEquals(0, value);
		}
		statement.close();
		connection.close();


		//the code load invoices by cayenne and check amountOwing value.
		context = cayenneService.newNonReplicatingContext();
		contact = Cayenne.objectForPK(context, Contact.class, 1001);
		invoices = contact.getInvoices();
		assertEquals(3, invoices.size());
		for (Invoice invoice : invoices) {
			if (invoice.getId().equals(1000L))
				assertEquals(Money.ZERO, Money.valueOf(invoice.getAmountOwing()));
			else
				assertEquals(new Money("-100.00"), Money.valueOf(invoice.getAmountOwing()));
		}
	}
}
