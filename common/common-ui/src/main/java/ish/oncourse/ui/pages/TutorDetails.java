package ish.oncourse.ui.pages;

import ish.common.types.AttachmentInfoVisibility;
import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.model.Document;
import ish.oncourse.model.Tutor;
import ish.oncourse.model.TutorRole;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.services.tutor.ITutorService;
import ish.oncourse.util.ValidationErrors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

public class TutorDetails extends ISHCommon {

	@Inject
	private IBinaryDataService binaryDataService;

	@Inject
	private ITutorService tutorService;

	@Inject
	private ITextileConverter textileConverter;
	
	@Property
	private Tutor tutor;

	@Property
	private TutorRole role;
	
	private static final Logger logger = LogManager.getLogger();

	private static final String TUTOR_ATTRIBUTE = "tutor";
	
	@SetupRender
	public void beforeRender() {
		tutor = (Tutor) request.getAttribute(TUTOR_ATTRIBUTE);
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

	public Document getProfilePicture()
	{
		Document profilePicture =  binaryDataService.getProfilePicture(tutor.getContact());
		if (profilePicture == null || AttachmentInfoVisibility.PRIVATE.equals(profilePicture.getWebVisibility())) {
			return null;
		} else {
			return profilePicture;
		}
	}
	
	public String getProfilePictureUrl() {
		return binaryDataService.getUrl(getProfilePicture());
	}
}
