package ish.oncourse.ui.pages;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Tutor;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.ui.base.ISHCommon;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

public class Tutors extends ISHCommon {

    @Inject
    private ICayenneService cayenneService;

    @Inject
    private IWebSiteService webSiteService;

    @Inject
    private IBinaryDataService binaryDataService;

    private Integer firstElementsOnPageCount = 2;

    @Property
    private List<Tutor> tutors;

    @Property
    private Tutor tutor;

    @Property
    private List<CourseClass> tutorCourseClasses;

    @Property
    private List<CourseClass> tutorClass;

    @Inject
    private ICourseClassService courseClassService;

    public String getMessage() {
        return "TUTORS TEST PAGE";
    }


}
