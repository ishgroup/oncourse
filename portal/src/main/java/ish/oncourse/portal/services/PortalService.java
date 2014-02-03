package ish.oncourse.portal.services;

import ish.common.types.AttachmentInfoVisibility;
import ish.common.types.EnrolmentStatus;
import ish.common.types.OutcomeStatus;
import ish.oncourse.model.*;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.cache.CacheGroup;
import ish.oncourse.services.courseclass.CourseClassFilter;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.util.FormatUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.commons.lang.time.DateUtils;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

/**
 * User: artem
 * Date: 10/14/13
 * Time: 3:35 PM
 */
public class PortalService implements IPortalService{


    @Inject
    private ICourseClassService courseClassService;

    @Inject
    private ICayenneService cayenneService;

    @Inject
    private PreferenceController preferenceController;

	@Inject
	private IWebSiteService webSiteService;

    @Override
    public JSONObject getSession(Session session) {
        JSONObject result = new JSONObject();
        result.put("startDate",FormatUtils.getDateFormat("EEEE dd MMMMM h:mma", session.getTimeZone()).format(session.getStartDate()));
        result.put("endDate", FormatUtils.getDateFormat("EEEE dd MMMMM h:mma", session.getTimeZone()).format(session.getEndDate()));
        return result;
    }

    public JSONObject getAttendences(Session session) {
       List<Attendance> attendances = session.getAttendances();

       JSONObject result = new JSONObject();
       result.append("session", getSession(session));

        for(Attendance attendance : attendances)
        {
            result.put(String.format("%s",attendance.getStudent().getId()),String.format("%s",attendance.getAttendanceType()));
        }

        return result;
    }

	public JSONObject getNearesSessionIndex(Integer i) {

        JSONObject result = new JSONObject();
        result.append("nearesIndex", i);

        return result;
	}

    /**
     * @return contact's sesssions array where entry format is MM-dd-yyyy,<a href='#class-%s'>%s</a>
     * we use it to show sessions callender for contact in Tempalte page
     */
    public JSONObject getCalendarEvents(Contact contact) {
        List<Session> sessions = courseClassService.getContactSessions(contact);

        JSONObject result = new JSONObject();

		Map<String, List<Session>> daysSessionMap = new HashMap<>();

        for (Session session : sessions) {
            TimeZone timeZone = courseClassService.getClientTimeZone(session.getCourseClass());
			String key = FormatUtils.getDateFormat("MM-dd-yyyy",timeZone).format(session.getStartDate());

			if (daysSessionMap.get(key) == null) {
				daysSessionMap.put(key, new ArrayList<Session>());
			}

			daysSessionMap.get(key).add(session);
		}

		for (String key : daysSessionMap.keySet()) {
			String events =	builEventsString(daysSessionMap.get(key));
			result.put(key, events);
		}

        return result;
    }

	private String builEventsString(List<Session> sessions) {

		StringBuilder events = new StringBuilder();

		for (Session session : sessions) {
			events.append(String.format("<li><a href='#class-%s' class=\"event\">%s</a></li>", session.getCourseClass().getId(), formatDate(session)));
		}

		return String.format("<ul>%s</ul>",events);
	}


	private String formatDate(Session session) {
        TimeZone timeZone = courseClassService.getClientTimeZone(session.getCourseClass());
        return String.format("%s - %s",
                FormatUtils.getDateFormat(FormatUtils.shortTimeFormatString, timeZone).format(session.getStartDate()),
                FormatUtils.getDateFormat(FormatUtils.timeFormatWithTimeZoneString, timeZone).format(session.getEndDate()));
    }

    public boolean isApproved(Contact contact, CourseClass courseClass) {

		Expression exp = ExpressionFactory.matchExp(TutorRole.TUTOR_PROPERTY,contact.getTutor()).andExp(ExpressionFactory.matchExp(TutorRole.COURSE_CLASS_PROPERTY, courseClass));
		SelectQuery q = new SelectQuery(TutorRole.class, exp);

		List <TutorRole> tutorRoles = cayenneService.sharedContext().performQuery(q);

        for (TutorRole t : tutorRoles) {
			if (!t.getIsConfirmed()) {
				return false;
			}
        }
        return true;
    }

    @Override
    public boolean isHistoryEnabled() {
        return preferenceController.isPortalHistoryEnabled();
    }

    @Override
    public List<CourseClass> getContactCourseClasses(Contact contact, CourseClassFilter filter) {
        List<CourseClass> courseClasses = new ArrayList<>();

        if(contact.getTutor() != null)
            courseClasses.addAll(getTutorCourseClasses(contact, filter));

        if(contact.getStudent() != null && filter != CourseClassFilter.UNCONFIRMED)
            courseClasses.addAll(getStudentCourseClasses(contact, filter));

        Ordering ordering = new Ordering(CourseClass.START_DATE_PROPERTY, SortOrder.ASCENDING);
        ordering.orderList(courseClasses);

        return courseClasses;
    }


