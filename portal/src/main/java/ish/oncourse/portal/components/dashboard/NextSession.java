/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.components.dashboard;

import ish.oncourse.model.Session;
import ish.oncourse.portal.services.attendance.AttendanceUtils;
import ish.oncourse.portal.services.attendance.SessionUtils;
import ish.oncourse.services.courseclass.GetCourseClassLocation;
import ish.oncourse.services.courseclass.Location;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

import java.util.TimeZone;

public class NextSession {
	
	private static final String MAP_URL_PATTERN = "https://maps.googleapis.com/maps/api/streetview?size=200x300&location=%f,%f&key=AIzaSyBAmT4qKhRR0cMHVqj70vQWDZU0-3ppJkg";
	
	@Parameter
	@Property
	private Session session;

	@Property
	private Location location;
	
	@SetupRender
	public void setupRender() {
		location = new GetCourseClassLocation(session).get();
	}
	
	public String getSessionStartDate() {
		return AttendanceUtils.getStartDate(TimeZone.getTimeZone(session.getTimeZone()), session, "EEEE, d MMMM yyyy");
	}
	
	public String getSessionDay() {
		return AttendanceUtils.getStartDate(TimeZone.getTimeZone(session.getTimeZone()), session, "EEE");
	}
	
	public String getSessionDate() {
		return AttendanceUtils.getStartDate(TimeZone.getTimeZone(session.getTimeZone()), session, "d");
	}
	public String getSessionTime() {
		return AttendanceUtils.getSessionTime(TimeZone.getTimeZone(session.getTimeZone()), session);
	}

	public String getVenue() {
		return SessionUtils.getVenue(session);
	}
	
	public String getMapUrl() {
		return location != null ? String.format(MAP_URL_PATTERN,location.getLatitude(), location.getLongitude()) : "http://www.mindingthecampus.com/originals/campus.jpg";
	}

}
