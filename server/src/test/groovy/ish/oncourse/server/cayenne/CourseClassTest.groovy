/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.cayenne

import ish.CayenneIshTestCase
import ish.common.types.AccountType
import ish.common.types.AttendanceType
import ish.common.types.ClassCostFlowType
import ish.common.types.ClassCostRepetitionType
import ish.common.types.CourseClassAttendanceType
import ish.common.types.DeliveryMode
import ish.common.types.DiscountType
import ish.common.types.NodeType
import ish.duplicate.ClassDuplicationRequest
import ish.math.Money
import ish.math.MoneyRounding
import ish.oncourse.entity.services.CourseClassService
import ish.oncourse.generator.DataGenerator
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.duplicate.DuplicateClassService
import ish.util.AccountUtil
import static junit.framework.Assert.assertEquals
import static junit.framework.Assert.assertNull
import static junit.framework.TestCase.assertFalse
import static junit.framework.TestCase.assertNotNull
import static junit.framework.TestCase.assertTrue
import org.apache.cayenne.Cayenne
import org.apache.cayenne.PersistenceState
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.query.ObjectIdQuery
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.Ordering
import org.apache.cayenne.query.RefreshQuery
import org.apache.cayenne.query.SelectQuery
import org.apache.cayenne.query.SortOrder
import org.apache.cxf.common.util.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import static org.junit.Assert.assertNotSame
import org.junit.Test

/**
 */
class CourseClassTest extends CayenneIshTestCase {
	
	private static final Logger logger = LogManager.getLogger()

    private  CourseClassService courseClassService = injector.getInstance(CourseClassService.class)

    @Test
    void  testNextAvailableCode() throws Exception {

		DataContext c0 = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        FieldConfigurationScheme scheme = DataGenerator.valueOf(c0).getFieldConfigurationScheme()

        createSytemUser(c0)

        Course course1 = c0.newObject(Course.class)
        course1.setCode("AAAA")
        course1.setName("name1")
        course1.setFieldConfigurationSchema(scheme)
        course1.setFeeHelpClass(Boolean.FALSE)

        Course course2 = c0.newObject(Course.class)
        course2.setCode("BBBB")
        course2.setName("name2")
        course2.setFieldConfigurationSchema(scheme)
        course2.setFeeHelpClass(Boolean.FALSE)

        List<String> codes =new ArrayList<>()
        List<CourseClass> classes = new ArrayList<>()

        // create classes with different code patterns
		List<String> formats = new ArrayList<>()
        formats.add("%d")
        formats.add("%04d")

        formats.add("XX%d")
        formats.add("XX%04d")

        formats.add("XX.%d")
        formats.add("XX.%04d")

        formats.add("XX01.%d")
        formats.add("XX01.%04d")

        for (int i=1;i<5;i++) {
			for (String format : formats) {
				String code1 = String.format(format, i)
                String code2 = String.format(format, i+2)

                CourseClass cclass1 = c0.newObject(CourseClass.class)
                cclass1.setMaximumPlaces(30)
                cclass1.setMinimumPlaces(5)
                cclass1.setCourse(course1)
                cclass1.setCode(code1)
                cclass1.setAttendanceType(CourseClassAttendanceType.NO_INFORMATION)
                cclass1.setTax(Tax.getTaxWithCode("GST", c0))
                cclass1.setIncomeAccount(AccountUtil.getDefaultBankAccount(c0, Account.class))

                classes.add(cclass1)
                codes.add(cclass1.getUniqueCode())

                CourseClass cclass2 = c0.newObject(CourseClass.class)
                cclass2.setMaximumPlaces(30)
                cclass2.setMinimumPlaces(5)
                cclass2.setCourse(course2)
                cclass2.setCode(code2)
                cclass2.setAttendanceType(CourseClassAttendanceType.NO_INFORMATION)
                cclass2.setTax(Tax.getTaxWithCode("GST", c0))
                cclass2.setIncomeAccount(AccountUtil.getDefaultBankAccount(c0, Account.class))

                classes.add(cclass2)
                codes.add(cclass2.getUniqueCode())
            }
		}

		c0.commitChanges()

        SelectQuery<CourseClass> q = SelectQuery.query(CourseClass.class)
        q.andQualifier(CourseClass.COURSE.dot(Course.CODE).in("AAAA", "BBBB"))

        //duplicate all classes 4 times
		for (int j=0;j<4;j++) {
			DataContext c1 = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()
            List<CourseClass> newClasses = new ArrayList<>()

            for (CourseClass aClass : classes) {

				// forcibly reload class to fetch just assigned id from server
				aClass.setPersistenceState(PersistenceState.HOLLOW)

                SelectQuery<CourseClass> query = SelectQuery.query(CourseClass.class)
                query.andQualifier(CourseClass.ID.eq(aClass.getId()))

                c1.performQuery(new RefreshQuery(query))
                CourseClass oldClass = (CourseClass) Cayenne.objectForQuery(c1, query)
                assertNotNull("Query for an existing class failed! : " + aClass, oldClass)

                ClassDuplicationRequest request = new ClassDuplicationRequest()
                request.setClasses(Arrays.asList(oldClass))
                request.setDaysTo(10)
                request.setCopyTutors(true)
                request.setCopyTrainingPlans(true)
                request.setApplyDiscounts(true)
                request.setCopyCosts(true)
                request.setCopySitesAndRooms(true)
                request.setCopyPayableTimeForSessions(true)
                request.setCopyNotes(false)
                DuplicateClassService service = injector.getInstance(DuplicateClassService.class)
                Long id = service.duplicateClasses(request).newIds[0]

                CourseClass newClass = ObjectSelect.query(CourseClass.class).orderBy(CourseClass.ID.getName(), SortOrder.DESCENDING).selectFirst(c1)

                assertEquals(id, newClass.id)
                assertFalse(StringUtils.isEmpty(newClass.getCode()))
                assertFalse("new code shouldn't be yet in use: " + newClass.getCode(), codes.contains(newClass.getUniqueCode()))
                newClasses.add(newClass)
                codes.add(newClass.getUniqueCode())
            }

			classes.addAll(newClasses)
            c1.commitChanges()

            course1.setPersistenceState(PersistenceState.HOLLOW)
            course2.setPersistenceState(PersistenceState.HOLLOW)
            c1.performQuery(new RefreshQuery(q))

            assertEquals("there should be as many codes as classes", course1.getCourseClasses().size() + course2.getCourseClasses().size(), codes.size())
            assertEquals("there should be as many codes as classes", classes.size(), codes.size())
            DataContext c3 = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()
            assertEquals("there should be as many codes as classes", c3.select(q).size(), codes.size())
        }

		assertEquals("there should be as many codes", 1024, codes.size())
    }

