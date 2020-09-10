/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.components.courseclass;

import ish.oncourse.model.Session;
import ish.oncourse.portal.services.attendance.AttendanceUtils;
import ish.oncourse.portal.services.attendance.SessionStyle;
import ish.oncourse.services.IRichtextConverter;
import ish.oncourse.util.ValidationErrors;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Id;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.TimeZone;

public class SessionDetails {

	@Property
	@Parameter
	private Session session;

	@Property
	private String markedClass;
	
	private TimeZone timeZone;

	@Inject
	private IRichtextConverter textileConverter;

	@Inject
	@Id("empty")
	private Block emptyBlock;

	@Inject
	@Id("unmarked")
	private Block unmarkedBlock;

	@Inject
	@Id("future")
	private Block futureBlock;

	@Inject
	@Id("marked")
	private Block markedBlock;

	@Property
	private Block visibileBlock;

	private static final String PAST_CLASS = "past-roll-desc";
	private static final String ACTUAL_CLASS = "actual-roll-desc";


	@SetupRender
	boolean setupRender() {
		timeZone = session.getCourseClass().getClassTimeZone();

		SessionStyle style = SessionStyle.valueOf(session);
		switch (style)
		{
			case empty:
				visibileBlock = emptyBlock;
				markedClass = ACTUAL_CLASS;
				break;
			case unmarked:
				visibileBlock = unmarkedBlock;
				markedClass = ACTUAL_CLASS;
				break;
			case future:
				visibileBlock = futureBlock;
				markedClass = ACTUAL_CLASS;
				break;
			case marked:
				visibileBlock = markedBlock;
				markedClass = PAST_CLASS;
				break;
			default:
				throw new IllegalArgumentException();
		}
		return true;
	}

	public String getStartDate() {
		return AttendanceUtils.getStartDate(timeZone, session);
	}

	public String getSessionDate() {
		return AttendanceUtils.getSessionDateTime(timeZone, session);
	}
	
	public String convertTextile(String note) {
		String detail = textileConverter.convertCustomText(note, new ValidationErrors());
		return detail == null ? StringUtils.EMPTY : detail;
	}
}
