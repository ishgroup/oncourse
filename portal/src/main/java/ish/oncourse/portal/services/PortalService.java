package ish.oncourse.portal.services;

import ish.common.types.EnrolmentStatus;
import ish.oncourse.model.*;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.cache.CacheGroup;
import ish.oncourse.services.courseclass.CourseClassFilter;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.util.FormatUtils;
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
    private IBinaryDataService binaryDataService;

    @Inject
    private IAuthenticationService authenticationService;



    @Override
    public JSONObject getSession(Session session) {
        JSONObject result = new JSONObject();
        result.put("startDate",FormatUtils.getDateFormat("EEEE dd MMMMM h:mma", session.getTimeZone()).format(session.getStartDate()));
        result.put("endDate", FormatUtils.getDateFormat("EEEE dd MMMMM h:mma", session.getTimeZone()).format(session.getEndDate()));
        return result;
    }

    public JSONObject getAttendences(Session session)
   {
       List<Attendance> attendances = session.getAttendances();

       JSONObject result = new JSONObject();
       result.append("session", getSession(session));

        for(Attendance attendance : attendances)
        {
            result.put(String.format("%s",attendance.getStudent().getId()),String.format("%s",attendance.getAttendanceType()));
        }



        return result;
    }



    public JSONObject getNearesSessionIndex(Integer i)
   {

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
        for (Session session : sessions) {
            TimeZone timeZone = FormatUtils.getClientTimeZone(session.getCourseClass());
            result.put(FormatUtils.getDateFormat("MM-dd-yyyy",timeZone).format(session.getStartDate()),
                    String.format("<a href='#class-%s'>%s</a>", session.getCourseClass().getId(), formatDate(session)));
        }
        return result;
    }

    private String formatDate(Session session) {
        TimeZone timeZone = FormatUtils.getClientTimeZone(session.getCourseClass());
        return String.format("%s - %s",
                FormatUtils.getDateFormat(FormatUtils.dateFormatForTimeline, timeZone).format(session.getStartDate()),
                FormatUtils.getDateFormat(FormatUtils.timeFormatWithTimeZoneString, timeZone).format(session.getEndDate()));
    }

    public String getJSONScript(Object bean) throws IOException {
        StringWriter writer = new StringWriter();
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        mapper.writeValue(writer, bean);
        return writer.toString();
    }

    public boolean isApproved(Contact tutor, CourseClass courseClass) {
        for (TutorRole t : courseClass.getTutorRoles()) {
            if (t.getTutor().getContact().getId().equals(tutor.getId())) {
                return t.getIsConfirmed();
            }
        }
        return false;
    }

    @Override
    public boolean isHistoryEnabled() {
        return preferenceController.isPortalHistoryEnabled();
    }

    @Override
    public boolean isHasResources(List<CourseClass> courseClasses) {
        boolean result=false;

        for(CourseClass courseClass : courseClasses){
            if(!binaryDataService.getAttachedFiles(courseClass.getId(), CourseClass.class.getSimpleName(), false).isEmpty()){
                result=true;
                break;
            }
        }
        return  result;
    }

    @Override
    public boolean isHasResult() {
        boolean result=false;

        if(authenticationService.getUser().getStudent()!=null){
            for(Enrolment enrolment : authenticationService.getUser().getStudent().getEnrolments()){
                Course course = enrolment.getCourseClass().getCourse();
                if(!course.getModules().isEmpty() || course.getQualification()!=null){
                    result = true;
                    break;
                }
            }
        }
        return result;
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
        expr = expr.andExp(ExpressionFactory.noMatchExp(CourseClass.CANCELLED_PROPERTY, Boolean.TRUE));

        SelectQuery q = new SelectQuery(CourseClass.class, expr);
        q.setCacheStrategy(QueryCacheStrategy.LOCAL_CACHE);
        q.setCacheGroups(CacheGroup.COURSES.name());

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
            expr = expr.andExp(ExpressionFactory.noMatchExp(CourseClass.CANCELLED_PROPERTY, Boolean.TRUE));
            SelectQuery q = new SelectQuery(CourseClass.class, expr);
            q.setCacheStrategy(QueryCacheStrategy.LOCAL_CACHE);
            q.setCacheGroups(CacheGroup.COURSES.name());

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
        Date date = new Date();

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
      //  Date date = new Date();
		past.addAll(courseClasses);
		past.removeAll(getCurrentStudentClasses(courseClasses,contact));
		return  past;

       /* for(CourseClass courseClass : courseClasses){

            if(courseClass.getIsDistantLearningCourse() && courseClass.getMaximumDays() != null){

                for(Enrolment enrolment : courseClass.getEnrolments()){
                    if(enrolment.getStudent().getId().equals(contact.getStudent().getId())){
                        if(DateUtils.addDays(enrolment.getCreated(),courseClass.getMaximumDays()).before(date))
                            past.add(courseClass);
                        break;
                    }
                }
            }else if(!courseClass.getIsDistantLearningCourse()){

                if(courseClass.getEndDate().before(date))
                    past.add(courseClass);
            }
        }

        return past;            */
    }




}
