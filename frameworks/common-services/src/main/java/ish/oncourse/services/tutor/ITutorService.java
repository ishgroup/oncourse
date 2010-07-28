package ish.oncourse.services.tutor;

import java.util.List;

import ish.oncourse.model.Tutor;
import ish.oncourse.model.TutorRole;

public interface ITutorService {

	Tutor getTutorById(Long tutorId);
	
	List<TutorRole> getCurrentVisibleTutorRoles(Tutor tutor);
	
}
