package ish.oncourse.portal.components.courseclass;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Session;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.services.courseclass.ICourseClassService;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.util.TextStreamResponse;

import java.util.Arrays;
import java.util.Date;
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

    @Persist
    @Property
    private Session nSession;

    @Persist
    @Property
    private List<Session> sessions;

    @Inject
    private IPortalService portalService;

    @SetupRender
    boolean setupRender() {
        sessions = searchSessionBy(courseClass);
        nSession = getNearestSession();
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



    private Session getNearestSession()
    {
       if (sessions.size() == 1)
                return sessions.get(0);

            Date date = new Date();
            Expression expression = ExpressionFactory.greaterOrEqualExp(Session.START_DATE_PROPERTY, date);
            List<Session> list = expression.filterObjects(sessions);
            if (!list.isEmpty())
            {
                Ordering.orderList(list, Arrays.asList(new Ordering(Session.START_DATE_PROPERTY, SortOrder.ASCENDING)));
                nSession = list.get(0);
            }
            else
            {
                expression = ExpressionFactory.lessOrEqualExp(Session.START_DATE_PROPERTY, date);
                list = expression.filterObjects(sessions);
                if (!list.isEmpty())
                {
                    Ordering.orderList(list, Arrays.asList(new Ordering(Session.START_DATE_PROPERTY, SortOrder.DESCENDING)));
                    nSession = list.get(0);
                }
            }

        return nSession;
    }

    @OnEvent(value = "getNearesIndex")
    public StreamResponse getNearesIndex(){

        for(int i=0;i<sessions.size(); i++){

            if(sessions.get(i).getId().equals(nSession.getId()))
                return new TextStreamResponse("text/json", portalService.getNearesSessionIndex(i).toString());

        }

        return null;
    }





}
