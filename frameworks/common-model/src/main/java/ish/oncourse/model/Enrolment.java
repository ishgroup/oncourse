package ish.oncourse.model;

import ish.common.types.EnrolmentStatus;
import ish.oncourse.model.auto._Enrolment;

import java.util.List;


public class Enrolment extends _Enrolment {

	public boolean isDuplicated(Student student) {
		if (getCourseClass() != null && isSuccessfullOrQueued()) {
			List<Enrolment> enrolments = getStudent().getEnrolments();
			boolean duplicateEnroment = false;
			if (enrolments != null)
				for (Enrolment enrolment : enrolments)
					if (!equals(enrolment))//TODO equalsIgnoreContext
						if (getCourseClass().equals(enrolment.getCourseClass()))//TODO equalsIgnoreContext
							if (enrolment.isSuccessfullOrQueued())
								duplicateEnroment = true;

			if (duplicateEnroment) {
				return true;
			}
		}
		return false;
	}

	
	/**
	 * An enrolments is successfull when its status is 'success' or 'queued', or null (the latter means, not processed yet, not even committed)
	 * 
	 * @return true if the enrolment is considered as successful
	 */
	public boolean isSuccessfullOrQueued() {
		return getStatus() == null || getStatus().equals(EnrolmentStatus.SUCCESS) || getStatus().equals(EnrolmentStatus.QUEUED);
	}
	
	/**
	 * checks for PersistentObject equality (even if in another context)
	 * 
	 * @param obj a persistent object to compare to
	 * @return true if the same object
	 */
	/*public boolean equalsIgnoreContext(Object obj) {
		if (obj instanceof PersistentObject) {
			return super.equals(obj) || getObjectId().equals(((PersistentObject) obj).getObjectId());
		}
		return false;
	}*/
}
