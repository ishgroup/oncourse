package ish.oncourse.portal.pages;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.CourseClass;
import ish.oncourse.portal.access.IAuthenticationService;

import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.courseclass.CourseClassFilter;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.util.FormatUtils;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;
import java.util.TimeZone;

public class Resources {

    @Inject
    private IAuthenticationService authenticationService;

    @Inject
    private ICourseClassService courseClassService;

    @Property
    private CourseClass courseClass;

    @Property
    private List<CourseClass> courseClasses;

    @Inject
    private IBinaryDataService binaryDataService;


    @SetupRender
    void  setupRender(){

        courseClasses = courseClassService.getContactCourseClasses(authenticationService.getUser(), CourseClassFilter.CURRENT);
    }


    public String getDate(CourseClass courseClass)
    {
         TimeZone timeZone = FormatUtils.getClientTimeZone(courseClass);

        return String.format("%s - %s",
                FormatUtils.getDateFormat("dd MMMMMM yyyy", timeZone).format(courseClass.getStartDate()),
                FormatUtils.getDateFormat("dd MMMMMM yyyy", timeZone).format(courseClass.getEndDate()));
    }


        public boolean hasResources(CourseClass courseClass){

        List<BinaryInfo> materials = binaryDataService.getAttachedFiles(courseClass.getId(), CourseClass.class.getSimpleName(), false);
        return !materials.isEmpty();
    }


}
