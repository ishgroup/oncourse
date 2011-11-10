package ish.oncourse.admin.pages.college;

import ish.oncourse.model.College;
import ish.oncourse.services.system.ICollegeService;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class Overview {
	
	@Inject
	private ICollegeService collegeService;
	
	@Property
	@Persist
	private College college;
	
	@Property
	private String lastIP;
	
	@Property
	private String onCourseVersion;
	
	@Property
	private String replicationState;
	
	@Property
	private String lastReplication;
	
	@SetupRender
	void setupRender() {
		lastIP = college.getIpAddress();
		onCourseVersion = college.getAngelVersion();
		replicationState = college.getCommunicationKeyStatus().toString();
		lastReplication = "";
	}
	
	Object onActivate(Long id) {
		this.college = collegeService.findById(id);
		return null;
	}

}
