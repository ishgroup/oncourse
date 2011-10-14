package ish.oncourse.webservices.services;

import ish.oncourse.model.College;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.springframework.beans.factory.annotation.Autowired;

public class CollegeRequestService implements ICollegeRequestService {

	@Inject
	@Autowired
	private Request request;

	@Override
	public College getRequestingCollege() {
		College college = (College) request.getAttribute(College.REQUESTING_COLLEGE_ATTRIBUTE);
		return college;
	}
}
