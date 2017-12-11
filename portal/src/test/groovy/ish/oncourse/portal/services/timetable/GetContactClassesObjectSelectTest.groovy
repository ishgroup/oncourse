package ish.oncourse.portal.services.timetable

import groovy.time.TimeCategory
import ish.oncourse.model.Contact
import ish.oncourse.model.CourseClass
import ish.oncourse.portal.services.APortalTest
import ish.oncourse.utils.DateUtils
import org.junit.Test

import static org.apache.cayenne.query.SelectById.query
import static org.junit.Assert.assertEquals

class GetContactClassesObjectSelectTest extends  APortalTest {

	@Override
	protected Map<String, Object> replacements() {
		Map<String, Object> replacements = new HashMap<>()
		use(TimeCategory) {
			def startOfCurrentMomth = DateUtils.startOfMonth(new Date())
			def plu1days = startOfCurrentMomth + 1.day
			def plu1Month = startOfCurrentMomth + 1.month
			def minus1day = startOfCurrentMomth - 1.day
			def minus1Month = startOfCurrentMomth - 1.month

			replacements.put("[plus1days]", plu1days)
			replacements.put("[plus1Month]", plu1Month)
			replacements.put("[minus1day]", minus1day)
			replacements.put("[minus1Month]", minus1Month)
		}

		return replacements
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
