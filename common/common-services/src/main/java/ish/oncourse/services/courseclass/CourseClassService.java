package ish.oncourse.services.courseclass;

import ish.common.types.EnrolmentStatus;
import ish.oncourse.model.*;
import ish.oncourse.services.cache.CacheGroup;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.util.CommonUtils;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.*;

@SuppressWarnings("unchecked")
public class CourseClassService implements ICourseClassService {

    private static final Logger logger = Logger.getLogger(CourseClassService.class);

    private final ICayenneService cayenneService;

    private final IWebSiteService webSiteService;

	private final ICookiesService cookiesService;
    
    @Inject
	public CourseClassService(ICayenneService cayenneService, IWebSiteService webSiteService, ICookiesService cookiesService) {
		this.cayenneService = cayenneService;
		this.webSiteService = webSiteService;
		this.cookiesService = cookiesService;
	}

	public CourseClass getCourseClassByFullCode(String code) {
        String[] parts = code.split("-");
        // courseClass code has format "course.code-courseClass.code"
        if (parts.length < 2) {
            return null;
        }
        String courseCode = parts[0];
        String courseClassCode = parts[1];
        SelectQuery query = new SelectQuery(CourseClass.class, getSiteQualifier().andExp(
                getSearchStringPropertyQualifier(CourseClass.COURSE_PROPERTY + "." + Course.CODE_PROPERTY, courseCode)).andExp(
                getSearchStringPropertyQualifier(CourseClass.CODE_PROPERTY, courseClassCode)));

        appyCourseClassCacheSettings(query);

        List<CourseClass> result = cayenneService.sharedContext().performQuery(query);
        return !result.isEmpty() ? result.get(0) : null;
    }

    /**
     * @return
     */
    private Expression getSiteQualifier() {
        if (CommonUtils.compare(webSiteService.getCurrentCollege().getAngelVersion(), "5.1A0") < 0)
            //code CommonUtils.compare.... should be deleted when all colleges are moved to angel 5.1 or higher
            return ExpressionFactory.matchExp(Course.COLLEGE_PROPERTY, webSiteService.getCurrentCollege()).andExp(
                    ExpressionFactory.matchExp(CourseClass.IS_WEB_VISIBLE_PROPERTY, true));
        else
            return ExpressionFactory.matchExp(Course.COLLEGE_PROPERTY, webSiteService.getCurrentCollege()).andExp(
                    ExpressionFactory.matchExp(CourseClass.IS_ACTIVE_PROPERTY, true));

    }

    public Expression getSearchStringPropertyQualifier(String searchProperty, Object value) {
        return ExpressionFactory.likeIgnoreCaseExp(searchProperty, value);
    }

	public List<CourseClass> loadByIds(Object... ids) {
		return loadByIds(cayenneService.sharedContext(), ids);
	}

	public List<CourseClass> loadByIds(ObjectContext objectContext,Object... ids) {

        if (ids.length == 0) {
            return Collections.emptyList();
        }

        List<Object> params = Arrays.asList(ids);

        SelectQuery q = new SelectQuery(CourseClass.class, ExpressionFactory.inDbExp(CourseClass.ID_PK_COLUMN, params));

        appyCourseClassCacheSettings(q);

        return objectContext.performQuery(q);
    }

    public List<CourseClass> loadByIds(List<Long> ids) {
        if ((ids == null) || (ids.isEmpty())) {
            return Collections.emptyList();
        }
        SelectQuery q = new SelectQuery(CourseClass.class, ExpressionFactory.inDbExp(CourseClass.ID_PK_COLUMN, ids).andExp(
                getSiteQualifier()));

        appyCourseClassCacheSettings(q);
        return cayenneService.sharedContext().performQuery(q);
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * ish.oncourse.services.courseclass.ICourseClassService#loadByAngelId(java
      * .lang.Long)
      */
    @Override
    public CourseClass loadByAngelId(Long angelId) {

        SelectQuery q = new SelectQuery(CourseClass.class);
        q.andQualifier(ExpressionFactory.matchExp(CourseClass.ANGEL_ID_PROPERTY, angelId));
        q.andQualifier(ExpressionFactory.matchExp(CourseClass.COLLEGE_PROPERTY, webSiteService.getCurrentCollege()));

        appyCourseClassCacheSettings(q);
        return (CourseClass) Cayenne.objectForQuery(cayenneService.sharedContext(), q);
    }

    /**
     * @see ICourseClassService#getContactSessionsForMonth(Contact, Date)
     */
    @Override
    public List<Session> getContactSessionsForMonth(Contact contact, Date month) {

        /*
           * get session for timetable for month
           * for timetable we should only show FUTURE sessions from ACTIVE classes
           */
        Calendar nextMonth = Calendar.getInstance();
        nextMonth.setTime(month);
        nextMonth.add(Calendar.MONTH, 1);

        // get only session for current month
        Expression intervalExpr = ExpressionFactory.betweenExp(Session.START_DATE_PROPERTY, month, nextMonth.getTime());

        return getContactSessionsWithAddingExpression(contact, intervalExpr);
    }

