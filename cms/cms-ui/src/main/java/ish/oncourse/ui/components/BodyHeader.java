package ish.oncourse.ui.components;

import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.services.college.ICollegeService;

public class BodyHeader {

	@Inject
	private ICollegeService collegeService;
	
	public String getCollegeName() {
		return collegeService.getCurrentCollege().getName();
	}
}
