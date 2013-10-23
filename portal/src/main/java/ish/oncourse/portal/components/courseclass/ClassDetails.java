package ish.oncourse.portal.components.courseclass;


import ish.oncourse.model.CourseClass;
import ish.oncourse.portal.access.IAuthenticationService;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * User: artem
 * Date: 10/16/13
 * Time: 10:00 AM
 */
public class ClassDetails {

    @Inject
    private IAuthenticationService authenticationService;

    @Property
    @Parameter
    private CourseClass courseClass;



    public boolean isTutor(){
        return authenticationService.isTutor();
    }


}