	@Test
    void testRollover() throws Exception {
		DataContext cc = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()
        createSytemUser(cc)
        CourseClass cclass = cc.newObject(CourseClass.class)

        int code = 0
        SelectQuery<CourseClass> sq = SelectQuery.query(CourseClass.class)
        sq.andQualifier(CourseClass.COURSE.dot(Course.CODE).eq("JUNIT"))

        sq.addOrdering(new Ordering(CourseClass.CODE.getName(), SortOrder.DESCENDING))

        List<CourseClass> list = cc.select(sq)
        if (list.size() > 0)
			code = Math.max(code, Integer.parseInt(list.get(0).getCode())) + 1

        Course course = cc.newObject(Course.class)
        course.setCode("CODE")
        course.setName("Course")
        course.setFieldConfigurationSchema(DataGenerator.valueOf(cc).getFieldConfigurationScheme())
        course.setFeeHelpClass(Boolean.FALSE)

        cclass.setCourse(course)
        cclass.setCode("" + code)
        cclass.setMaximumPlaces(30)
        cclass.setMinimumPlaces(5)
        cclass.setAttendanceType(CourseClassAttendanceType.NO_INFORMATION)
        cclass.setTax(Tax.getTaxWithCode("GST", cc))
        cclass.setIncomeAccount(AccountUtil.getDefaultBankAccount(cc, Account.class))

        Tutor tutor = cc.newObject(Tutor.class)
        Contact contact = cc.newObject(Contact.class)
        contact.setFirstName("Test_CourseClass")
        contact.setLastName("Test_CourseClass")
        tutor.setContact(contact)

        CourseClassTutor newRole = cc.newObject(CourseClassTutor.class)
        DefinedTutorRole definedTutorRole = cc.newObject(DefinedTutorRole.class)
        definedTutorRole.setName("definedTutorRole")
        newRole.setDefinedTutorRole(definedTutorRole)
        newRole.setTutor(tutor)
        newRole.setCourseClass(cclass)

        Discount discount = cc.newObject(Discount.class)
        discount.setName("name")
        discount.setAddByDefault(false)
        discount.setDiscountType(DiscountType.DOLLAR)
        discount.setDiscountDollar(new Money("10.00"))
        discount.setRounding(MoneyRounding.ROUNDING_10C)
        discount.setHideOnWeb(false)
        discount.setIsAvailableOnWeb(true)
        discount.setPredictedStudentsPercentage(new BigDecimal(0.1))
        discount.setMinEnrolments(1)
        discount.setMinValue(Money.ZERO)
        DiscountCourseClass dc = cc.newObject(DiscountCourseClass.class)
        dc.setCourseClass(cclass)
        dc.setDiscount(discount)

        Discount discount2 = cc.newObject(Discount.class)
        discount2.setName("name2")
        discount2.setAddByDefault(false)
        discount2.setDiscountType(DiscountType.PERCENT)
        discount2.setDiscountPercent(new BigDecimal(0.1))
        discount2.setRounding(MoneyRounding.ROUNDING_10C)
        discount2.setHideOnWeb(false)
        discount2.setIsAvailableOnWeb(true)
        discount2.setPredictedStudentsPercentage(new BigDecimal(0.1))
        discount2.setMinEnrolments(1)
        discount2.setMinValue(Money.ZERO)
        DiscountCourseClass dc2 = cc.newObject(DiscountCourseClass.class)
        dc2.setCourseClass(cclass)
        dc2.setDiscount(discount2)

        Discount discount3 = cc.newObject(Discount.class)
        discount3.setName("assignByDefault_1")
        discount3.setAddByDefault(true)
        discount3.setDiscountType(DiscountType.PERCENT)
        discount3.setDiscountPercent(new BigDecimal(0.1))
        discount3.setRounding(MoneyRounding.ROUNDING_10C)
        discount3.setHideOnWeb(false)
        discount3.setIsAvailableOnWeb(true)
        discount3.setPredictedStudentsPercentage(new BigDecimal(0.1))
        discount3.setMinEnrolments(1)
        discount3.setMinValue(Money.ZERO)

        Discount discount4 = cc.newObject(Discount.class)
        discount4.setName("assignByDefault_2")
        discount4.setAddByDefault(true)
        discount4.setDiscountType(DiscountType.PERCENT)
        discount4.setDiscountPercent(new BigDecimal(0.1))
        discount4.setRounding(MoneyRounding.ROUNDING_10C)
        discount4.setHideOnWeb(false)
        discount4.setIsAvailableOnWeb(true)
        discount4.setPredictedStudentsPercentage(new BigDecimal(0.1))
        discount4.setMinEnrolments(1)
        discount4.setMinValue(Money.ZERO)

        ClassCost cost = cc.newObject(ClassCost.class)
        ClassCost cost2 = cc.newObject(ClassCost.class)
        ClassCost cost3 = cc.newObject(ClassCost.class)

        cclass.addToCosts(cost)
        cclass.addToCosts(cost2)
        cclass.addToCosts(cost3)
        dc.setClassCost(cost2)
        dc2.setClassCost(cost3)

        assertTrue(courseClassService.getTutors(cclass).contains(tutor))
        assertTrue(cclass.getDiscounts().contains(discount))
        assertTrue(cclass.getDiscounts().contains(discount2))
        assertTrue(cclass.getCosts().contains(cost))
        cc.commitChanges()
        int daysTo = 5

        ClassDuplicationRequest request = new ClassDuplicationRequest()
        request.setClasses(Arrays.asList(cclass))
        request.setDaysTo(daysTo)
        request.setCopyTutors(false)
        request.setCopyTrainingPlans(false)
        request.setApplyDiscounts(false)
        request.setCopyCosts(false)
        request.setCopySitesAndRooms(false)
        request.setCopyPayableTimeForSessions(false)
        request.setCopyNotes(true)
        DuplicateClassService service = injector.getInstance(DuplicateClassService.class)
        service.duplicateClasses(request)

        CourseClass newClass = ObjectSelect.query(CourseClass.class).orderBy(CourseClass.ID.getName(), SortOrder.DESCENDING).selectFirst(cc)

        assertNotNull(newClass)
        assertNull(newClass.getStartDateTime())
        assertNull(newClass.getEndDateTime())
        assertEquals(0, courseClassService.getTutors(newClass).size())
        //two discounts and corresponded costs assigned by default
		assertEquals(2, newClass.getDiscounts().size())
        assertTrue(newClass.getDiscounts().contains(discount3))
        assertTrue(newClass.getDiscounts().contains(discount4))

        assertEquals(3, newClass.getCosts().size())

        for (ClassCost classCost : newClass.getCosts()) {
            if (!classCost.getDescription().equals("Student enrolment fee")) {
                assertNotNull(classCost.getDiscountCourseClass())
                assertEquals(ClassCostFlowType.DISCOUNT, classCost.getFlowType())
                assertEquals(ClassCostRepetitionType.DISCOUNT,classCost.getRepetitionType())

                if (!classCost.getDiscountCourseClass().getDiscount().equals(discount3)) {
                    assertEquals(discount4, classCost.getDiscountCourseClass().getDiscount())
                }
            } else {
                assertEquals(ClassCostFlowType.INCOME, classCost.getFlowType())
                assertEquals(ClassCostRepetitionType.PER_ENROLMENT,classCost.getRepetitionType())
            }
		}
		
		assertEquals(0, newClass.getSessions().size())

        GregorianCalendar gc = new GregorianCalendar()
        cclass.setStartDateTime(gc.getTime())
        gc.add(GregorianCalendar.DATE, 28)
        cclass.setEndDateTime(gc.getTime())
        cc.commitChanges()

        request = new ClassDuplicationRequest()
        request.setClasses(Arrays.asList(cclass))
        request.setDaysTo(daysTo)
        request.setCopyTutors(false)
        request.setCopyTrainingPlans(false)
        request.setApplyDiscounts(false)
        request.setCopyCosts(false)
        request.setCopySitesAndRooms(false)
        request.setCopyPayableTimeForSessions(false)
        request.setCopyNotes(true)
        service = injector.getInstance(DuplicateClassService.class)
        service.duplicateClasses(request)

        CourseClass newClassWithStartDateTime = ObjectSelect.query(CourseClass.class).orderBy(CourseClass.ID.getName(), SortOrder.DESCENDING).selectFirst(cc)


        GregorianCalendar gcStart = new GregorianCalendar()
        gcStart.setTime(cclass.getStartDateTime())
        gcStart.add(GregorianCalendar.DATE, 5)

        GregorianCalendar gcEnd = new GregorianCalendar()
        gcEnd.setTime(cclass.getEndDateTime())
        gcEnd.add(GregorianCalendar.DATE, 5)

        assertEquals(gcStart.getTime(), newClassWithStartDateTime.getStartDateTime())
        assertEquals(gcEnd.getTime(), newClassWithStartDateTime.getEndDateTime())
        assertEquals(0, courseClassService.getTutors(newClassWithStartDateTime).size())
        assertEquals(2, newClassWithStartDateTime.getDiscounts().size())
        assertEquals(3, newClassWithStartDateTime.getCosts().size())
        assertEquals(0, newClassWithStartDateTime.getSessions().size())
        assertNull(newClassWithStartDateTime.getRoom())

        Site site = cc.newObject(Site.class)
        site.setName("Site")
        site.setIsVirtual(false)

        Room room = cc.newObject(Room.class)
        room.setName("Room")
        room.setSeatedCapacity(20)
        room.setSite(site)
        cclass.setRoom(room)

        Tag tag = cc.newObject(Tag.class)
        tag.setName("Tag")
        tag.setIsVocabulary(true)
        tag.addToTagRequirements(tag.createTagRequirement(CourseClass.class))
        tag.setNodeType(NodeType.TAG)

        Tag childTag = cc.newObject(Tag.class)
        childTag.setName("Tag")
        childTag.setParentTag(tag)
        childTag.setNodeType(NodeType.TAG)
        cclass.addTag(childTag)

        cclass.setReportableHours(new BigDecimal(2))

        // add session
		Session session = cc.newObject(Session.class)

        session.setStartDatetime(cclass.getStartDateTime())
        session.setEndDatetime(cclass.getEndDateTime())
        session.setCourseClass(cclass)

        for (CourseClassTutor cct : session.getCourseClass().getTutorRoles()) {
			if (cct.getTutor().equals(tutor)) {
				TutorAttendance tutorAttendance = cc.newObject(TutorAttendance.class)
                tutorAttendance.setSession(session)
                tutorAttendance.setCourseClassTutor(cct)
                tutorAttendance.setAttendanceType(AttendanceType.UNMARKED)
            }
		}

		session.setRoom(room)
        Integer payAdjustmentForSessions = 20
        session.setPayAdjustment(payAdjustmentForSessions)
        cc.commitChanges()

        request = new ClassDuplicationRequest()
        request.setClasses(Arrays.asList(cclass))
        request.setDaysTo(daysTo)
        request.setCopyTutors(true)
        request.setCopyTrainingPlans(true)
        request.setApplyDiscounts(true)
        request.setCopyCosts(true)
        request.setCopySitesAndRooms(true)
        request.setCopyPayableTimeForSessions(true)
        request.setCopyNotes(false)
        service = injector.getInstance(DuplicateClassService.class)
        service.duplicateClasses(request)

        CourseClass newClassWithAll = ObjectSelect.query(CourseClass.class).orderBy(CourseClass.ID.getName(), SortOrder.DESCENDING).selectFirst(cc)

        Calendar originalStart = Calendar.getInstance(cclass.getTimeZone())
        originalStart.setTime(cclass.getStartDateTime())
        originalStart.add(GregorianCalendar.DATE, 5)

        Calendar originalEnd = Calendar.getInstance(cclass.getTimeZone())
        originalEnd.setTime(cclass.getEndDateTime())
        originalEnd.add(GregorianCalendar.DATE, 5)

        assertEquals(room, newClassWithAll.getRoom())
        assertEquals(1, newClassWithAll.getTags().size())
        assertTrue(newClassWithAll.getTags().contains(childTag))
        assertEquals(originalStart.getTime(), newClassWithAll.getStartDateTime())
        assertEquals(originalEnd.getTime(), newClassWithAll.getEndDateTime())
        assertEquals(1, courseClassService.getTutors(newClassWithAll).size())
        assertEquals(4, newClassWithAll.getDiscounts().size())
        assertEquals(cclass.getReportableHours(), newClassWithAll.getReportableHours())
        assertTrue(courseClassService.getTutors(newClassWithAll).contains(tutor))
        assertTrue(newClassWithAll.getDiscounts().contains(discount))
        assertTrue(newClassWithAll.getDiscounts().contains(discount2))
        assertTrue(newClassWithAll.getDiscounts().contains(discount3))
        assertTrue(newClassWithAll.getDiscounts().contains(discount3))
        assertEquals(1, newClassWithAll.getSessions().size())
        assertEquals(payAdjustmentForSessions, newClassWithAll.getSessions().get(0).getPayAdjustment())

        //verify unchecked payableTimeForSessions
		request = new ClassDuplicationRequest()
        request.setClasses(Arrays.asList(cclass))
        request.setDaysTo(daysTo)
        request.setCopyTutors(true)
        request.setCopyTrainingPlans(true)
        request.setApplyDiscounts(true)
        request.setCopyCosts(true)
        request.setCopySitesAndRooms(true)
        request.setCopyPayableTimeForSessions(false)
        request.setCopyNotes(false)
        service = injector.getInstance(DuplicateClassService.class)
        service.duplicateClasses(request)
        CourseClass newClassWithPayableTimeForSessions = ObjectSelect.query(CourseClass.class).orderBy(CourseClass.ID.getName(), SortOrder.DESCENDING).selectFirst(cc)

        assertEquals(new Integer(0), newClassWithPayableTimeForSessions.getSessions().get(0).getPayAdjustment())
    }

