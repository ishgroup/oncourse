package ish.oncourse.services.enrol;

import ish.oncourse.model.Enrolment;

public interface IEnrolmentService {
	Enrolment loadById(Long id);
}
