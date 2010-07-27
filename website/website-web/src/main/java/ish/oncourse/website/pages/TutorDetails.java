package ish.oncourse.website.pages;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.model.Tutor;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.tutor.ITutorService;

public class TutorDetails {
	@Inject
	private ITutorService tutorService;

	@Persist
	private Tutor tutor;

	void onActivate(long id) {
		tutor = tutorService.getTutorById(id);
	}

	public Tutor getTutor() {
		return tutor;
	}

	// TODO this part is for testing while there will be real functional
	@Inject
	private ICayenneService cayenneService;

	public Tutor getObject() {
		Tutor tutor = cayenneService.sharedContext().newObject(Tutor.class);

		tutor.setFirstName("firstName");
		tutor.setLastName("lastName");
		tutor.setResume("Resume Resume Resume");
		
		return tutor;
	}

	public boolean getHasResume() {
		return true;
	}
}
