package ish.oncourse.ui.pages;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.Tutor;
import ish.oncourse.model.TutorRole;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.services.tutor.ITutorService;
import ish.oncourse.util.ValidationErrors;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.List;

public class TutorDetails {

	@Inject
	private IBinaryDataService binaryDataService;

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
	
	private static final Logger logger = Logger.getLogger(TutorDetails.class);
	
	@SetupRender
	public void beforeRender() {
		String id = (String) request.getAttribute("tutorId");
		if (id != null && id.length() > 0 && id.matches("\\d+")) {
			tutor = tutorService.findByAngelId(Long.valueOf(id));
		}
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

	public BinaryInfo getProfilePicture()
	{
		return binaryDataService.getProfilePicture(tutor.getContact());
	}
	
	public String getProfilePictureUrl() {
		return binaryDataService.getUrl(getProfilePicture());
	}
}
