package ish.oncourse.portal.components.timetable;

import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.services.courseclass.CourseClassFilter;
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
public class ClassSlider {

    @Parameter
    private Contact contact;


    @Property
    private CourseClass courseClass;

    @Property
    private List<CourseClass> courseClasses;


    @Inject
    private IPortalService portalService;

    @SetupRender
    boolean setupRender() {
        courseClasses = portalService.getContactCourseClasses(contact,CourseClassFilter.CURRENT);
        return true;
    }
}
