package ish.oncourse.portal.components.history;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Student;
import ish.oncourse.portal.access.IAuthenticationService;

import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.util.FormatUtils;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * User: artem
 * Date: 11/4/13
 * Time: 9:28 AM
 */
public class Enrolments {

    @Inject
    private IAuthenticationService authenticationService;

    @Inject
    private ICourseClassService courseClassService;

    @Property
    private List<Enrolment> enrolments;

    @Property
    private Enrolment enrolment;

    @SetupRender
    void setupRender(){
        enrolments = authenticationService.getUser().getStudent().getEnrolments();

    }

    public String getDate(Enrolment enrolment)
    {
        TimeZone timeZone = FormatUtils.getClientTimeZone(enrolment.getCourseClass());
        return String.format("%s", FormatUtils.getDateFormat("d MMMMM h:mma", timeZone).format(enrolment.getCreated()));
    }


}
