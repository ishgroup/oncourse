package ish.oncourse.ui.components.kiosk;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Session;
import ish.oncourse.model.Site;
import ish.oncourse.services.tutor.ITutorService;
import ish.oncourse.ui.utils.GetVisibleTutors;
import ish.oncourse.util.tapestry.TapestryFormatUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class KioskSession {

    @Inject
    private ITutorService tutorService;

    @Property
    private TapestryFormatUtils formatUtils = new TapestryFormatUtils();

    @Parameter
    @Property
    private Session session;

    @Property
    private CourseClass courseClass;

    @Property
    private String name;

    @Property
    private String code;

    @Property
    private Site site;

    @Property
    private String firstTutorName;

    @Property
    private String isoSessionStartTime;

    @Property
    private String ISO8601 = "yyyy-MM-dd'T'HH:mm:ssZZ";

    @SetupRender
    public void setupRender() {
        courseClass = session.getCourseClass();

        name = courseClass.getCourse().getName();
        code = courseClass.getUniqueIdentifier();

        GetVisibleTutors getVisibleTutors = GetVisibleTutors.valueOf(courseClass, session, tutorService).get();
        if (!getVisibleTutors.getTutors().isEmpty()) {
            firstTutorName = GetVisibleTutors.getTutorName(getVisibleTutors.getTutors().get(0));
        }
        site = session.getRoom().getSite();
        isoSessionStartTime = formatUtils.formatDate(session.getStartDatetime(), ISO8601);
    }
}