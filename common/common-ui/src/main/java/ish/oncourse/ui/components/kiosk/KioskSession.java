package ish.oncourse.ui.components.kiosk;

import ish.oncourse.model.*;
import ish.oncourse.services.tutor.ITutorService;
import ish.oncourse.ui.utils.GetVisibleTutors;
import ish.oncourse.util.FormatUtils;
import ish.oncourse.util.tapestry.TapestryFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.Date;
import java.util.List;

import static java.lang.String.format;

public class KioskSession {

    private String ISO8601 = "yyyy-MM-dd'T'HH:mm:ssZZ";

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
    private String author;

    @Property
    private String time;

    @Property
    private Date currentDate;

    @Property
    private String commencedClass;

    @Property
    private TapestryFormatUtils formatUtils = new TapestryFormatUtils();

    @Property
    private String isoSessionStartTime;

    @Property
    private Site site;

    @Property
    private String firstTutorName;

    @Property
    private List<Tutor> visibleTutors;

    @Property
    private Tutor tutor;

    @SetupRender
    public void setupRender() {
        courseClass = session.getCourseClass();
        course = courseClass.getCourse();

        currentDate = new Date();

        time = FormatUtils.getSessionTimeAsString(session);

        commencedClass = StringUtils.EMPTY;
        if (session.getStartDate().before(currentDate)) {
            time = format("commenced %s", time);
            commencedClass = "course-commenced";
        }

        GetVisibleTutors getVisibleTutors = GetVisibleTutors.valueOf(courseClass, tutorService).get();
        visibleTutors = getVisibleTutors.getTutors();
        author = StringUtils.join(getVisibleTutors.tutorNames(), ", ");


        isoSessionStartTime = formatUtils.formatDate(session.getStartDatetime(), ISO8601);
        site = session.getRoom().getSite();

        firstTutorName = StringUtils.EMPTY;
        if (!getVisibleTutors.getTutors().isEmpty()) {
            firstTutorName = GetVisibleTutors.getTutorName(getVisibleTutors.getTutors().get(0));
        }
    }
}


