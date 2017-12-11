package ish.oncourse.portal.services.dashboard

import groovy.time.TimeCategory
import ish.math.Money
import ish.oncourse.model.Contact
import ish.oncourse.portal.services.APortalTest
import org.junit.Test

import static org.apache.cayenne.query.SelectById.query
import static org.junit.Assert.assertEquals

class CalculatePaymentDueTest extends APortalTest {


	@Override
	protected Map<String, Object> replacements() {
		Map<String, Object> replacements = new HashMap<>()
		use(TimeCategory) {
			def today = new Date()
			def plu3days = today + 3.day
			def plu4days = today + 4.day
			def plu8days = today + 8.day
			def minus4days = today - 4.day


			replacements.put("[today]", today)
			replacements.put("[threeDaysLater]", plu3days)
			replacements.put("[fourDaysLater]", plu4days)
			replacements.put("[eightDaysLater]", plu8days)
			replacements.put("[fourDaysAgo]", minus4days)
		}
		return replacements
	}

	@Test
	public void test() {
		Contact contact = query(Contact, 1L).selectOne(objectContext)
		Money toPay = new CalculatePaymentDue(contact).calculate();
		assertEquals(new Money("60.00"), toPay)

	}
}
