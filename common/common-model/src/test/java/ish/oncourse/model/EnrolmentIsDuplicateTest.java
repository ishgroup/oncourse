package ish.oncourse.model;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Created by akoiro on 15/04/2016.
 */
public class EnrolmentIsDuplicateTest {


	@Test
	public void testIsDuplicateTrue() {
		Student student = Mockito.mock(Student.class);
		CourseClass courseClass = Mockito.mock(CourseClass.class);

		Enrolment enrolment0 = createEnrolment(student, courseClass);

		when(courseClass.getValidEnrolments()).thenReturn(Collections.singletonList(enrolment0));

		Enrolment enrolment1 = createEnrolment(student, courseClass);
		when(enrolment1.isDuplicated()).thenCallRealMethod();

		assertTrue(enrolment1.isDuplicated());
	}

	@Test
	public void testIsDuplicateFalse() {
		Student student = Mockito.mock(Student.class);
		CourseClass courseClass = Mockito.mock(CourseClass.class);

		when(courseClass.getValidEnrolments()).thenReturn(Collections.<Enrolment>emptyList());

		Enrolment enrolment1 = createEnrolment(student, courseClass);
		when(enrolment1.isDuplicated()).thenCallRealMethod();

		assertFalse(enrolment1.isDuplicated());
	}


	private Enrolment createEnrolment(Student student, CourseClass courseClass) {
		Enrolment enrolment0 = Mockito.mock(Enrolment.class);
		when(enrolment0.getStudent()).thenReturn(student);
		when(enrolment0.getCourseClass()).thenReturn(courseClass);
		return enrolment0;
	}
}
