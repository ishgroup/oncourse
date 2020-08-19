package ish.oncourse.ui.pages;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Tutor;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.ui.base.ISHCommon;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.BlockNotFoundException;
import org.apache.tapestry5.annotations.Id;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ish.oncourse.model.auto._Course.IS_WEB_VISIBLE;

public class Tutors extends ISHCommon {

    @Inject
    private ICayenneService cayenneService;

    @Inject
    private IWebSiteService webSiteService;

    @Property
    private List<Tutor> tutors;

    @Property
    private Tutor tutor;
    
    @Property
    private Boolean hasMore;

    private static final int OFFSET_DEFAULT = 0;
    private static final int PAGE_SIZE = 10;

    private int start;
    
    @SetupRender
    void beginRender() {
        start = getIntParam(request.getParameter("start"), OFFSET_DEFAULT);

        tutors = ObjectSelect.query(Tutor.class)
                .where(Tutor.COLLEGE.eq(webSiteService.getCurrentCollege()))
                .and(Tutor.FINISH_DATE.isNull().orExp(Tutor.FINISH_DATE.lt(new Date())))
                .orderBy(Tutor.CONTACT.dot(Contact.FAMILY_NAME).asc(), Tutor.CONTACT.dot(Contact.GIVEN_NAME).asc())
                .offset(start)
                .limit(PAGE_SIZE)
                .select(cayenneService.sharedContext());
        int count = ObjectSelect.query(Tutor.class).count()
                .where(Course.COLLEGE.eq(webSiteService.getCurrentCollege()))
                .cacheStrategy(QueryCacheStrategy.LOCAL_CACHE, Tutor.class.getSimpleName())
                .selectOne(cayenneService.sharedContext()).intValue();
        
        start += PAGE_SIZE;

        hasMore = (start) < count;
    }

    public String getSearchParamsStr() {
        return "start=" + start;
    }

}
