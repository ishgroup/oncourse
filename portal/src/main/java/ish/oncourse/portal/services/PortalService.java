package ish.oncourse.portal.services;

import ish.common.types.AttachmentInfoVisibility;
import ish.common.types.EnrolmentStatus;
import ish.common.types.OutcomeStatus;
import ish.math.Money;
import ish.oncourse.model.*;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.pages.usi.Usi;
import ish.oncourse.portal.usi.UsiController;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.courseclass.CourseClassFilter;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.ContactFieldHelper;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.reference.ICountryService;
import ish.oncourse.services.reference.ILanguageService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.usi.IUSIVerificationService;
import ish.oncourse.util.FormatUtils;
import ish.util.SecurityUtil;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.internal.util.MessagesImpl;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.Request;

import java.text.SimpleDateFormat;
import java.util.*;

import static ish.oncourse.portal.services.PortalUtils.*;
import static ish.oncourse.services.preference.PreferenceController.ContactFieldSet.enrolment;

/**
 * User: artem
 * Date: 10/14/13
 * Time: 3:35 PM
 */
public class PortalService implements IPortalService {

    private static final Logger logger = LogManager.getLogger();

    @Inject
    private IAuthenticationService authenticationService;

    @Inject
    private ICourseClassService courseClassService;

    @Inject
    private ICayenneService cayenneService;

	@Inject
	private IBinaryDataService binaryDataService;
	
    @Inject
    private PreferenceController preferenceController;

    @Inject
    private IWebSiteService webSiteService;

    @Inject
    private ITagService tagService;

    @Inject
    private ICookiesService cookiesService;

    @Inject
    private ICountryService countryService;

    @Inject
    private ILanguageService languageService;

    @Inject
    private IUSIVerificationService usiVerificationService;

    @Inject
    private ApplicationStateManager applicationStateManager;

    @Inject
    private Request request;

