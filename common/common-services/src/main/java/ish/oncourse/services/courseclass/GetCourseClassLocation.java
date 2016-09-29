/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.courseclass;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Room;
import ish.oncourse.model.Session;
import ish.oncourse.model.Site;

/**
 * User: akoiro
 * Date: 3/08/2016
 */
public class GetCourseClassLocation {
	private CourseClass courseClass;
	private Session session;
	private Location location;

	public GetCourseClassLocation(CourseClass courseClass) {
		this.courseClass = courseClass;
	}
	
	public GetCourseClassLocation(Session session) {
		this.session = session;
	}

	public Location get() {
		if (hasSite()) {
			
			Site site = session != null ? session.getRoom().getSite() : courseClass.getRoom().getSite();

			location = new Location(site.getLatitude() != null ? site.getLatitude().doubleValue() : null, site.getLongitude() != null ? site.getLongitude().doubleValue() : null, 
						site.getState(), site.getStreet(), site.getSuburb());
		}
		return location;
	}

	private boolean hasSite() {
		Room room = session != null ? session.getRoom() : courseClass != null ? courseClass.getRoom() : null;
		return room != null &&
				room.getSite() != null &&
				room.getSite().getIsWebVisible();
		
	}
}
