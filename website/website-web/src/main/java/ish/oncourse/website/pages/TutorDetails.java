package ish.oncourse.website.pages;

import ish.oncourse.model.Tutor;
import ish.oncourse.model.TutorRole;
import ish.oncourse.services.tutor.ITutorService;

import java.util.List;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.ioc.annotations.Inject;

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

	private TutorRole role;

	public boolean getTutorFound() {
		return tutor != null;
	}

	public boolean getHasResume() {
		return !"".equals(tutor.getResume());
	}

	public boolean getHasRoles() {
		return !tutorService.getCurrentVisibleTutorRoles(tutor).isEmpty();
	}

	public List<TutorRole> getCurrentVisibleTutorRoles() {
		return tutorService.getCurrentVisibleTutorRoles(tutor);
	}

	/**
	 * @return the role
	 */
	public TutorRole getRole() {
		return role;
	}

	/**
	 * @param role
	 *            the role to set
	 */
	public void setRole(TutorRole role) {
		this.role = role;
	}

}