    /**
     * @see ICourseClassService#getContactSessions(Contact)
     */
    @Override
    public List<Session> getContactSessions(Contact contact) {
        return getContactSessionsWithAddingExpression(contact, null);
    }

    private List<Session> getContactSessionsWithAddingExpression(Contact contact, Expression addingExpresion) {

        /*
           * get session for timetable
           * for timetable we should only show future sessions from active classes
           */
        List<Session> sessions = new ArrayList<>(30);


        if (contact.getStudent() == null && contact.getTutor() == null) {
            logger.warn(String.format("Contact with ID:%s is neither Student nor Tutor.", contact.getId()));
            return Collections.emptyList();
        }

        // expression: get only future session
        Expression startingExp = ExpressionFactory.greaterOrEqualExp(Session.START_DATE_PROPERTY, new Date());

        // expression: get only sessions from ACTIVE classes (not canceled)
        Expression activeClassesExp = ExpressionFactory.noMatchExp(Session.COURSE_CLASS_PROPERTY + "." + CourseClass.CANCELLED_PROPERTY, Boolean.TRUE);

        if (contact.getTutor() != null) {
            Tutor tutor = contact.getTutor();
            Expression expr = ExpressionFactory.matchExp(Session.SESSION_TUTORS_PROPERTY + "." + SessionTutor.TUTOR_PROPERTY, tutor);
            if (addingExpresion != null) {
                expr = addingExpresion.andExp(expr);
            }
            SelectQuery q = new SelectQuery(Session.class, expr.andExp(startingExp).andExp(activeClassesExp));

            sessions.addAll(cayenneService.sharedContext().performQuery(q));
        }

        if (contact.getStudent() != null) {
            Student student = contact.getStudent();
            Expression expr = ExpressionFactory.matchExp(Session.COURSE_CLASS_PROPERTY + "." + CourseClass.ENROLMENTS_PROPERTY + "."
                    + Enrolment.STATUS_PROPERTY, EnrolmentStatus.SUCCESS);
            expr = expr.andExp(ExpressionFactory.matchExp(Session.COURSE_CLASS_PROPERTY + "." + CourseClass.ENROLMENTS_PROPERTY + "."
                    + Enrolment.STUDENT_PROPERTY, student));

            if (addingExpresion != null) {
                expr = addingExpresion.andExp(expr);
            }
            SelectQuery q = new SelectQuery(Session.class, expr.andExp(startingExp).andExp(activeClassesExp));

            sessions.addAll(cayenneService.sharedContext().performQuery(q));
        }


        new Ordering(Session.START_DATE_PROPERTY, SortOrder.ASCENDING).orderList(sessions);

        return sessions;
    }


    public List<CourseClass> getContactCourseClasses(Contact contact, CourseClassFilter filter) {

        /*
           * get classes for classes item
           * for classes item we should show ALL ACTIVE classes  (not canceled)
           */
        if (contact.getStudent() == null && contact.getTutor() == null) {
            logger.warn(String.format("Contact with ID:%s is neither Student nor Tutor.", contact.getId()));
            return Collections.emptyList();
        }

        List<CourseClass> courses = new ArrayList<>();
        Ordering ordering = getOrderingBy(filter);
        if (contact.getTutor() != null) {
            courses.addAll(getCourseClassesBy(contact,filter,true));
        }

        if (contact.getStudent() != null) {
            List<CourseClass> sClasses = getCourseClassesBy(contact, filter, false);
            courses = ListUtils.sum(courses, sClasses);

        }
        ordering.orderList(courses);

        return courses;
    }

    Ordering getOrderingBy(CourseClassFilter filter) {
        switch (filter) {
        	case UNCONFIRMED:
            case CURRENT:
                return new Ordering(CourseClass.START_DATE_PROPERTY, SortOrder.ASCENDING);
            case PAST:
            case ALL:
                return new Ordering(CourseClass.START_DATE_PROPERTY, SortOrder.DESCENDING);
            default:
                throw new IllegalArgumentException();
        }
    }

    private  List<CourseClass> getCourseClassesBy(Contact contact, CourseClassFilter filter, boolean forTutor)
    {
        Expression expr = getExpressionBy(contact, filter, forTutor);
        SelectQuery q = new SelectQuery(CourseClass.class, expr);
        appyCourseClassCacheSettings(q);

        return cayenneService.sharedContext().performQuery(q);
    }

