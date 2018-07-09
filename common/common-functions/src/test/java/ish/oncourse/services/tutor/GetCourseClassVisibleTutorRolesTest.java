/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.tutor;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Tutor;
import ish.oncourse.model.TutorRole;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetCourseClassVisibleTutorRolesTest {

	@Test
	public void test() {

		List roles = new ArrayList<>();
		roles.add(mockTutorRole(null, 1L));
		roles.add(mockTutorRole(DateUtils.addDays(new Date(), 1), 2L));
		roles.add(mockTutorRole(DateUtils.addDays(new Date(), 0), 3L));
		roles.add(mockTutorRole(DateUtils.addDays(new Date(), -1), 4L));

		CourseClass courseClass = mock(CourseClass.class);
		ObjectContext objectContext = mock(ObjectContext.class);
		when(objectContext.select(Matchers.any())).thenReturn(roles);

		List<TutorRole> res = GetCourseClassVisibleTutorRoles.valueOf(objectContext, courseClass).get();

		assertEquals(2, res.size());
		assertEquals("Only active tutors should be filtered (finish date == null || date.after(now)).",
				2, res.stream().filter(role -> role.getId() == 1L || role.getId() == 2L).count());
	}

	private TutorRole mockTutorRole(Date date, long roleId) {
		TutorRole role = mock(TutorRole.class);
		Tutor tutor = mock(Tutor.class);
		when(role.getTutor()).thenReturn(tutor);
		when(role.getId()).thenReturn(roleId);
		when(tutor.getFinishDate()).thenReturn(date);
		return role;
	}

}
