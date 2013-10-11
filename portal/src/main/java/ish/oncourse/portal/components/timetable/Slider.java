package ish.oncourse.portal.components.timetable;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Session;
import ish.oncourse.services.courseclass.ICourseClassService;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

/**
 * User: artem
 * Date: 10/11/13
 * Time: 3:10 PM
 */
public class Slider {

    @Parameter
    private Contact contact;


    @Property
    private Session session;

    @Property
    private List<Session> sessions;

    @Inject
    private ICourseClassService courseClassService;


    @SetupRender
    boolean setupRender() {
        sessions = courseClassService.getContactSessions(contact);
        return true;
    }
}
