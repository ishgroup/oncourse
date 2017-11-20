package ish.oncourse.ui.components.kiosk;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Session;
import ish.oncourse.services.tutor.ITutorService;
import ish.oncourse.ui.utils.GetVisibleTutors;
import ish.oncourse.util.FormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.Date;

import static java.lang.String.format;

public class KioskSession {

    @Inject
    private ITutorService tutorService;

    @Parameter
    @Property
    private Session session;

    @Property
    private CourseClass courseClass;

    @Property
    private Course course;

    @Property
    private String name;

    @Property
    private String code;

    @Property
    private String author;

    @Property
    private String time;

    @Property
    private String location;

    @Property
    private Date start;

    @Property
    private Date end;

    @Property
    private Date currentDate;

    @Property
    private String commencedClass;


    @SetupRender
    public void setupRender() {
        courseClass = session.getCourseClass();
        course = courseClass.getCourse();

        name = courseClass.getCourse().getName();
        code = courseClass.getUniqueIdentifier();

        currentDate = new Date();
        start = session.getStartDate();
        end = session.getEndDate();


        time = FormatUtils.getSessionTimeAsString(session);

        commencedClass = StringUtils.EMPTY;
        if (start.before(currentDate)) {
            time = format("commenced %s", time);
            commencedClass = "course-commenced";
        }

        location = session.getRoom().getName();

        GetVisibleTutors getVisibleTutors = GetVisibleTutors.valueOf(courseClass, session, tutorService).get();
        author = StringUtils.join(getVisibleTutors.tutorNames(), ", ");
    }
}


