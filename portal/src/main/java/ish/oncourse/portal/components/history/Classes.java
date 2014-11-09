package ish.oncourse.portal.components.history;

import ish.oncourse.model.CourseClass;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.PortalUtils;
import ish.oncourse.services.courseclass.CourseClassFilter;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.util.FormatUtils;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * User: artem
 * Date: 11/4/13
 * Time: 9:28 AM
 */
public class Classes {


	@Inject
	private  ICourseClassService courseClassService;

    @Inject
    private IPortalService portalService;


    @Property
    private List<CourseClass> classes;

    @Property
    private CourseClass courseClass;

    @Inject
    private Messages messages;

    @SetupRender
    void setupRender(){
        classes = portalService.getContactCourseClasses(CourseClassFilter.ALL);

    }

    public String getDate()
    {
        TimeZone timeZone = courseClassService.getClientTimeZone(courseClass);
        /**
         * we added created == null condition to exlude NPE when some old enrolment has null value in create field.
         * If the field has null value we show "(not set)" string
         * TODO The condition should be deleted after 21309 will be closed
         */
        Date date = courseClass.getStartDate();
        if (date != null)
        {
            //Date should be in format 19 November 2014 10:00am
            return String.format("%s", FormatUtils.getDateFormat(PortalUtils.DATE_FORMAT_d_MMMM_yyyy_h_mma, timeZone).format(date));
        }
        else
        {
            return messages.get(PortalUtils.MESSAGE_KEY_notSet);
        }

    }


    public boolean isNew()
    {
        return portalService.isNew(courseClass);
    }


}
