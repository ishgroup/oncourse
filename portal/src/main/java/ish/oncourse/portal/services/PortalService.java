package ish.oncourse.portal.services;

import ish.common.types.AttachmentInfoVisibility;
import ish.common.types.EnrolmentStatus;
import ish.common.types.OutcomeStatus;
import ish.math.Money;
import ish.oncourse.model.*;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.services.cache.CacheGroup;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.courseclass.CourseClassFilter;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.util.FormatUtils;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.ApplicationStateManager;

import java.text.SimpleDateFormat;
import java.util.*;

import static ish.oncourse.portal.services.PortalUtils.*;

/**
 * User: artem
 * Date: 10/14/13
 * Time: 3:35 PM
 */
public class PortalService implements IPortalService {

    private static final Logger logger = Logger.getLogger(PortalService.class);

    @Inject
    private IAuthenticationService authenticationService;

    @Inject
    private ICourseClassService courseClassService;

    @Inject
    private ICayenneService cayenneService;

    @Inject
    private PreferenceController preferenceController;

    @Inject
    private IWebSiteService webSiteService;

    @Inject
    private ITagService tagService;

    @Inject
    private ICookiesService cookiesService;

    @Inject
    private ApplicationStateManager applicationStateManager;

    @Persist(value = "session")
    private Contact selectedContact;

    @Override
    public Contact getContact() {
        return selectedContact == null ? getAuthenticatedUser() : selectedContact;
    }

    @Override
    public Contact getAuthenticatedUser() {
        return authenticationService.getUser();
    }

    public Notification getNotification() {
        if (!applicationStateManager.exists(Notification.class)) {
            Notification notification = new Notification();
            notification.setNewHistoryCount(getNewPaymentsCount() + getNewInvoicesCount() + getNewEnrolmentsCount());
            notification.setNewResultsCount(getNewResultsCount());
            notification.setNewResourcesCount(getNewResourcesCount());
            applicationStateManager.set(Notification.class, notification);
        }
        return applicationStateManager.get(Notification.class);
    }

    @Override
    public Date getLastLoginTime() {

        String sd = cookiesService.getCookieValue(COOKIE_NAME_lastLoginTime);
        SimpleDateFormat format = new SimpleDateFormat(PortalUtils.DATE_FORMAT_EEE_MMM_dd_hh_mm_ss_z_yyyy);
        if (StringUtils.trimToNull(sd) != null) {
            try {
                return format.parse(sd);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                return new Date(0);
            }
        } else {
            return new Date(0);
        }
    }

    @Override
    public JSONObject getSession(Session session) {
        JSONObject result = new JSONObject();
        result.put("startDate", FormatUtils.getDateFormat(DATE_FORMAT_EEEE_dd_MMMMM_h_mma, session.getTimeZone()).format(session.getStartDate()));
        result.put("endDate", FormatUtils.getDateFormat(DATE_FORMAT_EEEE_dd_MMMMM_h_mma, session.getTimeZone()).format(session.getEndDate()));
        return result;
    }

