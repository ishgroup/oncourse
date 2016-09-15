package ish.oncourse.portal.services.timetable

import groovy.time.TimeCategory
import ish.oncourse.model.Contact
import ish.oncourse.model.CourseClass
import ish.oncourse.portal.services.APortalTest
import ish.oncourse.utils.DateUtils
import org.dbunit.dataset.IDataSet
import org.dbunit.dataset.ReplacementDataSet
import org.junit.Test
import static org.apache.cayenne.query.SelectById.query
import static org.junit.Assert.assertEquals

class GetContactClassesObjectSelectTest extends  APortalTest {

	@Override
	protected IDataSet adjustDataSet(IDataSet dataSet) {
		ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet)
		use(TimeCategory) {
			def startOfCurrentMomth = DateUtils.startOfMonth(new Date())
			def plu1days = startOfCurrentMomth + 1.day
			def plu1Month = startOfCurrentMomth + 1.month
			def minus1day = startOfCurrentMomth - 1.day
			def minus1Month = startOfCurrentMomth - 1.month

			rDataSet.addReplacementObject("[plus1days]", plu1days)
			rDataSet.addReplacementObject("[plus1Month]", plu1Month)
			rDataSet.addReplacementObject("[minus1day]", minus1day)
			rDataSet.addReplacementObject("[minus1Month]", minus1Month)
		}

		return rDataSet
	}

	@Test
	public void test() {
		Contact contact = query(Contact, 1L).selectOne(objectContext)
		List<CourseClass> classes = new GetContactClassesObjectSelect(new Date(), contact.student, contact.tutor).get().select(objectContext);
		assertEquals(classes.collect { it.id }.sort(), [1l,2l] )

		contact = query(Contact, 2L).selectOne(objectContext)
		classes = new GetContactClassesObjectSelect(new Date(), contact.student, contact.tutor).get().select(objectContext);
		assertEquals(classes.collect { it.id }.sort(), [2l])

		contact = query(Contact, 3L).selectOne(objectContext)
		classes = new GetContactClassesObjectSelect(new Date(), contact.student, contact.tutor).get().select(objectContext);
		assertEquals(classes.collect { it.id }.sort(), [1l,2l])
		
		contact = query(Contact, 4L).selectOne(objectContext)
		classes = new GetContactClassesObjectSelect(new Date(), contact.student, contact.tutor).get().select(objectContext);
		assertEquals(classes.collect { it.id }.sort(), [5l])

		contact = query(Contact, 5L).selectOne(objectContext)
		classes = new GetContactClassesObjectSelect(new Date(), contact.student, contact.tutor).get().select(objectContext);
		assertEquals(classes.collect { it.id }.sort(), [5l])
	}
}