	@Test
    void testUniqueCode() {

		String courseCode = "CourseCode"
        String code = "Code"

        DataContext newContext = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()
        CourseClass cc = newContext.newObject(CourseClass.class)
        Course course = newContext.newObject(Course.class)
        course.setCode(courseCode)
        cc.setCourse(course)
        assertEquals("Checking uniqueCode ", courseCode + "-", cc.getUniqueCode())

        cc.setCode(code)
        assertEquals("Checking uniqueCode ", courseCode + "-" + code, cc.getUniqueCode())

        cc.setCourse(null)
        assertEquals("Checking uniqueCode ", "-" + code, cc.getUniqueCode())
    }

	@Test
    void testDirtyness() {

		DataContext newContext = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        new Money("55")
        Course course = newContext.newObject(Course.class)
        course.setCode("ADV")
        course.setName("courseNamwe1")
        course.setFieldConfigurationSchema(DataGenerator.valueOf(newContext).getFieldConfigurationScheme())
        course.setFeeHelpClass(Boolean.FALSE)

        Account account = newContext.newObject(Account.class)
        account.setAccountCode("accountwCode1")
        account.setDescription("descriwption1")
        account.setType(AccountType.ASSET)
        account.setIsEnabled(true)
        newContext.commitChanges()
        //commit accounts first than link to taxes (avoid exception with circular dependency on tables)
		
		Tax tax = newContext.newObject(Tax.class)
        tax.setIsGSTTaxType(Boolean.FALSE)
        tax.setTaxCode("taxCodwe1")
        tax.setReceivableFromAccount(account)
        tax.setPayableToAccount(account)

        logger.warn("1 {}", PersistenceState.persistenceStateName(account.getPersistenceState()))
        newContext.commitChanges()
        logger.warn("2 {}", PersistenceState.persistenceStateName(account.getPersistenceState()))

        CourseClass cc = newContext.newObject(CourseClass.class)
        cc.setSessionsCount(0)
        cc.setMinimumPlaces(4)
        cc.setMaximumPlaces(5)
        cc.setCode("111")
        cc.setCourse(course)
        cc.setTax(tax)
        cc.setIncomeAccount(account)
        cc.setIsActive(true)
        cc.setFeeExGst(Money.ZERO)
        cc.setTaxAdjustment(Money.ZERO)
        cc.setDeliveryMode(DeliveryMode.CLASSROOM)
        cc.setIsClassFeeApplicationOnly(true)
        cc.setSuppressAvetmissExport(false)
        cc.setAttendanceType(CourseClassAttendanceType.NO_INFORMATION)

        logger.warn("3 {}", PersistenceState.persistenceStateName(cc.getIncomeAccount().getPersistenceState()))
        newContext.commitChanges()
        logger.warn("4 {}", PersistenceState.persistenceStateName(cc.getIncomeAccount().getPersistenceState()))

        logger.warn("5 {}", PersistenceState.persistenceStateName(cc.getIncomeAccount().getPersistenceState()))
        cc.setPersistenceState(PersistenceState.HOLLOW)
        logger.warn("6 {}", PersistenceState.persistenceStateName(cc.getIncomeAccount().getPersistenceState()))
    }

