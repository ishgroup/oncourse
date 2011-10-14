package ish.oncourse.ui.pages;

import ish.oncourse.model.Tutor;
import ish.oncourse.model.TutorRole;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.services.tutor.ITutorService;
import ish.oncourse.util.ValidationErrors;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.List;

public class TutorDetails {
	@Inject
	private ITutorService tutorService;

	@Inject
	private ITextileConverter textileConverter;

	@Inject
	private Request request;

	@Property
	private Tutor tutor;

	@Property
	private TutorRole role;

	@SetupRender
	public void beforeRender() {
		String id = (String) request.getAttribute("tutorId");
		tutor = tutorService.findByAngelId(Long.valueOf(id));
	}

	public boolean getTutorFound() {
		return tutor != null;
	}

	public boolean getHasResume() {
		return tutor.getResume() != null && !"".equals(tutor.getResume());
	}

	public boolean getHasRoles() {
		return !tutor.getCurrentVisibleTutorRoles().isEmpty();
	}

	public List<TutorRole> getCurrentVisibleTutorRoles() {
		return tutor.getCurrentVisibleTutorRoles();
	}

	public String getTutorDetailsTitle() {
		if (getTutorFound()) {
			return tutor.getFullName();
		}
		return "Tutor not found";
	}

	public String getResume() {
		return textileConverter.convertCustomTextile(tutor.getResume(), new ValidationErrors());
	}
}
