package ish.oncourse.portal.services;

import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Session;
import ish.oncourse.services.courseclass.CourseClassFilter;
import org.apache.tapestry5.json.JSONObject;

import java.util.List;

/**
 * User: artem
 * Date: 10/25/13
 * Time: 2:53 PM
 */
public interface IPortalService {

    public JSONObject getSession(Session session);
    public JSONObject getAttendences(Session session);
    public JSONObject getCalendarEvents(Contact contact);
    public JSONObject getNearesSessionIndex(Integer i);

    public boolean isApproved(Contact tutor, CourseClass courseClass);

    public boolean isHistoryEnabled();

    boolean isHasResources(List<CourseClass> courseClasses);

    boolean isHasResult();

    public List<CourseClass> getContactCourseClasses(Contact contact, CourseClassFilter filter);
}