	@Test
    void testStart_EndDateAndSessionCount() throws Exception {
		DataContext newContext = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        Course course = newContext.newObject(Course.class)
        course.setCode("courseCode")
        course.setName("courseName")
        course.setFieldConfigurationSchema(DataGenerator.valueOf(newContext).getFieldConfigurationScheme())
        course.setFeeHelpClass(Boolean.FALSE)

        Account account = newContext.newObject(Account.class)
        account.setAccountCode("accountCode")
        account.setDescription("description")
        account.setType(AccountType.INCOME)
        account.setIsEnabled(true)
        //commit accounts first than link to taxes (avoid exception with circular dependency on tables)
		newContext.commitChanges()

        Tax tax = newContext.newObject(Tax.class)
        tax.setIsGSTTaxType(Boolean.FALSE)
        tax.setTaxCode("taxCode")
        tax.setReceivableFromAccount(account)
        tax.setPayableToAccount(account)

        CourseClass cc = newContext.newObject(CourseClass.class)
        cc.setSessionsCount(0)
        cc.setMinimumPlaces(4)
        cc.setMaximumPlaces(5)
        cc.setCode("testCourse")
        cc.setCourse(course)
        cc.setTax(tax)
        cc.setIncomeAccount(account)
        cc.setIsActive(true)
        cc.setFeeExGst(Money.ZERO)
        cc.setTaxAdjustment(Money.ZERO)
        cc.setDeliveryMode(DeliveryMode.CLASSROOM)
        cc.setIsClassFeeApplicationOnly(true)
        cc.setSuppressAvetmissExport(false)
        cc.setAttendanceType(CourseClassAttendanceType.NO_INFORMATION)

        GregorianCalendar gc = new GregorianCalendar()
        Date startTimeForFirstSession = gc.getTime()

        GregorianCalendar gc_end = new GregorianCalendar()
        gc_end.add(GregorianCalendar.DATE, 28)
        Date endTimeForFirstSession = gc_end.getTime()

        assertNull("Checking startDateTime for CourseClasse ", cc.getStartDateTime())
        assertNull("Checking endDateTime for CourseClasse ", cc.getEndDateTime())

        GregorianCalendar gc1 = new GregorianCalendar()
        gc1.add(GregorianCalendar.DATE, 1)

        Date classDate = gc1.getTime()
        cc.setStartDateTime(classDate)

        Session session = newContext.newObject(Session.class)
        session.setStartDatetime(startTimeForFirstSession)
        session.setEndDatetime(endTimeForFirstSession)

        session.setCourseClass(cc)
        session.setPayAdjustment(4)

        assertEquals("Checking startDateTime for CourseClasse ", gc1.getTime(), cc.getStartDateTime())
        assertNull("Checking endDateTime for CourseClasse ", cc.getEndDateTime())
        assertEquals("Checking sessionCount for CourseClasse ", new Integer(0), cc.getSessionsCount())

        newContext.commitChanges()

        assertEquals("Checking startDateTime for CourseClasse ", session.getStartDatetime(), cc.getStartDateTime())
        assertEquals("Checking endDateTime for CourseClasse ", session.getEndDatetime(), cc.getEndDateTime())
        assertEquals("Checking sessionCount for CourseClasse ", new Integer(1), cc.getSessionsCount())

        // reload committed class from db since different dbs handle dates differently and actually stored
		// value may differ from what we tried to store (e.g. MySQL timestamp doesn't store milliseconds
		// and therefore returns date truncated to seconds)
		cc = (CourseClass) Cayenne.objectForQuery(newContext, new ObjectIdQuery(cc.getObjectId()))

        Date createOn = cc.getCreatedOn()
        Date modifiedOn = cc.getModifiedOn()
        assertNotNull("Checking createdOn ", cc.getCreatedOn())
        assertNotNull("Checking modifiedOn ", cc.getModifiedOn())
// assertEquals(cc.getCreatedOn(), cc.getModifiedOn());

		// if set new StartDateTime for CourseClass (newDateTime.after(startTimeForFirstSession))
		// after commit the start time for the first session and class should be the same

		GregorianCalendar gc2 = new GregorianCalendar()
        gc2.add(GregorianCalendar.DATE, 1)
        Date newDateTime = gc2.getTime()
        cc.setStartDateTime(newDateTime)

        newContext.commitChanges()

        assertTrue(newDateTime.after(startTimeForFirstSession))

        assertEquals("Checking startDateTime for CourseClasse ", session.getStartDatetime(), cc.getStartDateTime())
        assertEquals("Checking endDateTime for CourseClasse ", session.getEndDatetime(), cc.getEndDateTime())

        // if set new StartDateTime for CourseClass (newDateTime2.before(startTimeForFirstSession))
		// after commit the start time for the first session and class should be the same

		GregorianCalendar gc3 = new GregorianCalendar()
        gc3.add(GregorianCalendar.DATE, -3)
        Date newDateTime2 = gc3.getTime()

        cc.setStartDateTime(newDateTime2)

        Thread.sleep(1000L)

        newContext.commitChanges()

        assertTrue(newDateTime2.before(startTimeForFirstSession))
        assertEquals("Checking startDateTime for CourseClasse ", session.getStartDatetime(), cc.getStartDateTime())
        assertEquals("Checking endDateTime for CourseClasse ", session.getEndDatetime(), cc.getEndDateTime())

        // test for update correct update fields createdOn and modifiedOn
		assertEquals("Checking createdOn ", createOn, cc.getCreatedOn())
        assertNotSame("Checking modifiedOn ", modifiedOn, cc.getModifiedOn())
        assertNotSame(cc.getCreatedOn(), cc.getModifiedOn())
        assertTrue(cc.getCreatedOn().before(cc.getModifiedOn()))

        // add new session with startDateTime after startDateTime in CourseClasse
		GregorianCalendar gc4 = new GregorianCalendar()
        gc4.add(GregorianCalendar.DATE, 2)

        Date startTimeForSecondSession = gc4.getTime()
        assertTrue(startTimeForSecondSession.after(cc.getStartDateTime()))

        Session secondSession = newContext.newObject(Session.class)
        secondSession.setStartDatetime(startTimeForSecondSession)
        secondSession.setEndDatetime(endTimeForFirstSession)

        secondSession.setCourseClass(cc)
        secondSession.setPayAdjustment(4)

        assertEquals("Checking sessionCount for CourseClasse ", new Integer(1), cc.getSessionsCount())

        newContext.commitChanges()

        assertTrue(startTimeForSecondSession.after(startTimeForFirstSession))
        assertEquals("Checking startDateTime for CourseClasse ", session.getStartDatetime(), cc.getStartDateTime())
        assertEquals("Checking endDateTime for CourseClasse ", session.getEndDatetime(), cc.getEndDateTime())
        assertEquals("Checking sessionCount for CourseClasse ", new Integer(2), cc.getSessionsCount())

        // add new session with startDateTime before startDateTime in CourseClasse
		gc.add(GregorianCalendar.DATE, -2)
        Date startTimeForNextSession = gc.getTime()
        assertTrue(startTimeForNextSession.before(cc.getStartDateTime()))

        Session nextSession = newContext.newObject(Session.class)
        nextSession.setStartDatetime(startTimeForNextSession)
        nextSession.setEndDatetime(endTimeForFirstSession)

        nextSession.setCourseClass(cc)
        nextSession.setPayAdjustment(4)

        newContext.commitChanges()

        assertTrue(startTimeForNextSession.before(startTimeForFirstSession))
        assertEquals("Checking startDateTime for CourseClasse ", nextSession.getStartDatetime(), cc.getStartDateTime())
        assertEquals("Checking endDateTime for CourseClasse ", nextSession.getEndDatetime(), cc.getEndDateTime())
        assertEquals("Checking sessionCount for CourseClasse ", new Integer(3), cc.getSessionsCount())

        // remove 3th session
		newContext.deleteObjects(nextSession)
        newContext.commitChanges()

        assertEquals("Checking startDateTime for CourseClasse ", session.getStartDatetime(), cc.getStartDateTime())
        assertEquals("Checking endDateTime for CourseClasse ", session.getEndDatetime(), cc.getEndDateTime())
        assertEquals("Checking sessionCount for CourseClasse ", new Integer(2), cc.getSessionsCount())

        // remove second and first session
		newContext.deleteObjects(secondSession)
        newContext.commitChanges()

        newContext.deleteObjects(session)
        newContext.commitChanges()

        assertEquals("Checking startDateTime for CourseClasse ", session.getStartDatetime(), cc.getStartDateTime())
        assertEquals("Checking endDateTime for CourseClasse ", session.getEndDatetime(), cc.getEndDateTime())
        assertEquals("Checking sessionCount for CourseClasse ", new Integer(0), cc.getSessionsCount())

    }