    public JSONObject getAttendences(Session session) {
        List<Attendance> attendances = session.getAttendances();

        JSONObject result = new JSONObject();
        result.append("session", getSession(session));

        for (Attendance attendance : attendances) {
            result.put(String.format("%s", attendance.getStudent().getId()), String.format("%s", attendance.getAttendanceType()));
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
    public JSONObject getCalendarEvents() {
        List<Session> sessions = courseClassService.getContactSessions(getContact());

        JSONObject result = new JSONObject();

        Map<String, List<Session>> daysSessionMap = new HashMap<>();

        for (Session session : sessions) {
            TimeZone timeZone = courseClassService.getClientTimeZone(session.getCourseClass());
            String key = FormatUtils.getDateFormat("MM-dd-yyyy", timeZone).format(session.getStartDate());

            if (daysSessionMap.get(key) == null) {
                daysSessionMap.put(key, new ArrayList<Session>());
            }

            daysSessionMap.get(key).add(session);
        }

        for (String key : daysSessionMap.keySet()) {
            String events = builEventsString(daysSessionMap.get(key));
            result.put(key, events);
        }

        return result;
    }

    private String builEventsString(List<Session> sessions) {

        StringBuilder events = new StringBuilder();

        for (Session session : sessions) {
            events.append(String.format("<li><a href='#class-%s' class=\"event\">%s</a></li>", session.getCourseClass().getId(), formatDate(session)));
        }

        return String.format("<ul>%s</ul>", events);
    }


    private String formatDate(Session session) {
        TimeZone timeZone = courseClassService.getClientTimeZone(session.getCourseClass());
        return String.format("%s - %s",
                FormatUtils.getDateFormat(FormatUtils.shortTimeFormatString, timeZone).format(session.getStartDate()),
                FormatUtils.getDateFormat(FormatUtils.timeFormatWithTimeZoneString, timeZone).format(session.getEndDate()));
    }

    public boolean isApproved(CourseClass courseClass) {

        Expression exp = ExpressionFactory.matchExp(TutorRole.TUTOR_PROPERTY,
                getContact().getTutor()).andExp(ExpressionFactory.matchExp(TutorRole.COURSE_CLASS_PROPERTY, courseClass));
        SelectQuery q = new SelectQuery(TutorRole.class, exp);

        List<TutorRole> tutorRoles = cayenneService.sharedContext().performQuery(q);

        for (TutorRole t : tutorRoles) {
            if (!t.getIsConfirmed()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<CourseClass> getContactCourseClasses(CourseClassFilter filter) {
        List<CourseClass> courseClasses = new ArrayList<>();

        Contact contact = getContact();
        if (contact.getTutor() != null)
            courseClasses.addAll(getTutorCourseClasses(contact, filter));

        if (contact.getStudent() != null && filter != CourseClassFilter.UNCONFIRMED)
            courseClasses.addAll(getStudentCourseClasses(contact, filter));

        Ordering ordering = new Ordering(CourseClass.START_DATE_PROPERTY, SortOrder.ASCENDING);
        ordering.orderList(courseClasses);

        return courseClasses;
    }


    private List<CourseClass> getStudentCourseClasses(Contact contact, CourseClassFilter filter) {
        if (contact.getStudent() != null) {
            Expression expr = getStudentClassesExpression(contact);

            SelectQuery q = new SelectQuery(CourseClass.class, expr);
            q.setCacheStrategy(QueryCacheStrategy.LOCAL_CACHE);
            q.setCacheGroups(CacheGroup.COURSES.name());
            q.addPrefetch(CourseClass.SESSIONS_PROPERTY);

            List<CourseClass> courseClasses = cayenneService.sharedContext().performQuery(q);

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
        if (contact.getTutor() != null) {
            SelectQuery q = new SelectQuery(CourseClass.class, getTutorClassesExpression());
            q.setCacheStrategy(QueryCacheStrategy.LOCAL_CACHE);
            q.setCacheGroups(CacheGroup.COURSES.name());
            q.addPrefetch(CourseClass.SESSIONS_PROPERTY);

            List<CourseClass> courseClasses = cayenneService.sharedContext().performQuery(q);
            switch (filter) {
                case UNCONFIRMED:
                    return getUnconfirmedTutorClasses(courseClasses, contact);
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

    private Expression getStudentClassesExpression(Contact contact) {
        Expression expr = ExpressionFactory.matchExp(CourseClass.ENROLMENTS_PROPERTY + "." + Enrolment.STUDENT_PROPERTY, getContact().getStudent());
        expr = expr.andExp(ExpressionFactory.matchExp(CourseClass.ENROLMENTS_PROPERTY + "." + Enrolment.STATUS_PROPERTY, EnrolmentStatus.SUCCESS));
        expr = expr.andExp(ExpressionFactory.matchExp(CourseClass.CANCELLED_PROPERTY, false));
        return expr;
    }


    private Expression getTutorClassesExpression() {
        Expression expr = ExpressionFactory.matchExp(CourseClass.TUTOR_ROLES_PROPERTY + "." + TutorRole.TUTOR_PROPERTY, getContact().getTutor());
        expr = expr.andExp(ExpressionFactory.matchExp(CourseClass.CANCELLED_PROPERTY, false));
        return expr;
    }

    private List<CourseClass> getPastTutorClasses(List<CourseClass> courseClasses) {

        List<CourseClass> past = new ArrayList<>();
        past.addAll(courseClasses);
        past.removeAll(getCurrentTutorClasses(courseClasses));

        return past;
    }

    private List<CourseClass> getCurrentTutorClasses(List<CourseClass> courseClasses) {
        List<CourseClass> current = new ArrayList<>();
        Date date = new Date();

        for (CourseClass courseClass : courseClasses) {
            if (courseClass.getIsDistantLearningCourse()) {
                current.add(courseClass);
            }
            if (!courseClass.getIsDistantLearningCourse() && !courseClass.getSessions().isEmpty()) {

                if (courseClass.getEndDate().after(date))
                    current.add(courseClass);
            }

            if (!courseClass.getIsDistantLearningCourse() && courseClass.getSessions().isEmpty()) {

                current.add(courseClass);
            }

        }

        return current;
    }


    private List<CourseClass> getUnconfirmedTutorClasses(List<CourseClass> courseClasses, Contact contact) {
        List<CourseClass> unconfirmed = new ArrayList<>();

        for (CourseClass courseClass : courseClasses) {
            for (TutorRole tutorRole : courseClass.getTutorRoles()) {
                if (tutorRole.getTutor().getId().equals(contact.getTutor().getId())
                        && !tutorRole.getIsConfirmed()
                        && getCurrentTutorClasses(courseClasses).contains(courseClass)) {

                    unconfirmed.add(courseClass);

                }
            }
        }
        return unconfirmed;
    }


    private List<CourseClass> getCurrentStudentClasses(List<CourseClass> courseClasses, Contact contact) {

        List<CourseClass> current = new ArrayList<>();
        Date date = new Date();

        for (CourseClass courseClass : courseClasses) {

            if (courseClass.getIsDistantLearningCourse() && courseClass.getMaximumDays() != null) {

                for (Enrolment enrolment : courseClass.getEnrolments()) {
                    if (enrolment.getStudent().getId().equals(contact.getStudent().getId())) {
                        /**
                         * we added enrolment.getCreated() != null condition to exlude NPE when some old enrolment has null value in create field
                         * TODO The condition should be deleted after 21309 will be closed
                         */
                        if (enrolment.getCreated() != null &&
                                DateUtils.addDays(enrolment.getCreated(), courseClass.getMaximumDays()).after(date))
                            current.add(courseClass);
                        break;
                    }
                }
            }

            if (courseClass.getIsDistantLearningCourse() && courseClass.getMaximumDays() == null) {
                current.add(courseClass);
            }

            if (!courseClass.getIsDistantLearningCourse() && !courseClass.getSessions().isEmpty()) {

                if (courseClass.getEndDate().after(date))
                    current.add(courseClass);
            }

            if (!courseClass.getIsDistantLearningCourse() && courseClass.getSessions().isEmpty()) {

                current.add(courseClass);
            }


        }

        return current;
    }

    private List<CourseClass> getPastStudentClasses(List<CourseClass> courseClasses, Contact contact) {
        List<CourseClass> past = new ArrayList<>();
        past.addAll(courseClasses);
        past.removeAll(getCurrentStudentClasses(courseClasses, contact));
        return past;
    }

    public CourseClass getCourseClassBy(long id) {
        ObjectContext context = cayenneService.newContext();
        List<CourseClass> result = new ArrayList<>();
        if (getContact().getTutor() != null) {
            Expression expression = getTutorClassesExpression();
            expression = expression.andExp(ExpressionFactory.matchDbExp(CourseClass.ID_PK_COLUMN, id));
            SelectQuery q = new SelectQuery(CourseClass.class, expression);
            result.addAll(context.performQuery(q));
        }

        if (getContact().getStudent() != null) {
            Expression expression = getStudentClassesExpression(getContact());
            expression = expression.andExp(ExpressionFactory.matchDbExp(CourseClass.ID_PK_COLUMN, id));

            SelectQuery q = new SelectQuery(CourseClass.class, expression);
            result.addAll(context.performQuery(q));
        }

        return result.isEmpty() ? null : result.get(0);


    }

    public Invoice getInvoiceBy(long id) {
        ObjectContext context = cayenneService.newContext();
        Expression expression = ExpressionFactory.matchExp(Invoice.CONTACT_PROPERTY, getContact());
        expression = expression.andExp(ExpressionFactory.matchDbExp(Invoice.ID_PK_COLUMN, id));
        SelectQuery q = new SelectQuery(Invoice.class, expression);
        List<Invoice> result = context.performQuery(q);
        return result.isEmpty() ? null : result.get(0);
    }


    public PaymentIn getPaymentInBy(long id) {
        ObjectContext context = cayenneService.newContext();
        Expression expression = ExpressionFactory.matchExp(PaymentIn.CONTACT_PROPERTY, getContact());
        expression = expression.andExp(ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, id));
        SelectQuery q = new SelectQuery(PaymentIn.class, expression);
        List<PaymentIn> result = context.performQuery(q);
        return result.isEmpty() ? null : result.get(0);
    }

    public Tag getMailingList(long id) {
        ObjectContext context = cayenneService.newContext();

        Expression expression = ExpressionFactory.matchExp(Taggable.COLLEGE_PROPERTY, getContact().getCollege());
        expression = expression.andExp(ExpressionFactory.matchExp(Tag.TAGGABLE_TAGS_PROPERTY + "." +
                        TaggableTag.TAGGABLE_PROPERTY + "." +
                        Taggable.ENTITY_IDENTIFIER_PROPERTY,
                Contact.class.getSimpleName()
        ));
        expression = expression.andExp(ExpressionFactory.matchExp(Tag.TAGGABLE_TAGS_PROPERTY + "." +
                        TaggableTag.TAGGABLE_PROPERTY + "." +
                        Taggable.ENTITY_WILLOW_ID_PROPERTY,
                getContact().getId()
        ));
        SelectQuery selectQuery = new SelectQuery(Tag.class, expression);

        List<Tag> result = context.performQuery(selectQuery);
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public List<Document> getTutorCommonResources() {

        if (authenticationService.isTutor()) {
            ObjectContext sharedContext = cayenneService.sharedContext();

            SelectQuery query = new SelectQuery(Document.class, ExpressionFactory.matchExp(
                    Document.WEB_VISIBILITY_PROPERTY, AttachmentInfoVisibility.TUTORS).andExp(
                    ExpressionFactory.matchExp(Document.COLLEGE_PROPERTY, webSiteService.getCurrentCollege())).andExp(
                    ExpressionFactory.matchExp(Document.BINARY_INFO_RELATIONS_PROPERTY + "+." + BinaryInfoRelation.CREATED_PROPERTY, null)));

            return (List<Document>) sharedContext.performQuery(query);
        } else {
            return Collections.emptyList();
        }

    }

    public List<Enrolment> getEnrolments() {
        Student student = getContact().getStudent();
        if (student != null) {
            ObjectContext sharedContext = cayenneService.sharedContext();
            Expression exp = ExpressionFactory.matchExp(Enrolment.STUDENT_PROPERTY, student);
            exp = exp.andExp(ExpressionFactory.matchExp(Enrolment.STATUS_PROPERTY, EnrolmentStatus.SUCCESS));
            SelectQuery query = new SelectQuery(Enrolment.class, exp);
            query.addOrdering(Enrolment.MODIFIED_PROPERTY, SortOrder.DESCENDING);
            return (List<Enrolment>) sharedContext.performQuery(query);
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public List<Document> getResourcesBy(CourseClass courseClass) {

        Contact contact = getContact();
        ObjectContext sharedContext = cayenneService.sharedContext();
        contact = sharedContext.localObject(contact);

        if (contact.getTutor() != null) {
            SelectQuery query = new SelectQuery(TutorRole.class, ExpressionFactory.matchExp(
                    TutorRole.COURSE_CLASS_PROPERTY, courseClass).andExp(
                    ExpressionFactory.matchExp(TutorRole.TUTOR_PROPERTY, contact.getTutor())));
            List<TutorRole> tutorRole = sharedContext.performQuery(query);
            if (!tutorRole.isEmpty()) {
                return getAttachedFilesForTutor(courseClass);
            }
        }
		
		if (contact.getStudent() != null) {
			return getAttachedFilesForStudent(contact.getStudent(), courseClass);
		}
		
        return Collections.emptyList();
    }

    private List<Document> getAttachedFilesForStudent(Student student, CourseClass courseClass) {

		// only students with active enrolments can view class attachments
		Expression filter = ExpressionFactory.matchExp(Enrolment.STUDENT_PROPERTY, student);

		if (filter.filterObjects(courseClass.getValidEnrolments()).isEmpty()) {
			return Collections.emptyList();
		}

        ObjectContext sharedContext = cayenneService.sharedContext();
        ArrayList<Document> result = new ArrayList<>();

        Expression exp = ExpressionFactory.inExp(Document.WEB_VISIBILITY_PROPERTY, AttachmentInfoVisibility.STUDENTS, AttachmentInfoVisibility.PUBLIC)
                .andExp(ExpressionFactory.matchExp(Document.BINARY_INFO_RELATIONS_PROPERTY + "." + BinaryInfoRelation.ENTITY_WILLOW_ID_PROPERTY, courseClass.getCourse().getId()))
                .andExp(ExpressionFactory.matchExp(Document.BINARY_INFO_RELATIONS_PROPERTY + "." + BinaryInfoRelation.ENTITY_IDENTIFIER_PROPERTY, Course.class.getSimpleName()));
        result.addAll(sharedContext.performQuery(new SelectQuery(Document.class, exp)));

        exp = ExpressionFactory.inExp(Document.WEB_VISIBILITY_PROPERTY, AttachmentInfoVisibility.STUDENTS, AttachmentInfoVisibility.PUBLIC)
                .andExp(ExpressionFactory.matchExp(Document.BINARY_INFO_RELATIONS_PROPERTY + "." + BinaryInfoRelation.ENTITY_WILLOW_ID_PROPERTY, courseClass.getId()))
                .andExp(ExpressionFactory.matchExp(Document.BINARY_INFO_RELATIONS_PROPERTY + "." + BinaryInfoRelation.ENTITY_IDENTIFIER_PROPERTY, courseClass.getClass().getSimpleName()));
        result.addAll(sharedContext.performQuery(new SelectQuery(Document.class, exp)));
        return result;
    }

    private List<Document> getAttachedFilesForTutor(CourseClass courseClass) {
        ObjectContext sharedContext = cayenneService.sharedContext();
        ArrayList<Document> result = new ArrayList<>();

        Expression exp = ExpressionFactory.noMatchExp(Document.WEB_VISIBILITY_PROPERTY, AttachmentInfoVisibility.PRIVATE) 
				.andExp(ExpressionFactory.matchExp(Document.BINARY_INFO_RELATIONS_PROPERTY + "." + BinaryInfoRelation.ENTITY_WILLOW_ID_PROPERTY, courseClass.getCourse().getId()))
                .andExp(ExpressionFactory.matchExp(Document.BINARY_INFO_RELATIONS_PROPERTY + "." + BinaryInfoRelation.ENTITY_IDENTIFIER_PROPERTY, Course.class.getSimpleName()));
        result.addAll(sharedContext.performQuery(new SelectQuery(Document.class, exp)));

        exp = ExpressionFactory.noMatchExp(Document.WEB_VISIBILITY_PROPERTY, AttachmentInfoVisibility.PRIVATE)
				.andExp(ExpressionFactory.matchExp(Document.BINARY_INFO_RELATIONS_PROPERTY + "." + BinaryInfoRelation.ENTITY_WILLOW_ID_PROPERTY, courseClass.getId()))
                .andExp(ExpressionFactory.matchExp(Document.BINARY_INFO_RELATIONS_PROPERTY + "." + BinaryInfoRelation.ENTITY_IDENTIFIER_PROPERTY, courseClass.getClass().getSimpleName()));
        result.addAll(sharedContext.performQuery(new SelectQuery(Document.class, exp)));

        return result;
    }

    public List<Document> getResources() {
        List<Document> resources = new ArrayList<>();
        resources.addAll(getTutorCommonResources());

        List<PCourseClass> courseClasses = fillCourseClassSessions(CourseClassFilter.CURRENT);

        for (PCourseClass courseClass : courseClasses) {
            {
                resources.addAll(getResourcesBy(courseClass.getCourseClass()));
            }
        }
        return resources;
    }


    @Override
    public boolean hasResult(CourseClass courseClass) {

        Student student = getContact().getStudent();
        Expression exp = ExpressionFactory.matchExp(Enrolment.STUDENT_PROPERTY, student);
        List<Enrolment> enrolments = exp.filterObjects(courseClass.getValidEnrolments());

        return !enrolments.isEmpty();
    }

    private int getNewResultsCount() {
        Expression expression = getOutcomeExpression();
        expression = expression.andExp(ExpressionFactory.greaterOrEqualExp(Outcome.MODIFIED_PROPERTY, getLastLoginTime()));

        ObjectContext sharedContext = cayenneService.sharedContext();
        return sharedContext.performQuery(new SelectQuery(Outcome.class, expression)).size();
    }

    private Expression getOutcomeExpression() {
        Expression expression = ExpressionFactory.matchExp(Outcome.ENROLMENT_PROPERTY + "." + Enrolment.STATUS_PROPERTY, EnrolmentStatus.SUCCESS);
        expression = expression.andExp(ExpressionFactory.matchExp(Outcome.ENROLMENT_PROPERTY + "." + Enrolment.STUDENT_PROPERTY, getContact().getStudent()));
        expression = expression.andExp(ExpressionFactory.noMatchExp(Outcome.MODULE_PROPERTY, null)
                .orExp(ExpressionFactory.noMatchExp(Outcome.STATUS_PROPERTY, OutcomeStatus.STATUS_NOT_SET)));
        return expression;
    }

    public List<Outcome> getResultsBy(CourseClass courseClass) {
        Expression expression = getOutcomeExpression();
        expression = expression.andExp( ExpressionFactory.matchExp(Outcome.ENROLMENT_PROPERTY + "." + Enrolment.COURSE_CLASS_PROPERTY, courseClass));

        ObjectContext sharedContext = cayenneService.sharedContext();
        SelectQuery selectQuery = new SelectQuery(Outcome.class, expression);
        selectQuery.addOrdering(Outcome.MODIFIED_PROPERTY, SortOrder.DESCENDING);
        return sharedContext.performQuery(selectQuery);
    }

    private int getNewResourcesCount() {
        Date lastLoginTime = getLastLoginTime();
        int newResourcesCount = 0;

        for (Document document : getResources()) {
            /**
             * we added binaryInfo.getModified() != null condition to exlude NPE when some old binaryInfo has null value in create Modified.
             * TODO The condition should be deleted after 21309 will be closed
             */
            if (document.getModified() != null && document.getModified().after(lastLoginTime)) {
                newResourcesCount++;
            }
        }
        return newResourcesCount;
    }

    @Override
    public boolean hasResults() {
        return getContact().getStudent() != null && !getStudentCourseClasses(getContact(), CourseClassFilter.CURRENT).isEmpty();
    }


    @Override
    public List<PCourseClass> fillCourseClassSessions(CourseClassFilter filter) {

        List<CourseClass> courseClasses = getContactCourseClasses(filter);

        List<PCourseClass> pCourseClasses = new ArrayList<>();
        List<Session> sessions = new ArrayList<>();

        for (CourseClass courseClass : courseClasses) {

            if (courseClass.getSessions().isEmpty()) {
                pCourseClasses.add(new PCourseClass(courseClass, null, null));
            } else {
                sessions.addAll(courseClass.getSessions());
            }
        }

        Ordering.orderList(sessions, Arrays.asList(new Ordering(Session.START_DATE_PROPERTY, SortOrder.ASCENDING)));
        Expression ex = ExpressionFactory.greaterExp(Session.START_DATE_PROPERTY, new Date());

        sessions = ex.filterObjects(sessions);

        for (Session session : sessions) {
            PCourseClass pCourseClass = new PCourseClass(session.getCourseClass(), session.getStartDate(), session.getEndDate());

            if (!pCourseClasses.contains(pCourseClass)) {
                pCourseClasses.add(pCourseClass);
            }

        }

        return pCourseClasses;
    }

    public String getUrlBy(CourseClass courseClass) {
        return getClassDetailsURLBy(courseClass, getDomainName(preferenceController));
    }

    public String getUrlBy(Course course) {
        return getCourseDetailsURLBy(course, getDomainName(preferenceController));
    }


    @Override
    public List<PaymentIn> getPayments() {

        Contact contact = getContact();

        ObjectContext sharedContext = cayenneService.sharedContext();

        SelectQuery query = new SelectQuery(PaymentIn.class, ExpressionFactory.matchExp(
                PaymentIn.CONTACT_PROPERTY, contact).andExp(
                ExpressionFactory.greaterExp(PaymentIn.AMOUNT_PROPERTY, Money.ZERO)));
        return (List<PaymentIn>) sharedContext.performQuery(query);
    }

    public int getNewPaymentsCount() {
        Contact contact = getContact();
        Date lastLoginTime = getLastLoginTime();

        ObjectContext sharedContext = cayenneService.sharedContext();
        SelectQuery query = new SelectQuery(PaymentIn.class, ExpressionFactory.matchExp(
                PaymentIn.CONTACT_PROPERTY, contact).andExp(
                ExpressionFactory.greaterExp(PaymentIn.AMOUNT_PROPERTY, Money.ZERO)).andExp(
                ExpressionFactory.greaterOrEqualExp(PaymentIn.MODIFIED_PROPERTY, lastLoginTime)));

        return sharedContext.performQuery(query).size();
    }

    public int getNewInvoicesCount() {
        Contact contact = getContact();
        Date lastLoginTime = getLastLoginTime();

        ObjectContext sharedContext = cayenneService.sharedContext();
        SelectQuery query = new SelectQuery(Invoice.class, ExpressionFactory.matchExp(
                Invoice.CONTACT_PROPERTY, contact).andExp(
                ExpressionFactory.greaterOrEqualExp(Invoice.MODIFIED_PROPERTY, lastLoginTime)));

        return sharedContext.performQuery(query).size();
    }

    public int getNewEnrolmentsCount() {
        Contact contact = getContact();
        Date lastLoginTime = getLastLoginTime();

        ObjectContext sharedContext = cayenneService.sharedContext();
        SelectQuery query = new SelectQuery(Enrolment.class, ExpressionFactory.matchExp(
                Enrolment.STUDENT_PROPERTY, contact.getStudent()).andExp(
                ExpressionFactory.greaterOrEqualExp(Enrolment.MODIFIED_PROPERTY, lastLoginTime)));

        return sharedContext.performQuery(query).size();
    }


    public boolean isNew(CayenneDataObject object) {

        /**
         * we added value != null condition to exlude NPE when some old CayenneDataObject has null value in create field.
         * If the field has null value we show "(not set)" string
         * TODO The condition should be deleted after 21309 will be closed
         */
        try {
            Date value = (Date) BeanUtilsBean.getInstance().getPropertyUtils().getProperty(object, Enrolment.MODIFIED_PROPERTY);
            return value != null && value.after(getLastLoginTime());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Contact> getChildContacts()
    {
        ObjectContext context = cayenneService.newContext();
        Contact parent = Cayenne.objectForPK(context, Contact.class, getAuthenticatedUser().getId());
        List<ContactRelation> contactRelations = parent.getToContacts();
        Expression exp = ExpressionFactory.matchExp(ContactRelation.RELATION_TYPE_PROPERTY + "." + ContactRelationType.DELEGATED_ACCESS_TO_CONTACT_PROPERTY, true);
        contactRelations = exp.filterObjects(contactRelations);
        ArrayList<Contact> result = new ArrayList<>();
        result.add(getAuthenticatedUser());
        for (ContactRelation contactRelation : contactRelations) {
            result.add(contactRelation.getToContact());
        }
        return  Collections.unmodifiableList(result);
    }

    public boolean isSelectedContact(Contact contact)
    {
        return getContact().getId().equals(contact.getId());
    }

    public void selectContact(Contact contact)
    {
        this.selectedContact = contact;
    }

    @Override
    public void logout() {
        selectedContact = null;
        authenticationService.logout();
    }
}
