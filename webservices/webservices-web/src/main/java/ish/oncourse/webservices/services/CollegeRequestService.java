package ish.oncourse.webservices.services;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import ish.oncourse.model.College;
import ish.oncourse.webservices.util.SoapUtil;

public class CollegeRequestService implements ICollegeRequestService {

	@Inject
	private Request request;

	@Override
	public College getRequestingCollege() {
		College college = (College) request.getAttribute(SoapUtil.REQUESTING_COLLEGE);
		return college;
	}
}