	@Test
    void testFee() {
		DataContext newContext = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        Money money = new Money("55")
        Money feeExGst
        Money feeGST
        Money feeIncGst
        Money deposit

        Course course = newContext.newObject(Course.class)
        course.setCode("ABSEDV")
        course.setName("courseName1")
        course.setFieldConfigurationSchema(DataGenerator.valueOf(newContext).getFieldConfigurationScheme())
        course.setFeeHelpClass(Boolean.FALSE)

        Account account = newContext.newObject(Account.class)
        account.setAccountCode("accountCode1")
        account.setDescription("description1")
        account.setType(AccountType.ASSET)
        account.setIsEnabled(true)
        //commit accounts first than link to taxes (avoid exception with circular dependency on tables)
		newContext.commitChanges()

        Tax tax = newContext.newObject(Tax.class)
        tax.setIsGSTTaxType(Boolean.FALSE)
        tax.setTaxCode("taxCode1")
        tax.setReceivableFromAccount(account)
        tax.setPayableToAccount(account)

        CourseClass cc = newContext.newObject(CourseClass.class)
        cc.setSessionsCount(0)
        cc.setMinimumPlaces(4)
        cc.setMaximumPlaces(5)
        cc.setCode("testCourse1")
        cc.setCourse(course)
        cc.setTax(tax)
        cc.setIncomeAccount(account)
        cc.setIsActive(true)
        cc.setFeeExGst(Money.ZERO)
        cc.setTaxAdjustment(Money.ZERO)
        cc.setDeliveryMode(DeliveryMode.CLASSROOM)
        cc.setIsClassFeeApplicationOnly(true)
        cc.setSuppressAvetmissExport(false)
        cc.setAttendanceType(CourseClassAttendanceType.NO_INFORMATION)

        ClassCost classCost = newContext.newObject(ClassCost.class)
        classCost.setFlowType(ClassCostFlowType.INCOME)
        classCost.setInvoiceToStudent(true)
        classCost.setPayableOnEnrolment(false)
        classCost.setPerUnitAmountExTax(money)

        cc.addToCosts(classCost)

        assertEquals("Checking FeeExGst ", Money.ZERO, cc.getFeeExGst())
        assertEquals("Checking FeeGST ", Money.ZERO, cc.getFeeGST())
        assertEquals("Checking FeeIncGst ", Money.ZERO, cc.getFeeIncGst())

        feeExGst = money
        feeGST = Money.ZERO
        feeIncGst = feeExGst
        deposit = Money.ZERO

        newContext.commitChanges()

        assertEquals("Checking FeeExGst ", feeExGst, cc.getFeeExGst())
        assertEquals("Checking FeeGST ", feeGST, cc.getFeeGST())
        assertEquals("Checking FeeIncGst ", feeIncGst, cc.getFeeIncGst())
        assertEquals("Checking deposit ", deposit, cc.getDeposit())

        Money money2 = new Money("40")

        ClassCost classCost2 = newContext.newObject(ClassCost.class)
        classCost2.setFlowType(ClassCostFlowType.INCOME)
        classCost2.setInvoiceToStudent(true)
        classCost2.setPayableOnEnrolment(true)
        classCost2.setPerUnitAmountExTax(money2)

        cc.addToCosts(classCost2)

        feeExGst = money.add(money2)
        feeGST = Money.ZERO
        feeIncGst = feeExGst
        deposit = money2

        newContext.commitChanges()

        assertEquals("Checking deposit ", deposit, cc.getDeposit())
        assertEquals("Checking FeeExGst ", feeExGst, cc.getFeeExGst())
        assertEquals("Checking FeeGST ", feeGST, cc.getFeeGST())
        assertEquals("Checking FeeIncGst ", feeIncGst, cc.getFeeIncGst())

        BigDecimal rate = new BigDecimal(0.20)
        cc.getTax().setRate(rate)

        newContext.commitChanges()

        feeGST = feeExGst.multiply(rate)
        feeIncGst = feeExGst.multiply(rate.add(BigDecimal.ONE))

        assertEquals("Checking FeeGST ", feeGST, cc.getFeeGST())
        assertEquals("Checking FeeIncGst ", feeIncGst, cc.getFeeIncGst())

    }

    private SystemUser createSytemUser(DataContext context) {
        SystemUser user = context.newObject(SystemUser.class)
        user.firstName = 'a'
        user.lastName = 'b'
        user.password = 'c'
        user.login = 'd'
        user.email = 'e'

        Site defaultAdminCentre = context.newObject(Site.class)
        defaultAdminCentre.name = 'f'
        defaultAdminCentre.isVirtual = false

        user.defaultAdministrationCentre = defaultAdminCentre

        user
    }

}
