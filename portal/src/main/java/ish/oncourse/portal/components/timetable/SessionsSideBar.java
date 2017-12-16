/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.components.timetable;

import ish.oncourse.model.Attendance;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Session;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.attendance.ContactUtils;
import ish.oncourse.portal.services.attendance.SessionUtils;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.utils.DateUtils;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SessionsSideBar {

	@Parameter
	private int pageSize;

	@Parameter
	private int offset;

	@Parameter
	@Property
	private Contact contact;

	@Property
	protected List<Contact> children;
	
	@Parameter
	@Property
	private boolean showTeam;

	@Property
	private Map<String, Map<Date, List<Session>>> monthDaySessions;

	@Property
	private Map.Entry<String, Map<Date, List<Session>>> monthDayEntry;
	
	@Property
	private Map<Date, List<Session>> daySessions;

	@Property
	private Map.Entry<Date,List<Session>> dayEntry;

	@Parameter
	@Property
	private Date month;
	
	@Inject
	private ICayenneService cayenneService;

	@Inject
	@Property
	private IPortalService portalService;

	
	@SetupRender
	void setupRender() {

		Date fromDate;

		if (DateUtils.isCurrentMonth(month)) {
			fromDate = DateUtils.startOfDay(new Date());
		} else {
			fromDate = DateUtils.startOfMonth(month);
		}

		ObjectSelect<Session> query = ObjectSelect.query(Session.class).where(Session.START_DATE.gte(fromDate)).and(Session.COURSE_CLASS.dot(CourseClass.CANCELLED).isFalse());
		
		if (showTeam) {
			children = new ArrayList<>(portalService.getChildContacts());
			children.remove(portalService.getContact());
			query = query.and(Session.ATTENDANCES.dot(Attendance.STUDENT).in(ContactUtils.getStudentsBy(children)));
			query = query.offset(offset).limit(pageSize);

			monthDaySessions = SessionUtils.groupByMonthDay(query
					.orderBy(Session.START_DATE.asc())
					.prefetch(Session.COURSE_CLASS.disjoint())
					.select(cayenneService.newContext()));
		} else {
			monthDaySessions = SessionUtils.groupByMonthDay(portalService.getContactSessionsFrom(fromDate, contact));
		}
		
		
	}

	public boolean isCurrentMonth(String month) {
		return new SimpleDateFormat("MMMM yyyy").format(new Date()).equals(month);
	}
	
	public boolean isEmptyCurrentMonth() {
		return offset == 0 && monthDaySessions.get(new SimpleDateFormat("MMMM yyyy").format(month)) == null;
	}

}
