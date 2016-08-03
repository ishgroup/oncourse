/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.courseclass;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Site;

/**
 * User: akoiro
 * Date: 3/08/2016
 */
public class GetCourseClassLocation {
	private CourseClass courseClass;
	private Location location;

	public GetCourseClassLocation(CourseClass courseClass) {
		this.courseClass = courseClass;
	}

	public Location get() {
		if (hasSite()) {
			Site site = courseClass.getRoom().getSite();

			if (site.getLatitude() != null && site.getLongitude() != null) {
				location = new Location(site.getLatitude().doubleValue(), site.getLongitude().doubleValue());
			}
		}
		return location;
	}

	private boolean hasSite() {
		return courseClass.getRoom() != null &&
				courseClass.getRoom().getSite() != null &&
				courseClass.getRoom().getSite().getIsWebVisible();
	}
}