    private Expression getExpressionBy(Contact contact, CourseClassFilter filter, boolean forTutor) {
        Expression expr;
        if (forTutor)
        {
            expr = ExpressionFactory.matchExp(CourseClass.TUTOR_ROLES_PROPERTY + "." + TutorRole.TUTOR_PROPERTY, contact.getTutor());
        }
        else
        {
            expr = ExpressionFactory.matchExp(CourseClass.ENROLMENTS_PROPERTY + "." + Enrolment.STUDENT_PROPERTY, contact.getStudent());
            //for student only where enrolment has status SUCCESS.
            expr = expr.andExp(ExpressionFactory.matchExp(CourseClass.ENROLMENTS_PROPERTY + "." + Enrolment.STATUS_PROPERTY, EnrolmentStatus.SUCCESS));
        }
        Expression activeClassesExp = ExpressionFactory.noMatchExp(CourseClass.CANCELLED_PROPERTY, Boolean.TRUE);
        expr = expr.andExp(activeClassesExp);

        Date today = new Date(System.currentTimeMillis());

        switch (filter) {
        	case UNCONFIRMED:
        		return expr.andExp(ExpressionFactory.greaterOrEqualExp(CourseClass.END_DATE_PROPERTY, today))
        				.andExp(ExpressionFactory.matchExp(CourseClass.TUTOR_ROLES_PROPERTY + "." + TutorRole.IS_CONFIRMED_PROPERTY, false));
            case CURRENT:
            	if (forTutor) {
            		return expr.andExp(ExpressionFactory.greaterOrEqualExp(CourseClass.END_DATE_PROPERTY, today))
                		.andExp(ExpressionFactory.matchExp(CourseClass.TUTOR_ROLES_PROPERTY + "." + TutorRole.IS_CONFIRMED_PROPERTY, true));
                } else {
                	return expr.andExp(ExpressionFactory.greaterOrEqualExp(CourseClass.END_DATE_PROPERTY, today));
                }
            case PAST:
                return expr.andExp(ExpressionFactory.lessExp(CourseClass.END_DATE_PROPERTY, today));
            case ALL:
                return expr;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public Attendance loadAttendanceById(Object id) {

        SelectQuery q = new SelectQuery(Attendance.class);
        q.andQualifier(ExpressionFactory.matchExp(Attendance.ANGEL_ID_PROPERTY, id));
        q.andQualifier(ExpressionFactory.matchExp(CourseClass.COLLEGE_PROPERTY, webSiteService.getCurrentCollege()));

        return (Attendance) Cayenne.objectForQuery(cayenneService.sharedContext(), q);
    }

    /**
     * Add necessary prefetches and assign cache group for course query;
     *
     * @param q course query
     */
    private static void appyCourseClassCacheSettings(SelectQuery q) {

        q.setCacheStrategy(QueryCacheStrategy.LOCAL_CACHE);
        q.setCacheGroups(CacheGroup.COURSES.name());

        q.addPrefetch(CourseClass.ROOM_PROPERTY);
        q.addPrefetch(CourseClass.SESSIONS_PROPERTY);
        q.addPrefetch(CourseClass.TUTOR_ROLES_PROPERTY);
        q.addPrefetch(CourseClass.DISCOUNT_COURSE_CLASSES_PROPERTY);
        q.addPrefetch(CourseClass.DISCUSSIONS_PROPERTY);
    }


    @Override
    public List<CourseClass> getContactCourseClasses(Contact contact) {
        return getContactCourseClasses(contact, CourseClassFilter.ALL);
    }


	public List<Survey> getSurveysFor(Tutor tutor)
	{
		Expression expr = ExpressionFactory.matchExp(StringUtils.join(new String[]{Survey.ENROLMENT_PROPERTY, Enrolment.COURSE_CLASS_PROPERTY, CourseClass.TUTOR_ROLES_PROPERTY, TutorRole.TUTOR_PROPERTY}, "."),tutor);
		SelectQuery q = new SelectQuery(Survey.class, expr);
		return cayenneService.sharedContext().performQuery(q);
	}

	public List<Survey> getSurveysFor(CourseClass courseClass) {
		Expression surveyExp = ExpressionFactory.matchExp(Survey.ENROLMENT_PROPERTY + "." + Enrolment.COURSE_CLASS_PROPERTY, courseClass);
		SelectQuery query = new SelectQuery(Survey.class, surveyExp);

		return cayenneService.sharedContext().performQuery(query);
	}

	@Override
	public TimeZone getClientTimeZone(CourseClass courseClass) {
		TimeZone timezone = TimeZone.getTimeZone(courseClass.getTimeZone());

		if (timezone == null && cookiesService != null) {
			timezone = cookiesService.getClientTimezone();
			if (timezone == null) {
				timezone = cookiesService.getSimpleClientTimezone();
			}
		}
		return timezone;
	}
}
