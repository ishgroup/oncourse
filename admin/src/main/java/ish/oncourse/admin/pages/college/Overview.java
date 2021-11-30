package ish.oncourse.admin.pages.college;

import ish.oncourse.model.College;
import ish.oncourse.model.KeyStatus;
import ish.oncourse.model.Preference;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.system.ICollegeService;
import ish.persistence.Preferences;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Overview {

	private static final Logger logger = LogManager.getLogger();

	@Inject
	private ICollegeService collegeService;

	@Inject
	private ICayenneService cayenneService;

	@Property
	private College college;

	@Property
	private String lastIP;

	@Property
	private String onCourseVersion;

	@Property
	private String replicationState;

	@Property
	private String lastReplication;

	@Property
	private String collegeKey;

	@SetupRender
	void setupRender() {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		lastIP = college.getIpAddress();
		onCourseVersion = college.getAngelVersion();
		replicationState = college.getCommunicationKeyStatus().toString();
		lastReplication = college.getLastRemoteAuthentication() != null ? dateFormat.format(college.getLastRemoteAuthentication()) : "never";
		collegeKey = college.getCollegeKey();
	}

	void onActivate(Long id) {
		this.college = collegeService.findById(id);
	}

	Object onPassivate() {
		return college.getId();
	}

	Object onActionFromDisableCollege() {

		ObjectContext context = cayenneService.newContext();
		College college = context.localObject(this.college);

		college.setCommunicationKeyStatus(KeyStatus.HALT);
		context.commitChanges();

		return "Index";
	}
}
