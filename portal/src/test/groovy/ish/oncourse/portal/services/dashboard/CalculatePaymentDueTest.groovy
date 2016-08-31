package ish.oncourse.portal.services.dashboard

import groovy.time.TimeCategory
import ish.math.Money
import ish.oncourse.model.Contact
import ish.oncourse.model.Module
import ish.oncourse.portal.services.APortalTest
import org.dbunit.dataset.IDataSet
import org.dbunit.dataset.ReplacementDataSet
import org.junit.Test

import static org.apache.cayenne.query.SelectById.query
import static org.junit.Assert.assertEquals

class CalculatePaymentDueTest extends APortalTest {


	@Override
	protected IDataSet adjustDataSet(IDataSet dataSet) {
		ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet)
		use(TimeCategory) {
			def today = new Date()
			def plu3days = today + 3.day
			def plu4days = today + 4.day
			def plu8days = today + 8.day
			def minus4days = today - 4.day

			rDataSet.addReplacementObject("[today]", today)
			rDataSet.addReplacementObject("[threeDaysLater]", plu3days)
			rDataSet.addReplacementObject("[fourDaysLater]", plu4days)
			rDataSet.addReplacementObject("[eightDaysLater]", plu8days)
			rDataSet.addReplacementObject("[fourDaysAgo]", minus4days)
		}
		
		return rDataSet
	}

	@Test
	public void test() {
		Contact contact = query(Contact, 1L).selectOne(objectContext)
		Money toPay = new CalculatePaymentDue(contact).calculate();
		assertEquals(new Money("60.00"), toPay)

	}
}