    private List<CourseClass> getStudentCourseClasses(Contact contact, CourseClassFilter filter) {
        if(contact.getStudent() != null){
        Expression expr;
        expr = ExpressionFactory.matchExp(CourseClass.ENROLMENTS_PROPERTY + "." + Enrolment.STUDENT_PROPERTY, contact.getStudent());
        expr = expr.andExp(ExpressionFactory.matchExp(CourseClass.ENROLMENTS_PROPERTY + "." + Enrolment.STATUS_PROPERTY, EnrolmentStatus.SUCCESS));
        expr = expr.andExp(ExpressionFactory.matchExp(CourseClass.CANCELLED_PROPERTY, false));

        SelectQuery q = new SelectQuery(CourseClass.class, expr);
        q.setCacheStrategy(QueryCacheStrategy.LOCAL_CACHE);
        q.setCacheGroups(CacheGroup.COURSES.name());
		q.addPrefetch(CourseClass.SESSIONS_PROPERTY);

        List <CourseClass> courseClasses = cayenneService.sharedContext().performQuery(q);

            switch (filter) {

                case CURRENT:
                    return getCurrentStudentClasses(courseClasses, contact);
                case PAST:
                    return getPastStudentClasses(courseClasses, contact);
                case ALL:
                    return courseClasses;
                default:
                    throw new IllegalArgumentException();
            }
	    }
		return null;

    }


    private List<CourseClass> getTutorCourseClasses(Contact contact, CourseClassFilter filter) {
        if(contact.getTutor() != null){
            Expression expr = ExpressionFactory.matchExp(CourseClass.TUTOR_ROLES_PROPERTY + "." + TutorRole.TUTOR_PROPERTY, contact.getTutor());
            expr = expr.andExp(ExpressionFactory.matchExp(CourseClass.CANCELLED_PROPERTY, false));
            SelectQuery q = new SelectQuery(CourseClass.class, expr);
            q.setCacheStrategy(QueryCacheStrategy.LOCAL_CACHE);
            q.setCacheGroups(CacheGroup.COURSES.name());
			q.addPrefetch(CourseClass.SESSIONS_PROPERTY);

            List <CourseClass> courseClasses = cayenneService.sharedContext().performQuery(q);
            switch (filter) {
                case UNCONFIRMED:
                   return getUnconfirmedTutorClasses(courseClasses,contact);
                case CURRENT:
                   return getCurrentTutorClasses(courseClasses);
                case PAST:
                    return getPastTutorClasses(courseClasses);
                case ALL:
                    return courseClasses;
                default:
                    throw new IllegalArgumentException();
            }
        }
        return null;
    }

    private  List<CourseClass>  getPastTutorClasses(List<CourseClass> courseClasses){

		List<CourseClass> past = new ArrayList<>();
		past.addAll(courseClasses);
		past.removeAll(getCurrentTutorClasses(courseClasses));

        return past;
    }

    private  List<CourseClass> getCurrentTutorClasses(List<CourseClass> courseClasses){
        List<CourseClass> current = new ArrayList<>();
        Date date = new Date();

             for(CourseClass courseClass : courseClasses){
                 if(courseClass.getIsDistantLearningCourse()){
                     current.add(courseClass);
                 }
                 if(!courseClass.getIsDistantLearningCourse() && !courseClass.getSessions().isEmpty()) {

					 if(courseClass.getEndDate().after(date))
						 current.add(courseClass);
				 }

				 if(!courseClass.getIsDistantLearningCourse() && courseClass.getSessions().isEmpty()) {

					 current.add(courseClass);
				 }

             }

        return  current;
    }



    private List<CourseClass> getUnconfirmedTutorClasses(List<CourseClass> courseClasses, Contact contact){
        List<CourseClass> unconfirmed = new ArrayList<>();

        for(CourseClass courseClass : courseClasses){
           for(TutorRole tutorRole : courseClass.getTutorRoles()){
                 if(tutorRole.getTutor().getId().equals(contact.getTutor().getId())
			        && tutorRole.getIsConfirmed()==false
					&& getCurrentTutorClasses(courseClasses).contains(courseClass)) {

							unconfirmed.add(courseClass);

                 }
           }
        }
        return unconfirmed;
    }



