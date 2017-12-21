/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.tutor;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Tutor;
import ish.oncourse.model.TutorRole;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: akoiro
 * Date: 21/12/17
 */
public class GetVisibleTutorRolesTest {

	@Test
	public void test() {

		List roles = new ArrayList<>();
		roles.add(mockTutorRole(null));
		roles.add(mockTutorRole(DateUtils.addDays(new Date(), 1)));
		roles.add(mockTutorRole(DateUtils.addDays(new Date(), -1)));

		CourseClass courseClass = mock(CourseClass.class);
		ObjectContext objectContext = mock(ObjectContext.class);
		when(objectContext.select(Matchers.any())).thenReturn(roles);
		Assert.assertEquals(2, new GetVisibleTutorRoles().courseClass(courseClass).get(objectContext).size());
	}

	private TutorRole mockTutorRole(Date date) {
		TutorRole role = mock(TutorRole.class);
		Tutor tutor = mock(Tutor.class);
		when(role.getTutor()).thenReturn(tutor);
		when(tutor.getFinishDate()).thenReturn(date);
		return role;
	}

}
