package ish.oncourse.portal.components.courseclass;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Session;
import ish.oncourse.services.courseclass.ICourseClassService;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

/**
 * User: artem
 * Date: 10/16/13
 * Time: 11:06 AM
 */
public class SessionSlider {

    @Parameter
    @Property
    private CourseClass courseClass;


    @Property
    private Session session;

    @Property
    private List<Session> sessions;


    @SetupRender
    boolean setupRender() {
        sessions = searchSessionBy(courseClass);
        return true;
    }


    public int getNumberOfSessions(){
       return sessions.size();

    }



    /**
     *  The method searches Sessions for the  courseClass and sorts them by startDate field.
     *  The method has been introduced because we need sorting by startDate field, CourseClass.getSessions()
     */
    List<Session> searchSessionBy(CourseClass courseClass) {
        Expression exp = ExpressionFactory.matchDbExp(Session.COURSE_CLASS_PROPERTY, courseClass);
        SelectQuery selectQuery = new SelectQuery(Session.class, exp);
        selectQuery.addOrdering(Session.START_DATE_PROPERTY, SortOrder.ASCENDING);
        //the prefetch has been added to reload ATTENDANCES every time when we reload the page.
        selectQuery.addPrefetch(Session.ATTENDANCES_PROPERTY);
        return  (List<Session>) courseClass.getObjectContext().performQuery(selectQuery);
    }

}
