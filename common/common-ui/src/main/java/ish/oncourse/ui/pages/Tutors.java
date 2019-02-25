package ish.oncourse.ui.pages;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Document;
import ish.oncourse.model.Tutor;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.courseclass.GetActiveTutorClasses;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.util.HTMLUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
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

    @SetupRender
    public void setupRender() {
        tutors = getTutorsByOffset(cayenneService.newContext(), 0, firstElementsOnPageCount);
    }

    public String getTutorImage(Tutor tutor) {
        List<Document> images = binaryDataService.getImages(tutor);
        if (images.size() > 0) {
            return binaryDataService.getUrl(images.get(0));
        }
        return "https://tecnoaccesible.net/sites/default/files/styles/medium/public/Sin_imagen-en-01.png?itok=9vZY5uOu";
    }

    public String getMessage() {
        return "TUTORS TEST PAGE";
    }

    @OnEvent(value = "getTutors")
    public String getTutors(int offset, int pageSize) {
        List<Tutor> list = getTutorsByOffset(cayenneService.newContext(), offset, pageSize);
        if (list.size() > 0) {
            StringBuilder response = new StringBuilder();
            //response.
        }
        return null;
    }

    private List<Tutor> getTutorsByOffset(ObjectContext context, int offset, int pageSize) {
        return ObjectSelect.query(Tutor.class)
                .where(Tutor.COLLEGE.eq(webSiteService.getCurrentCollege()))
                .offset(0)
                .pageSize(firstElementsOnPageCount)
                .select(context);
    }

    private String classesToJson(List<CourseClass> courseClasses) {
        StringBuilder response = new StringBuilder();
        response.append("[");
        courseClasses.forEach(c -> {
           // response.append();
        });
        return null;
    }

    private String tutorToJson(Tutor tutor) {
        if (tutor != null) {
            List<CourseClass> tutorClasses = GetActiveTutorClasses.valueOf(cayenneService.newContext(), tutor).get();
            StringBuilder buffer = null;
        }
        return null;
    }

    private String classToJson(CourseClass courseClass) {
        return String.format("{\"className\":\"%s\",\"classUrl\":\"%s\"}",
                courseClass.getCourse().getName(), HTMLUtils.getCanonicalLinkPathForCourseClass(request, courseClass));
    }
}
