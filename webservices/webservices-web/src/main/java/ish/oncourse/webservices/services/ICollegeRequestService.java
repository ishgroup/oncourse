package ish.oncourse.webservices.services;

import org.apache.tapestry5.services.Session;

import ish.oncourse.model.College;

public interface ICollegeRequestService {
	College getRequestingCollege();
	Session getCollegeSession(boolean create);
}
