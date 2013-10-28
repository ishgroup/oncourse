package ish.oncourse.portal.services;

import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Session;
import org.apache.tapestry5.json.JSONObject;

/**
 * User: artem
 * Date: 10/25/13
 * Time: 2:53 PM
 */
public interface IPortalService {
    public JSONObject getAttendences(Session session);
    public JSONObject getCalendarEvents(Contact contact);

    public boolean isApproved(Contact tutor, CourseClass courseClass);
}
