package ish.oncourse.ui.components.kiosk;

import ish.oncourse.model.*;
import ish.oncourse.ui.utils.GetTutorName;
import ish.oncourse.ui.utils.GetVisibleTutors;
import ish.oncourse.util.FormatUtils;
import ish.oncourse.util.tapestry.TapestryFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class KioskSession {

    private String ISO8601 = "yyyy-MM-dd'T'HH:mm:ssZZ";

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

    /* deprecated properties should be deleted
    after migration all colleges to bootique web app
    (OD-11646 - adjusting KioskSession tapestry template
     OD-11645 - adjusting KioskSession controller)
     */

    @Deprecated
    @Property
    private String name;

    @Deprecated
    @Property
    private String code;

    @Deprecated
    @Property
    private Date start;

    @Deprecated
    @Property
    private Date end;

    @Deprecated
    @Property
    private String location;

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

        visibleTutors = GetVisibleTutors.valueOf(session).get();
        author = StringUtils.join(visibleTutors.stream()
                        .map(tutor -> GetTutorName.valueOf(tutor).get())
                        .collect(Collectors.toList()),
                ", ");


        isoSessionStartTime = formatUtils.formatDate(session.getStartDatetime(), ISO8601);
        site = session.getRoom().getSite();

        firstTutorName = StringUtils.EMPTY;
        if (!visibleTutors.isEmpty()) {
            firstTutorName = GetTutorName.valueOf(visibleTutors.get(0)).get();
        }

        name = courseClass.getCourse().getName();
        code = courseClass.getUniqueIdentifier();

        start = session.getStartDate();
        end = session.getEndDate();

        location = session.getRoom().getName();
    }
}


