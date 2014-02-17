package ish.oncourse.portal.services;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Session;
import ish.oncourse.services.courseclass.CourseClassFilter;
import org.apache.tapestry5.json.JSONObject;

import java.util.List;

public interface IPortalService {

    public JSONObject getSession(Session session);

    public JSONObject getAttendences(Session session);

    public JSONObject getCalendarEvents(Contact contact);

    public JSONObject getNearesSessionIndex(Integer i);

    public boolean isApproved(Contact tutor, CourseClass courseClass);

    public boolean isHistoryEnabled();

	/**
	 * return true if contact has valid enrolment on the courseClass
	 * @param contact
	 * @param courseClass
	 * @return
	 */
	boolean hasResult(Contact contact, CourseClass courseClass);

	public List<BinaryInfo> getCommonTutorsBinaryInfo();

	public List<BinaryInfo> getAttachedFiles(CourseClass courseClass, Contact contact);

    public List<CourseClass> getContactCourseClasses(Contact contact, CourseClassFilter filter);

	public List<PCourseClass> fillCourseClassSessions(Contact contact, CourseClassFilter filter) ;
}
