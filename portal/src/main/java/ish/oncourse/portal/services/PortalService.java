package ish.oncourse.portal.services;

import ish.common.types.*;
import ish.math.Money;
import ish.oncourse.cache.ICacheFactory;
import ish.oncourse.cache.ICacheProvider;
import ish.oncourse.cache.caffeine.CaffeineFactory;
import ish.oncourse.model.*;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.courseclass.CourseClassFilter;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.util.FormatUtils;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.BaseDataObject;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.*;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static ish.oncourse.model.auto._ContactRelation.RELATION_TYPE;
import static ish.oncourse.model.auto._ContactRelationType.DELEGATED_ACCESS_TO_CONTACT;
import static ish.oncourse.portal.services.PortalUtils.*;

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
    private ICookiesService cookiesService;

    @Inject
    private CacheManager cacheManager;
    
    @Inject
    private Request request;

    private ICacheProvider cacheProvider;
    private ICacheFactory<String, String> cacheFactory;
    private Configuration<String, String> cacheConfig;

    public PortalService(@Inject ICacheProvider provider) {
        cacheProvider = provider;
        this.cacheFactory = cacheProvider.createFactory(String.class, String.class);
        long cacheSize = new Long(System.getProperty("cache.tapestry.size", "10000"));
        this.cacheConfig = CaffeineFactory.createDefaultConfig(String.class, String.class, cacheSize);
    }

    private static final String NOTIFICATION_CACHE =  "notification";
    private static final String NOTIFICATION_CACHE_KEY =  "%s-%s";
    private static final String NOTIFICATION_CACHE_VALUE =  "%d/%d/%d";
    private static final String NOTIFICATION_CACHE_DELIMITER =  "/";

    @Override
    public Contact getContact() {
        return authenticationService.getSelectedUser();
    }

    @Override
    public Contact getAuthenticatedUser() {
        return authenticationService.getUser();
    }

    public Notification getNotification() {
        Contact contact = getContact();
        Date lastLoginTime = getLastLoginTime();
        String cacheKey = String.format(NOTIFICATION_CACHE_KEY, contact.getId().toString(), lastLoginTime.toString());
        int historyCount, resultsCount, resourcesCount;

        Supplier<String> get = () -> {
            int history = getNewPaymentsCount() + getNewInvoicesCount() + getNewEnrolmentsCount();
            int results = getNewResultsCount();
            int resources = getNewResourcesCount();
            return String.format(NOTIFICATION_CACHE_VALUE, history, results, resources);
        };

        String value = getCachedValue(cacheKey, get);

        String[] values = value.split(NOTIFICATION_CACHE_DELIMITER);
        historyCount = Integer.parseInt(values[0]);
        resultsCount = Integer.parseInt(values[1]);
        resourcesCount = Integer.parseInt(values[2]);

        return new Notification(historyCount, resultsCount, resourcesCount);
    }

    /**
     * Create cache create if absent, use <code>appKey</code> as group name,
     * appkey contains site key so we don't need to use host name in MultiKey
     * appkey can been null for SiteNotFound page
     */
    private String getCachedValue(String key, Supplier<String> get) {
        try {
            Cache<String, String> cache = cacheFactory.createIfAbsent(NOTIFICATION_CACHE, cacheConfig);
            String v = cache.get(key);
            if (v == null) {
                v = get.get();
                cache.put(key, v);
            }
            return v;
        } catch (Exception e) {
            logger.error("Exception appeared during reading cached value. cacheGroup {}, key: {}", NOTIFICATION_CACHE, key);
            logger.catching(e);
            return get.get();
        }
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
    public JSONObject getCalendarEvents(Date month, boolean showTeamEvents) {
        List<Session> sessions;

        if (showTeamEvents) {
            List<Contact> contacts = new ArrayList<>(getChildContacts());
            contacts.remove(getContact());
            Set<Session> uniqSessions = new HashSet<>();
            for (Contact contact : contacts) {
                uniqSessions.addAll(courseClassService.getContactSessions(contact, month));
            }
            sessions = new ArrayList<>(uniqSessions);
        } else {
            sessions = courseClassService.getContactSessions(getContact(), month);
        }

        Session.START_DATE.asc().orderList(sessions);

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
            events.append(String.format("<span id=\"sessionId-%d\" value=\"%d\" class=\"event\"/>", session.getId(), session.getCourseClass().getId()));
        }

        return events.toString();
    }


    public boolean isApproved(CourseClass courseClass) {

        List<TutorRole> tutorRoles = ObjectSelect.query(TutorRole.class).where(TutorRole.TUTOR.eq(getContact().getTutor()))
                .and(TutorRole.COURSE_CLASS.eq(courseClass)).select(cayenneService.newContext());

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

            List<CourseClass> courseClasses = ObjectSelect.query(CourseClass.class, getStudentClassesExpression()).
					cacheStrategy(QueryCacheStrategy.SHARED_CACHE).
					cacheGroup(CourseClass.class.getSimpleName()).
                    prefetch(CourseClass.SESSIONS.disjoint()).
                    select(cayenneService.newContext());

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

    public List<CourseClass> getTutorCourseClasses(Contact contact, CourseClassFilter filter) {
        if (contact.getTutor() != null) {

            List<CourseClass> courseClasses = ObjectSelect.query(CourseClass.class, getTutorClassesExpression()).
					cacheStrategy(QueryCacheStrategy.SHARED_CACHE).
					cacheGroup(CourseClass.class.getSimpleName()).
                    prefetch(CourseClass.SESSIONS.disjoint()).
                    select(cayenneService.newContext());

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
            if (IsCurrentTutorClass.valueOf(courseClass, date).is()) {
                current.add(courseClass);
            }
        }

        return current;
    }


    private List<CourseClass> getUnconfirmedTutorClasses(List<CourseClass> courseClasses, Contact contact) {

        List<CourseClass> unconfirmed = new ArrayList<>();

        Date date = getCurrentDate();

        for (CourseClass courseClass : courseClasses) {
            if (IsCurrentTutorClass.valueOf(courseClass, date).is()) {
                TutorRole tutorRole = ObjectSelect.query(TutorRole.class).where(TutorRole.TUTOR.eq(contact.getTutor()))
                        .and(TutorRole.COURSE_CLASS.eq(courseClass))
                        .and(TutorRole.IS_CONFIRMED.isFalse()).selectFirst(cayenneService.newContext());

                if (tutorRole != null) {
                    unconfirmed.add(courseClass);
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
    public List<Document> getStudentAndTutorCommonResources(List<CourseClass> allowedClasses) {
        if (!allowedClasses.isEmpty() && (getContact().getTutor() != null || getContact().getStudent() != null)) {
            ObjectContext sharedContext = cayenneService.newContext();
            
            List<Long> allowedCourseIds = allowedClasses.stream().map(cc -> cc.getCourse().getId()).distinct().collect(Collectors.toList());
            Expression expr =Document.BINARY_INFO_RELATIONS.outer().dot(BinaryInfoRelation.ENTITY_IDENTIFIER).eq(Course.class.getSimpleName())
                        .andExp(Document.BINARY_INFO_RELATIONS.outer().dot(BinaryInfoRelation.ENTITY_WILLOW_ID).in(allowedCourseIds));

            return ObjectSelect.query(Document.class).where(expr)
                    .select(sharedContext);
        } else {
            return Collections.emptyList();
        }
    }
    public List<Enrolment> getEnrolments() {
        Student student = getContact().getStudent();
        if (student != null) {
            ObjectContext sharedContext = cayenneService.newContext();
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
        ObjectContext sharedContext = cayenneService.newContext();
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
        ObjectContext sharedContext = cayenneService.newContext();

        Enrolment enrolment = ObjectSelect.query(Enrolment.class).where(Enrolment.STUDENT.eq(student)).and(Enrolment.COURSE_CLASS.eq(courseClass)).and(Enrolment.STATUS.eq(EnrolmentStatus.SUCCESS)).selectFirst(sharedContext);
        // only students with active enrolments can view class attachments
        if (enrolment == null) {
            return Collections.emptyList();
        }

        Expression courseQualifier = Document.BINARY_INFO_RELATIONS.dot(BinaryInfoRelation.ENTITY_IDENTIFIER).eq(Course.class.getSimpleName())
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
        ObjectContext sharedContext = cayenneService.newContext();

        return ObjectSelect.query(Document.class).where(Document.WEB_VISIBILITY.in(AttachmentInfoVisibility.TUTORS, AttachmentInfoVisibility.STUDENTS))
                .and(Document.IS_REMOVED.isFalse())
                .and((Document.BINARY_INFO_RELATIONS.dot(BinaryInfoRelation.ENTITY_IDENTIFIER).eq(Course.class.getSimpleName())
                        .andExp(Document.BINARY_INFO_RELATIONS.dot(BinaryInfoRelation.ENTITY_WILLOW_ID).eq(courseClass.getCourse().getId())))
                        .orExp(Document.BINARY_INFO_RELATIONS.dot(BinaryInfoRelation.ENTITY_IDENTIFIER).eq(CourseClass.class.getSimpleName())
                                .andExp(Document.BINARY_INFO_RELATIONS.dot(BinaryInfoRelation.ENTITY_WILLOW_ID).eq(courseClass.getId())))).select(sharedContext);
    }

    public List<Document> getResources() {
        List<CourseClass> courseClasses = getContactCourseClasses(CourseClassFilter.CURRENT);

        List<Document> resources = new ArrayList<>(getStudentAndTutorCommonResources(courseClasses));

        List<PCourseClass> courseClassesSessions = fillCourseClassSessions(CourseClassFilter.CURRENT);

        courseClassesSessions.forEach(session -> {
            List<Document> list = getResourcesBy(session.getCourseClass());
            list = list.stream()
                    .filter(doc -> !resources.stream()
                                            .map(Document::getId)
                                            .collect(Collectors.toList())
                                            .contains(doc.getId()))
                    .collect(Collectors.toList());
            resources.addAll(list);
        });



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

        ObjectContext sharedContext = cayenneService.newContext();
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

        ObjectContext sharedContext = cayenneService.newContext();
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

        Ordering.orderList(sessions, Collections.singletonList(new Ordering(Session.START_DATE_PROPERTY, SortOrder.ASCENDING)));
        Expression ex = ExpressionFactory.greaterExp(Session.START_DATE_PROPERTY, (filter == CourseClassFilter.CURRENT) ? getCurrentDate() : getOldestDate());

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
    public List<PaymentIn> getPaymentIns() {

        Contact contact = getContact();

        ObjectContext sharedContext = cayenneService.newContext();

        return ObjectSelect.query(PaymentIn.class).where(PaymentIn.STATUS.eq(PaymentStatus.SUCCESS))
                .and(PaymentIn.AMOUNT.ne(Money.ZERO))
                .and(PaymentIn.CONTACT.eq(contact)).select(sharedContext);
    }

    @Override
    public List<PaymentOut> getPaymentOuts() {

        Contact contact = getContact();

        ObjectContext sharedContext = cayenneService.newContext();

        return ObjectSelect.query(PaymentOut.class).where(PaymentOut.STATUS.eq(PaymentStatus.SUCCESS))
                .and(PaymentOut.TOTAL_AMOUNT.ne(Money.ZERO))
                .and(PaymentOut.CONTACT.eq(contact)).select(sharedContext);
    }

    public int getNewPaymentsCount() {
        Contact contact = getContact();
        Date lastLoginTime = getLastLoginTime();

        ObjectContext sharedContext = cayenneService.newContext();

        return ObjectSelect.query(PaymentIn.class).where(PaymentIn.CONTACT.eq(contact)
                .andExp(PaymentIn.AMOUNT.gt(Money.ZERO))
                .andExp(PaymentIn.MODIFIED.gte(lastLoginTime))).select(sharedContext).size();
    }

    public int getNewInvoicesCount() {
        Contact contact = getContact();
        Date lastLoginTime = getLastLoginTime();

        ObjectContext sharedContext = cayenneService.newContext();

        return ObjectSelect.query(Invoice.class).where(Invoice.CONTACT.eq(contact))
                .and(Invoice.MODIFIED.gte(lastLoginTime)).select(sharedContext).size();
    }

    public int getNewEnrolmentsCount() {
        Contact contact = getContact();
        Date lastLoginTime = getLastLoginTime();

        ObjectContext sharedContext = cayenneService.newContext();

        return ObjectSelect.query(Enrolment.class).where(Enrolment.STUDENT.eq(contact.getStudent()))
                .and(Enrolment.MODIFIED.gte(lastLoginTime)).select(sharedContext).size();
    }


    public boolean isNew(BaseDataObject object) {

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
        ArrayList<Contact> result = new ArrayList<>();
        ObjectContext context = cayenneService.newContext();
        Contact parent = Cayenne.objectForPK(context, Contact.class, getAuthenticatedUser().getId());
        List<ContactRelation> contactRelations = parent.getToContacts();
        contactRelations = RELATION_TYPE.dot(DELEGATED_ACCESS_TO_CONTACT).eq(true).filterObjects(contactRelations);

        List<Ordering> orderings = new ArrayList<>();
        orderings.add(ContactRelation.TO_CONTACT.dot(Contact.GIVEN_NAME).ascInsensitive());
        orderings.add(ContactRelation.TO_CONTACT.dot(Contact.FAMILY_NAME).ascInsensitive());
        Ordering.orderList(contactRelations, orderings);

        result.add(getAuthenticatedUser());
        for (ContactRelation contactRelation : contactRelations) {
            result.add(contactRelation.getToContact());
        }
        return Collections.unmodifiableList(result);
    }


    public boolean isSelectedContact(Contact contact) {
        return getContact().getId().equals(contact.getId());
    }

    public void selectContact(Contact contact) {
        authenticationService.selectUser(contact);
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
        return ObjectSelect.query(Enrolment.class)
                .where(Enrolment.STUDENT.eq(student))
                .and(Enrolment.COURSE_CLASS.eq(courseClass))
                .and(Enrolment.STATUS.in(Enrolment.VALID_ENROLMENTS))
                .selectFirst(student.getObjectContext());
    }

    public boolean isTutorFor(CourseClass courseClass) {
        courseClass = getContact().getTutor().getObjectContext().localObject(courseClass);
        Expression exp = ExpressionFactory.matchExp(TutorRole.TUTOR_PROPERTY, getContact().getTutor());
        return !exp.filterObjects(courseClass.getTutorRoles()).isEmpty();
    }


    private Survey getSurveyBy(Student student, CourseClass courseClass) {
        Expression surveyExp = ExpressionFactory.matchExp(Survey.ENROLMENT_PROPERTY + "." + Enrolment.STUDENT_PROPERTY, student)
                .andExp(ExpressionFactory.matchExp(Survey.ENROLMENT_PROPERTY + "." + Enrolment.COURSE_CLASS_PROPERTY, courseClass));
        SelectQuery query = new SelectQuery(Survey.class, surveyExp);

        return (Survey) Cayenne.objectForQuery(student.getObjectContext(), query);
    }

    public Survey getAverageSurveyFor(CourseClass courseClass) {
        ObjectContext context = cayenneService.newNonReplicatingContext();
        Expression surveyExp = ExpressionFactory.matchExp(Survey.ENROLMENT_PROPERTY + "." + Enrolment.COURSE_CLASS_PROPERTY, courseClass);
        SelectQuery query = new SelectQuery(Survey.class, surveyExp);
        List<Survey> surveys = context.performQuery(query);

        Survey result = new Survey();
        result.setCourseScore(0);
        result.setVenueScore(0);
        result.setTutorScore(0);
        result.setNetPromoterScore(0);
        for (Survey survey : surveys) {
            result.setCourseScore(result.getCourseScore() + survey.getCourseScore());
            result.setVenueScore(result.getVenueScore() + survey.getVenueScore());
            result.setTutorScore(result.getTutorScore() + survey.getTutorScore());
            result.setNetPromoterScore(result.getNetPromoterScore() + survey.getNetPromoterScore());
        }

        int size = surveys.size();
        if (size != 0) {
            result.setCourseScore(((int) Math.floor(result.getCourseScore() / size)));
            result.setTutorScore(((int) Math.floor(result.getTutorScore() / size)));
            result.setVenueScore(((int) Math.floor(result.getVenueScore() / size)));
            result.setNetPromoterScore(((int) Math.floor(result.getNetPromoterScore() / size)));
        }
        result.setVisibility(SurveyVisibility.TESTIMONIAL);
        return result;
    }


    public String getProfilePictureUrl(Contact contact) {
        //check profile pictype at first
        if (binaryDataService.getProfilePicture(contact) != null) {
            return binaryDataService.getUrl(binaryDataService.getProfilePicture(contact));
        }

        if (contact.getEmailAddress() == null) {
            return "https://" + request.getServerName() + "/portal/img/student-default.png";
        }

        //else finde avatar on gravatar servise - use special URL https://s.gravatar.com/avatar/hash?d=default_img_URL
        //the following steps should be taken to create a hash:
        //1.Trim leading and trailing whitespace from an email address
        //2.Force all characters to lower-case
        //3.md5 hash the final string
        return "https://s.gravatar.com/avatar/"
                + DigestUtils.md5Hex(contact.getEmailAddress().trim().toLowerCase().getBytes())
                + "?d=https%3A%2F%2Fskillsoncourse.com.au%2Fportal%2Fimg%2Fstudent-default.png";
    }


    private Date getCurrentDate() {
        return DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
    }

    private Date getOldestDate() {
        return DateUtils.truncate(DateUtils.addMonths(new Date(), -1), Calendar.DAY_OF_MONTH);
    }


    public List<Session> getContactSessionsFrom(Date start, Contact contact) {

        ObjectSelect<Session> query = ObjectSelect.query(Session.class).where(Session.START_DATE.gte(start)).and(Session.COURSE_CLASS.dot(CourseClass.CANCELLED).isFalse());

        if (contact.getTutor() == null && contact.getStudent() == null) {
            return Collections.EMPTY_LIST;
        }

        Expression contactExp = null;

        if (contact.getTutor() != null) {
            contactExp = Session.SESSION_TUTORS.outer().dot(SessionTutor.TUTOR).eq(contact.getTutor());
        }
        if (contact.getStudent() != null) {
            Expression studentExp = Session.COURSE_CLASS.outer().dot(CourseClass.ENROLMENTS).outer().dot(Enrolment.STUDENT).eq(contact.getStudent())
                    .andExp(Session.COURSE_CLASS.outer().dot(CourseClass.ENROLMENTS).outer().dot(Enrolment.STATUS).eq(EnrolmentStatus.SUCCESS));

            if (contactExp == null) {
                contactExp = studentExp;
            } else {
                contactExp = contactExp.orExp(studentExp);
            }
        }
        query = query.and(contactExp);

        return query.orderBy(Session.START_DATE.asc())
                .prefetch(Session.COURSE_CLASS.disjoint())
                .select(cayenneService.newContext());
    }


    public static class IsCurrentTutorClass {
        private CourseClass courseClass;
        private Date date;

        public boolean is() {
            return courseClass.getIsDistantLearningCourse() ||
                    (!courseClass.getSessions().isEmpty() && courseClass.getEndDate().after(date)) ||
                    courseClass.getSessions().isEmpty();
        }

        public static IsCurrentTutorClass valueOf(CourseClass courseClass, Date date) {
            IsCurrentTutorClass result = new IsCurrentTutorClass();
            result.courseClass = courseClass;
            result.date = date;
            return result;
        }

    }

}
