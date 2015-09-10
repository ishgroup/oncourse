/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.services.application;

import ish.oncourse.model.Application;
import ish.oncourse.model.Student;

import java.util.List;

public interface IPortalApplicationService {

	/**
	 * Find students applications in  'new' status or applications in 'offered' status which enroledBy date in future or equals null  
	 * @param student
	 * @return list of applications
	 */
	List<Application> getActiveApplicationsBy(Student student);


	/**
	 * Find all students applications
	 * @param student
	 * @return list of applications
	 */
	List<Application> getAllApplicationsBy(Student student);
}