    @Override
    public Contact getContact() {
        Contact selectedContact = getSelectedContact();
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
                return new Date();
            }
        } else {
            return new Date();
        }
    }

    @Override
    public JSONObject getJSONSession(Session session) {
        JSONObject jSession = new JSONObject();
        jSession.put("id", session.getId());
        jSession.put("startDate", FormatUtils.getDateFormat(DATE_FORMAT_EEEE_dd_MMMMM_h_mma, session.getTimeZone()).format(session.getStartDate()));
        jSession.put("endDate", FormatUtils.getDateFormat(DATE_FORMAT_EEEE_dd_MMMMM_h_mma, session.getTimeZone()).format(session.getEndDate()));

        JSONArray array = new JSONArray();
        List<Attendance> attendances = session.getAttendances();
        for (int i = 0; i < attendances.size(); i++) {
            Attendance attendance = attendances.get(i);
            JSONObject jAttendance = new JSONObject();
            jAttendance.put("id", attendance.getId());
            jAttendance.put("studentId", attendance.getStudent().getId());
            jAttendance.put("type", attendance.getAttendanceType());
            array.put(i, jAttendance);
        }
        jSession.put("attendances", array);

        return jSession;
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
            Expression expr = getStudentClassesExpression();

            SelectQuery q = new SelectQuery(CourseClass.class, expr);
            q.setCacheStrategy(QueryCacheStrategy.LOCAL_CACHE);
            q.setCacheGroups(CourseClass.class.getSimpleName());
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
            q.setCacheGroups(CourseClass.class.getSimpleName());
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

    private Expression getStudentClassesExpression() {
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

        Expression filter = ExpressionFactory.greaterExp(CourseClass.END_DATE_PROPERTY, getOldestDate());
        past = filter.filterObjects(past);
        return past;
    }

    private List<CourseClass> getCurrentTutorClasses(List<CourseClass> courseClasses) {
        List<CourseClass> current = new ArrayList<>();
        Date date = getCurrentDate();

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

        List<CourseClass> currentClasses = getCurrentTutorClasses(courseClasses);

        for (CourseClass courseClass : courseClasses) {
            for (TutorRole tutorRole : courseClass.getTutorRoles()) {
                try {
                    if (tutorRole.getTutor().getId().equals(contact.getTutor().getId())
                            && !tutorRole.getIsConfirmed()
                            && currentClasses.contains(courseClass)) {
                        unconfirmed.add(courseClass);
                    }
                } catch (Exception e) {
                    logger.error("Unexpected error. contactId: {}, courseClassId: {}, tutorRoleId: {}", contact.getId(), courseClass.getId(), tutorRole.getId(), e);
                }
            }
        }
        return unconfirmed;
    }


    private List<CourseClass> getCurrentStudentClasses(List<CourseClass> courseClasses, Contact contact) {

        List<CourseClass> current = new ArrayList<>();
        Date date = getCurrentDate();

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

        Expression filter = ExpressionFactory.greaterExp(CourseClass.END_DATE_PROPERTY, getOldestDate());
        past = filter.filterObjects(past);
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
            Expression expression = getStudentClassesExpression();
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

			return ObjectSelect.query(Document.class).where(Document.WEB_VISIBILITY.eq(AttachmentInfoVisibility.TUTORS))
					.and(Document.IS_REMOVED.isFalse())
					.and(Document.COLLEGE.eq(webSiteService.getCurrentCollege()))
					.and(Document.BINARY_INFO_RELATIONS.outer().dot(BinaryInfoRelation.CREATED).isNull()).select(sharedContext);
        } else {
            return Collections.emptyList();
        }

    }
	
	@Override
	public List<Document> getStudentAndTutorCommonResources() {
		if (getContact().getTutor() != null || getContact().getStudent() != null) {
			ObjectContext sharedContext = cayenneService.sharedContext();

			return ObjectSelect.query(Document.class).where(Document.WEB_VISIBILITY.eq(AttachmentInfoVisibility.STUDENTS))
					.and(Document.IS_REMOVED.isFalse())
					.and(Document.COLLEGE.eq(webSiteService.getCurrentCollege()))
					.and(Document.BINARY_INFO_RELATIONS.outer().dot(BinaryInfoRelation.CREATED).isNull()).select(sharedContext);
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
			List<TutorRole> tutorRole = ObjectSelect.query(TutorRole.class).where(TutorRole.COURSE_CLASS.eq(courseClass)).and(TutorRole.TUTOR.eq(contact.getTutor())).select(sharedContext);

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
		ObjectContext sharedContext = cayenneService.sharedContext();

		Enrolment enrolment = ObjectSelect.query(Enrolment.class).where(Enrolment.STUDENT.eq(student)).and(Enrolment.COURSE_CLASS.eq(courseClass)).and(Enrolment.STATUS.eq(EnrolmentStatus.SUCCESS)).selectFirst(sharedContext);
		// only students with active enrolments can view class attachments
        if (enrolment == null) {
            return Collections.emptyList();
        }

        Expression courseQualifier  = Document.BINARY_INFO_RELATIONS.dot(BinaryInfoRelation.ENTITY_IDENTIFIER).eq(Course.class.getSimpleName())
				.andExp(Document.BINARY_INFO_RELATIONS.dot(BinaryInfoRelation.ENTITY_WILLOW_ID).eq(courseClass.getCourse().getId()));

		Expression classQualifier = Document.BINARY_INFO_RELATIONS.dot(BinaryInfoRelation.ENTITY_IDENTIFIER).eq(CourseClass.class.getSimpleName())
				.andExp(Document.BINARY_INFO_RELATIONS.dot(BinaryInfoRelation.ENTITY_WILLOW_ID).eq(courseClass.getId()));

		Expression enrolmentQualifier = Document.BINARY_INFO_RELATIONS.dot(BinaryInfoRelation.ENTITY_IDENTIFIER).eq(Enrolment.class.getSimpleName())
				.andExp(Document.BINARY_INFO_RELATIONS.dot(BinaryInfoRelation.ENTITY_WILLOW_ID).eq(enrolment.getId()));
				
		return ObjectSelect.query(Document.class).where(Document.WEB_VISIBILITY.eq(AttachmentInfoVisibility.STUDENTS))
				.and(Document.IS_REMOVED.isFalse())
				.and(courseQualifier.orExp(classQualifier).orExp(enrolmentQualifier)).select(sharedContext);
    }

    private List<Document> getAttachedFilesForTutor(CourseClass courseClass) {
        ObjectContext sharedContext = cayenneService.sharedContext();

		return ObjectSelect.query(Document.class).where(Document.WEB_VISIBILITY.in(AttachmentInfoVisibility.TUTORS, AttachmentInfoVisibility.STUDENTS))
				.and(Document.IS_REMOVED.isFalse())
				.and((Document.BINARY_INFO_RELATIONS.dot(BinaryInfoRelation.ENTITY_IDENTIFIER).eq(Course.class.getSimpleName())
					.andExp(Document.BINARY_INFO_RELATIONS.dot(BinaryInfoRelation.ENTITY_WILLOW_ID).eq(courseClass.getCourse().getId())))
					.orExp(Document.BINARY_INFO_RELATIONS.dot(BinaryInfoRelation.ENTITY_IDENTIFIER).eq(CourseClass.class.getSimpleName())
					.andExp(Document.BINARY_INFO_RELATIONS.dot(BinaryInfoRelation.ENTITY_WILLOW_ID).eq(courseClass.getId())))).select(sharedContext);
    }

    public List<Document> getResources() {
        List<Document> resources = new ArrayList<>();
        resources.addAll(getTutorCommonResources());
		resources.addAll(getStudentAndTutorCommonResources());

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
        expression = expression.andExp(ExpressionFactory.matchExp(Outcome.ENROLMENT_PROPERTY + "." + Enrolment.COURSE_CLASS_PROPERTY, courseClass));

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
        return getContact().getStudent() != null && !getStudentCourseClasses(getContact(), CourseClassFilter.ALL).isEmpty();
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
        Expression ex = ExpressionFactory.greaterExp(Session.START_DATE_PROPERTY, (filter == CourseClassFilter.CURRENT) ? getCurrentDate(): getOldestDate());

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

    public List<Contact> getChildContacts() {
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
        return Collections.unmodifiableList(result);
    }

    public Contact getSelectedContact() {
        org.apache.tapestry5.services.Session session = request.getSession(false);
        return session != null ? (Contact) session.getAttribute("portal.selectedContact") : null;
    }

    public boolean isSelectedContact(Contact contact) {
        return getContact().getId().equals(contact.getId());
    }

    public void selectContact(Contact contact) {
        org.apache.tapestry5.services.Session session = request.getSession(false);
        if (session != null) {
            session.setAttribute("portal.selectedContact", contact);
            session.setAttribute("portal.usiController", null);
        }
    }

    @Override
    public void logout() {
        authenticationService.logout();
    }

    @Override
    public Survey getStudentSurveyFor(CourseClass courseClass) {
        return getSurveyBy(getContact().getStudent(), courseClass);
    }

    @Override
    public Survey createStudentSurveyFor(CourseClass courseClass) {
        ObjectContext context = cayenneService.newContext();
        Survey survey = context.newObject(Survey.class);
        survey.setCollege(context.localObject(getContact().getCollege()));
        survey.setEnrolment(context.localObject(getEnrolmentBy(getContact().getStudent(), courseClass)));
        return survey;
    }

    public Enrolment getEnrolmentBy(Student student, CourseClass courseClass) {
        Expression enrolmentExp = ExpressionFactory.matchExp(Enrolment.STUDENT_PROPERTY, student)
                .andExp(ExpressionFactory.matchExp(Enrolment.COURSE_CLASS_PROPERTY, courseClass))
                .andExp(ExpressionFactory.matchExp(Enrolment.STATUS_PROPERTY, EnrolmentStatus.SUCCESS));
        SelectQuery query = new SelectQuery(Enrolment.class, enrolmentExp);

        return (Enrolment) Cayenne.objectForQuery(student.getObjectContext(), query);
    }

    public boolean isTutorFor(CourseClass courseClass)
    {
        courseClass = getContact().getTutor().getObjectContext().localObject(courseClass);
        Expression exp = ExpressionFactory.matchExp(TutorRole.TUTOR_PROPERTY, getContact().getTutor());
        return  !exp.filterObjects(courseClass.getTutorRoles()).isEmpty();
    }


    private Survey getSurveyBy(Student student, CourseClass courseClass) {
        Expression surveyExp = ExpressionFactory.matchExp(Survey.ENROLMENT_PROPERTY + "." + Enrolment.STUDENT_PROPERTY, student)
                .andExp(ExpressionFactory.matchExp(Survey.ENROLMENT_PROPERTY + "." + Enrolment.COURSE_CLASS_PROPERTY, courseClass));
        SelectQuery query = new SelectQuery(Survey.class, surveyExp);

        return (Survey) Cayenne.objectForQuery(student.getObjectContext(), query);
    }

    public Survey getAverageSurveyFor(CourseClass courseClass)
    {
        ObjectContext context = cayenneService.newNonReplicatingContext();
        Expression surveyExp = ExpressionFactory.matchExp(Survey.ENROLMENT_PROPERTY + "." + Enrolment.COURSE_CLASS_PROPERTY, courseClass);
        SelectQuery query = new SelectQuery(Survey.class, surveyExp);
        List<Survey> surveys = context.performQuery(query);

        Survey result = new Survey();
        result.setCourseScore(0);
        result.setVenueScore(0);
        result.setTutorScore(0);
        for (Survey survey : surveys) {
            result.setCourseScore(result.getCourseScore() + survey.getCourseScore());
            result.setVenueScore(result.getVenueScore() + survey.getVenueScore());
            result.setTutorScore(result.getTutorScore() + survey.getTutorScore());
        }

        int size = surveys.size();
        if (size != 0) {
            result.setCourseScore(((int) Math.floor(result.getCourseScore() / size)));
            result.setTutorScore(((int) Math.floor(result.getTutorScore() / size)));
            result.setVenueScore(((int) Math.floor(result.getVenueScore() / size)));
        }
        return result;
    }

    @Override
    public JSONObject getJSONSurvey(Survey survey) {
        JSONObject result = new JSONObject();
        result.put(Survey.COURSE_SCORE_PROPERTY, survey.getCourseScore());
        result.put(Survey.TUTOR_SCORE_PROPERTY, survey.getTutorScore());
        result.put(Survey.VENUE_SCORE_PROPERTY, survey.getVenueScore());
        result.put(Survey.COMMENT_PROPERTY, survey.getComment());
        result.put(Survey.PUBLIC_COMMENT_PROPERTY, survey.getPublicComment());
        return result;
    }


	public String getProfilePictureUrl(Contact contact) {
		//check profile pictype at first
		if (binaryDataService.getProfilePicture(contact) != null) {
			return binaryDataService.getUrl(binaryDataService.getProfilePicture(contact));
		}
		
		if (contact.getEmailAddress() == null) {
			return "https://" + request.getServerName() + "/portal/img/ico-student-default.png";
		}
		
		//else finde avatar on gravatar servise - use special URL https://s.gravatar.com/avatar/hash?d=default_img_URL
		//the following steps should be taken to create a hash:
		//1.Trim leading and trailing whitespace from an email address
		//2.Force all characters to lower-case
		//3.md5 hash the final string
		return "https://s.gravatar.com/avatar/" 
				+ DigestUtils.md5Hex(contact.getEmailAddress().trim().toLowerCase().getBytes())
				+ "?d=https%3A%2F%2Fskillsoncourse.com.au%2Fportal%2Fimg%2Fico-student-default.png";
	}


    private Date getCurrentDate()
    {
        return DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
    }

    private Date getOldestDate()
    {
        return DateUtils.truncate(DateUtils.addMonths(new Date(), -1), Calendar.DAY_OF_MONTH);
    }

    @Override
    public UsiController getUsiController() {
        org.apache.tapestry5.services.Session session = request.getSession(false);
        UsiController usiController = (UsiController) session.getAttribute("portal.usiController");
        if (usiController == null)
        {
            ObjectContext context = cayenneService.newContext();
            usiController = new UsiController();
            usiController.setContact(context.localObject(getContact()));
            usiController.setCountryService(countryService);
            usiController.setLanguageService(languageService);
            usiController.setPreferenceController(preferenceController);
            usiController.setContactFieldHelper(new ContactFieldHelper(usiController.getPreferenceController(), enrolment));
            usiController.setUsiVerificationService(usiVerificationService);
            usiController.setMessages(MessagesImpl.forClass(Usi.class));
            usiController.getValidationResult().setMessages(usiController.getMessages());
            session.setAttribute("portal.usiController", usiController);
        }
        return usiController;
    }
}
