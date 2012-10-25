package ish.oncourse.enrol.utils;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import org.apache.cayenne.PersistenceState;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class for validation in enrol app.
 * 
 * @author dzmitry
 */
@Deprecated //will be removed after checkout/payemnt page will be ready
public class EnrolmentValidationUtil {
	
	/**
	 * Checks if places limit for any class linked to enrolments list will be exceeded 
	 * if they are going to be successful.
	 * 
	 * @param list of enrolments which will be counted when calculating places
	 * @return true if place limit will be exceeded for any class linked with enrolments passed
	 */
	public static boolean isPlacesLimitExceeded(List<Enrolment> enrolments) {
		
		Set<CourseClass> classes = new HashSet<CourseClass>();
		for (Enrolment e : enrolments) {
			classes.add(e.getCourseClass());
		}
		
		for (CourseClass cc : classes) {
			int availablePlaces = cc.getMaximumPlaces() - cc.validEnrolmentsCount();
			for (Enrolment e : enrolments) {
				if (e.getCourseClass().equals(cc) && e.getPersistenceState() == PersistenceState.NEW) {
					availablePlaces--;
				}
			}
			if (availablePlaces < 0) {
				return false;
			}
		}
		
		return true;
	}

}
