/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.components.timetable;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Session;
import ish.oncourse.portal.services.IPortalService;

import ish.oncourse.portal.services.attendance.SessionUtils;

import ish.oncourse.services.IReachtextConverter;
import ish.oncourse.util.FormatUtils;
import ish.oncourse.util.ValidationErrors;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.Date;
import java.util.List;
import java.util.Map;

abstract public class AbstractSessionItem {
	@Parameter
	@Property
	private Map.Entry<Date, List<Session>> dayEntry;

	@Property
	private Session session;

	@Parameter
	private Contact contact;

	@Inject
	@Property
	private IPortalService portalService;

	@Inject
	private IReachtextConverter textileConverter;

	public String getVenue() {
		return SessionUtils.getVenue(session);
	}

	public String convertTextile(String note) {
		String detail = textileConverter.convertCustomText(note, new ValidationErrors());
		return detail == null ? StringUtils.EMPTY : detail;
	}

	public boolean isShowPrivateNotes() {
		return contact.getTutor() != null && session.getPublicNotes() != null && portalService.isTutorFor(session.getCourseClass());
	}

	public boolean isTutor() {
		return contact.getTutor() != null && portalService.isTutorFor(session.getCourseClass());
	}

	public String getClassUrl() {
		return portalService.getUrlBy(session.getCourseClass());
	}

	public String getStartDateTime() {
		return FormatUtils.getDateFormat("h.mma", session.getTimeZone()).format(session.getStartDate());
	}

	public String getEndDateTime() {
		return FormatUtils.getDateFormat("h.mma", session.getTimeZone()).format(session.getEndDate());
	}
	
	public String getEntryKey() {
		return FormatUtils.getDateFormat("MMMM yyyy dd").format(dayEntry.getKey());
	}
	
}