    private List<CourseClass> getCurrentStudentClasses(List<CourseClass> courseClasses, Contact contact){

        List<CourseClass> current = new ArrayList<>();
        Date date = new Date();

        for(CourseClass courseClass : courseClasses){

           if(courseClass.getIsDistantLearningCourse() && courseClass.getMaximumDays() != null){

              for(Enrolment enrolment : courseClass.getEnrolments()){
                  if(enrolment.getStudent().getId().equals(contact.getStudent().getId())){
                     if(DateUtils.addDays(enrolment.getCreated(),courseClass.getMaximumDays()).after(date))
                         current.add(courseClass);
                     break;
                  }
              }
           }

		   if(courseClass.getIsDistantLearningCourse() && courseClass.getMaximumDays() == null) {
			   current.add(courseClass);
		   }

		   if(!courseClass.getIsDistantLearningCourse() && !courseClass.getSessions().isEmpty()) {

              if(courseClass.getEndDate().after(date))
                  current.add(courseClass);
           }

		   if(!courseClass.getIsDistantLearningCourse() && courseClass.getSessions().isEmpty()) {

			   current.add(courseClass);
		   }


        }

        return current;
    }

    private List<CourseClass> getPastStudentClasses(List<CourseClass> courseClasses, Contact contact){
        List<CourseClass> past = new ArrayList<>();
		past.addAll(courseClasses);
		past.removeAll(getCurrentStudentClasses(courseClasses,contact));
		return  past;
    }

	@Override
	public List<BinaryInfo> getCommonTutorsBinaryInfo(){

		ObjectContext sharedContext = cayenneService.sharedContext();

		SelectQuery query = new SelectQuery(BinaryInfo.class, ExpressionFactory.matchExp(
				BinaryInfo.WEB_VISIBLE_PROPERTY, AttachmentInfoVisibility.TUTORS).andExp(
				ExpressionFactory.matchExp(BinaryInfo.COLLEGE_PROPERTY, webSiteService.getCurrentCollege())).andExp(
				ExpressionFactory.matchExp(BinaryInfo.BINARY_INFO_RELATIONS_PROPERTY + "+." + BinaryInfoRelation.CREATED_PROPERTY, null)));

		return (List<BinaryInfo>) sharedContext.performQuery(query);

	}

	@Override
	public List<BinaryInfo> getAttachedFiles(CourseClass courseClass, Contact contact) {

		ObjectContext sharedContext = cayenneService.sharedContext();

		if (contact.getTutor() !=null) {
			SelectQuery query = new SelectQuery(TutorRole.class, ExpressionFactory.matchExp(
					TutorRole.COURSE_CLASS_PROPERTY, courseClass).andExp(
					ExpressionFactory.matchExp(TutorRole.TUTOR_PROPERTY, contact.getTutor())));
			List<TutorRole> tutorRole = sharedContext.performQuery(query);
			if (!tutorRole.isEmpty()) {
				return getAttachedFilesForTutor(courseClass);
			}
		}
		return getAttachedFilesForStudent(courseClass);
	}

	private List<BinaryInfo> getAttachedFilesForStudent(CourseClass courseClass) {

		ObjectContext sharedContext = cayenneService.sharedContext();

		Expression exp = ExpressionFactory.inExp(BinaryInfo.WEB_VISIBLE_PROPERTY, AttachmentInfoVisibility.STUDENTS, AttachmentInfoVisibility.PRIVATE, AttachmentInfoVisibility.PUBLIC)
				.andExp(ExpressionFactory.matchExp(BinaryInfo.BINARY_INFO_RELATIONS_PROPERTY + "." + BinaryInfoRelation.ENTITY_WILLOW_ID_PROPERTY, courseClass.getId()))
				.andExp(ExpressionFactory.matchExp(BinaryInfo.BINARY_INFO_RELATIONS_PROPERTY + "." + BinaryInfoRelation.ENTITY_IDENTIFIER_PROPERTY, courseClass.getClass().getSimpleName()));
		return sharedContext.performQuery(new SelectQuery(BinaryInfo.class, exp));
	}

	private List<BinaryInfo> getAttachedFilesForTutor(CourseClass courseClass) {
		ObjectContext sharedContext = cayenneService.sharedContext();

		Expression exp = ExpressionFactory.matchExp(BinaryInfo.BINARY_INFO_RELATIONS_PROPERTY + "." + BinaryInfoRelation.ENTITY_WILLOW_ID_PROPERTY, courseClass.getId())
				.andExp(ExpressionFactory.matchExp(BinaryInfo.BINARY_INFO_RELATIONS_PROPERTY + "." + BinaryInfoRelation.ENTITY_IDENTIFIER_PROPERTY, courseClass.getClass().getSimpleName()));
		return sharedContext.performQuery(new SelectQuery(BinaryInfo.class, exp));
	}

	@Override
	public boolean hasResult(Contact user, CourseClass courseClass) {

		Student student = user.getStudent();
		Expression exp = ExpressionFactory.matchExp(Enrolment.STUDENT_PROPERTY , student);
		List<Enrolment> enrolments = exp.filterObjects(courseClass.getValidEnrolments());

		return !enrolments.isEmpty();
	}
}
