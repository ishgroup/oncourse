package ish.oncourse.ui.pages;

import ish.oncourse.model.Tutor;
import ish.oncourse.model.TutorRole;
import ish.oncourse.services.tutor.ITutorService;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.List;

public class TutorDetails {
	@Inject
	private ITutorService tutorService;

	@Inject
	private Request request;
	
	@Persist
	@Property
	private Tutor tutor;
	
	@Property
	private TutorRole role;

	@SetupRender
	public void beforeRender() {
		String id = (String) request.getAttribute("tutorId");
		tutor = tutorService.getTutorById(Long.valueOf(id));
	}
	
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

}
