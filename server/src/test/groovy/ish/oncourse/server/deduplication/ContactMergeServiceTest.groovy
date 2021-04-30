package ish.oncourse.server.deduplication

import ish.CayenneIshTestCase
import ish.common.types.Gender
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.v1.model.MergeLineDTO
import ish.oncourse.server.cayenne.*
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.Assert.*

class ContactMergeServiceTest extends CayenneIshTestCase {

    private ContactMergeService contactMergeService
    private ICayenneService cayenneService

    @BeforeEach
    void setup() {
        wipeTables()
        contactMergeService = injector.getInstance(ContactMergeService)
        cayenneService = injector.getInstance(ICayenneService.class)

        InputStream st = ContactMergeServiceTest.class.getClassLoader().getResourceAsStream("ish/oncourse/server/deduplication/dedupeContactDataSet.xml")
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st)

        executeDatabaseOperation(dataSet)
        injector.getInstance(PreferenceController.class).setReplicationEnabled(true)
        super.setup()
    }

    @Test
    void testGetDiffData() {
        ObjectContext context = cayenneService.newContext
        Contact contactA = ObjectSelect.query(Contact).where(Contact.ID.eq(1L)).selectFirst(context)
        Contact contactB = ObjectSelect.query(Contact).where(Contact.ID.eq(2L)).selectFirst(context)
        List<MergeLineDTO> mergeLines = contactMergeService.getDifferenceAttributes(contactA, contactB)
        assertEquals("[class MergeLineDTO {\n" +
                "    key: Contact.birthDate\n" +
                "    label: Birth date\n" +
                "    a: 1991-01-01\n" +
                "    b: 1990-12-31\n" +
                "}, class MergeLineDTO {\n" +
                "    key: Contact.email\n" +
                "    label: Email\n" +
                "    a: test1@test.te\n" +
                "    b: test2@test.te\n" +
                "}, class MergeLineDTO {\n" +
                "    key: Contact.firstName\n" +
                "    label: First name\n" +
                "    a: fname1\n" +
                "    b: fname2\n" +
                "}, class MergeLineDTO {\n" +
                "    key: Contact.gender\n" +
                "    label: Gender\n" +
                "    a: Female\n" +
                "    b: Male\n" +
                "}, class MergeLineDTO {\n" +
                "    key: Contact.homePhone\n" +
                "    label: Home phone\n" +
                "    a: 331110000\n" +
                "    b: 000011133\n" +
                "}, class MergeLineDTO {\n" +
                "    key: Contact.lastName\n" +
                "    label: Last name\n" +
                "    a: lname1\n" +
                "    b: lname2\n" +
                "}, class MergeLineDTO {\n" +
                "    key: Contact.middleName\n" +
                "    label: Middle name\n" +
                "    a: mname1\n" +
                "    b: mname2\n" +
                "}, class MergeLineDTO {\n" +
                "    key: Contact.mobilePhone\n" +
                "    label: Mobile phone\n" +
                "    a: 331110000\n" +
                "    b: 7654332131\n" +
                "}, class MergeLineDTO {\n" +
                "    key: Contact.postcode\n" +
                "    label: Postcode\n" +
                "    a: 1001\n" +
                "    b: 0110\n" +
                "}, class MergeLineDTO {\n" +
                "    key: Contact.state\n" +
                "    label: State\n" +
                "    a: NSW\n" +
                "    b: WSN\n" +
                "}, class MergeLineDTO {\n" +
                "    key: Contact.street\n" +
                "    label: Street\n" +
                "    a: Main\n" +
                "    b: Down\n" +
                "}, class MergeLineDTO {\n" +
                "    key: Contact.suburb\n" +
                "    label: Suburb\n" +
                "    a: Sydney\n" +
                "    b: Melbourne\n" +
                "}, class MergeLineDTO {\n" +
                "    key: Contact.workPhone\n" +
                "    label: Work phone\n" +
                "    a: 331110000\n" +
                "    b: 123456723\n" +
                "}, class MergeLineDTO {\n" +
                "    key: Student.studentNumber\n" +
                "    label: Student number\n" +
                "    a: 1\n" +
                "    b: 2\n" +
                "}, class MergeLineDTO {\n" +
                "    key: Student.citizenship\n" +
                "    label: Citizenship\n" +
                "    a: No information\n" +
                "    b: Students/Applicants with permanent humanitarian visa\n" +
                "}, class MergeLineDTO {\n" +
                "    key: Student.disabilityType\n" +
                "    label: Disability type\n" +
                "    a: not stated\n" +
                "    b: Hearing/Deaf\n" +
                "}, class MergeLineDTO {\n" +
                "    key: Student.englishProficiency\n" +
                "    label: Proficiency in spoken English\n" +
                "    a: not stated\n" +
                "    b: Very Well\n" +
                "}, class MergeLineDTO {\n" +
                "    key: Student.feeHelpEligible\n" +
                "    label: Fee help eligible\n" +
                "    a: No\n" +
                "    b: Yes\n" +
                "}, class MergeLineDTO {\n" +
                "    key: Student.highestSchoolLevel\n" +
                "    label: Highest school level\n" +
                "    a: not stated\n" +
                "    b: Did not go to school\n" +
                "}, class MergeLineDTO {\n" +
                "    key: Student.indigenousStatus\n" +
                "    label: Indigenous status\n" +
                "    a: not stated\n" +
                "    b: Aboriginal\n" +
                "}, class MergeLineDTO {\n" +
                "    key: Student.isOverseasClient\n" +
                "    label: Overseas\n" +
                "    a: No\n" +
                "    b: Yes\n" +
                "}, class MergeLineDTO {\n" +
                "    key: Student.labourForceStatus\n" +
                "    label: Employment category\n" +
                "    a: not stated\n" +
                "    b: Full-time employee\n" +
                "}, class MergeLineDTO {\n" +
                "    key: Student.priorEducationCode\n" +
                "    label: Prior educational achievement\n" +
                "    a: not stated\n" +
                "    b: Bachelor degree or higher degree level\n" +
                "}, class MergeLineDTO {\n" +
                "    key: tags\n" +
                "    label: Tags\n" +
                "    a: #ContactCommonChildTag #Contact1ChildTag\n" +
                "    b: #ContactCommonChildTag #Contact2ChildTag\n" +
                "}, class MergeLineDTO {\n" +
                "    key: customField.someField1\n" +
                "    label: optionalChoice\n" +
                "    a: value1\n" +
                "    b: value2\n" +
                "}, class MergeLineDTO {\n" +
                "    key: customField.someField2\n" +
                "    label: optionalChoice2\n" +
                "    a: null\n" +
                "    b: value3\n" +
                "}, class MergeLineDTO {\n" +
                "    key: Tutor.dateFinished\n" +
                "    label: Date finished\n" +
                "    a: Tue 1 Jan 2013\n" +
                "    b: Wed 1 Jan 2014\n" +
                "}, class MergeLineDTO {\n" +
                "    key: Tutor.dateStarted\n" +
                "    label: Date started\n" +
                "    a: Sun 1 Jan 2012\n" +
                "    b: Tue 1 Jan 2013\n" +
                "}, class MergeLineDTO {\n" +
                "    key: Tutor.payrollRef\n" +
                "    label: Payroll ref\n" +
                "    a: 1234567890\n" +
                "    b: 0987654321\n" +
                "}, class MergeLineDTO {\n" +
                "    key: Tutor.resume\n" +
                "    label: Resume\n" +
                "    a: some text\n" +
                "    b: some2 text2\n" +
                "}, class MergeLineDTO {\n" +
                "    key: Tutor.wwChildrenStatus\n" +
                "    label: Ww children status\n" +
                "    a: Not checked\n" +
                "    b: Application in progress\n" +
                    "}]", mergeLines.toString())
    }

    @Test
    void testMerge() throws Exception {
        ObjectContext context = cayenneService.newContext
        Contact a = ObjectSelect.query(Contact).where(Contact.ID.eq(1L)).selectFirst(context)
        Contact b = ObjectSelect.query(Contact).where(Contact.ID.eq(2L)).selectFirst(context)

        Map<String, String> diffMap = ['Contact.birthDate'     : 'A',
                                       'Contact.email'         : 'B',
                                       'Contact.firstName'     : 'A',
                                       'Contact.homePhone'     : 'A',
                                       'Contact.gender'        : 'B',
                                       'Contact.lastName'      : 'A',
                                       'Contact.middleName'    : 'B',
                                       'Contact.mobilePhone'   : 'A',
                                       'Contact.postcode'      : 'B',
                                       'Contact.state'         : 'A',
                                       'Contact.street'        : 'B',
                                       'Contact.suburb'        : 'A',
                                       'Contact.workPhone'     : 'B',

                                       'Student.studentNumber' : 'B',
                                       'Student.citizenship'   : 'A',
                                       'Student.disabilityType': 'B',
                                       'Student.englishProficiency': 'A',
                                       'Student.feeHelpEligible': 'B',
                                       'Student.highestSchoolLevel': 'A',
                                       'Student.indigenousStatus': 'B',
                                       'Student.isOverseasClient': 'A',
                                       'Student.labourForceStatus': 'B',
                                       'Student.priorEducationCode': 'A',

                                       'Tutor.dateFinished'    : 'B',
                                       'Tutor.dateStarted'     : 'A',
                                       'Tutor.payrollRef'      : 'B',
                                       'Tutor.resume'          : 'A',
                                       'Tutor.wwChildrenStatus': 'B',

                                       'customField.someField1': 'A',
                                       'customField.someField2': 'B',
                                       'tags'                  : 'A'
        ]

        contactMergeService.merge(a, b, diffMap)

        assertEquals(3, ObjectSelect.query(Contact).selectCount(context))
        assertEquals(1, ObjectSelect.query(Student).selectCount(context))
        assertEquals(1, ObjectSelect.query(Tutor).selectCount(context))

        assertEquals(2, ObjectSelect.query(ContactCustomField).where(ContactCustomField.RELATED_OBJECT.eq(a)).selectCount(context))
        assertEquals(2, ObjectSelect.query(Tag).where(Tag.TAG_RELATIONS.dot(TagRelation.ENTITY_IDENTIFIER).eq(8).andExp(Tag.TAG_RELATIONS.dot(TagRelation.ENTITY_ANGEL_ID).eq(1L))).selectCount(context))
        assertEquals(1, ObjectSelect.query(ContactRelation).where(ContactRelation.TO_CONTACT.eq(a)).selectCount(context))
        assertEquals(1, ObjectSelect.query(ContactRelation).where(ContactRelation.FROM_CONTACT.eq(a)).selectCount(context))
        assertEquals(2, ObjectSelect.query(ClassCost).where(ClassCost.CONTACT.eq(a)).selectCount(context))
        assertEquals(2, ObjectSelect.query(CorporatePass).where(CorporatePass.CONTACT.eq(a)).selectCount(context))
        assertEquals(2, ObjectSelect.query(Invoice).where(Invoice.CONTACT.eq(a)).selectCount(context))
        assertEquals(2, ObjectSelect.query(Message).where(Message.MESSAGE_PERSONS.dot(MessagePerson.CONTACT).eq(a)).selectCount(context))
        assertEquals(3, ObjectSelect.query(ContactNoteRelation).where(ContactNoteRelation.NOTED_CONTACT.eq(a)).selectCount(context))
        assertEquals(2, ObjectSelect.query(PaymentIn).where(PaymentIn.PAYER.eq(a)).selectCount(context))
        assertEquals(2, ObjectSelect.query(PaymentOut).where(PaymentOut.PAYEE.eq(a)).selectCount(context))
        assertEquals(2, ObjectSelect.query(Payslip).where(Payslip.CONTACT.eq(a)).selectCount(context))
        assertEquals(2, ObjectSelect.query(ProductItem).where(ProductItem.CONTACT.eq(a)).selectCount(context))
        assertEquals(2, ObjectSelect.query(ContactUnavailableRuleRelation).where(ContactUnavailableRuleRelation.CONTACT.eq(a)).selectCount(context))
        assertEquals(3, ObjectSelect.query(ContactDuplicate).where(ContactDuplicate.CONTACT_TO_UPDATE.eq(a)).selectCount(context))

        assertEquals(2, ObjectSelect.query(Attendance).where(Attendance.STUDENT.eq(a.student)).selectCount(context))
        assertEquals(2, ObjectSelect.query(Certificate).where(Certificate.STUDENT.eq(a.student)).selectCount(context))
        assertEquals(2, ObjectSelect.query(PriorLearning).where(PriorLearning.STUDENT.eq(a.student)).selectCount(context))
        assertEquals(2, ObjectSelect.query(ConcessionType).where(ConcessionType.STUDENT_CONCESSIONS.dot(StudentConcession.STUDENT).eq(a.student)).selectCount(context))

        assertEquals(2, ObjectSelect.query(CourseClassTutor).where(CourseClassTutor.TUTOR.eq(a.tutor)).selectCount(context))

        assertEquals('1991-01-01', a.birthDate.toString())
        assertEquals('test2@test.te', a.email)
        assertEquals('fname1', a.firstName)
        assertEquals('331110000', a.homePhone)
        assertEquals(Gender.MALE, a.gender)
        assertEquals('lname1', a.lastName)
        assertEquals('mname2', a.middleName)
        assertEquals('331110000', a.mobilePhone)
        assertEquals('0110', a.postcode)
        assertEquals('NSW', a.state)
        assertEquals('Down', a.street)
        assertEquals('Sydney', a.suburb)
        assertEquals('123456723', a.workPhone)

        assertEquals(2, a.student.studentNumber)
        assertEquals(9, a.student.citizenship.databaseValue)
        assertEquals(1, a.student.disabilityType.databaseValue)
        assertEquals(0, a.student.englishProficiency.databaseValue)
        assertTrue(a.student.feeHelpEligible)
        assertEquals(0, a.student.highestSchoolLevel.databaseValue)
        assertEquals(1, a.student.indigenousStatus.databaseValue)
        assertFalse(a.student.isOverseasClient)
        assertEquals(1, a.student.labourForceStatus.databaseValue)
        assertEquals(0, a.student.priorEducationCode.databaseValue)

        assertEquals('2014-01-01 00:00:00', a.tutor.dateFinished.format('YYYY-MM-DD HH:mm:ss'))
        assertEquals('2012-01-01 00:00:00', a.tutor.dateStarted.format('YYYY-MM-DD HH:mm:ss'))
        assertEquals('0987654321', a.tutor.payrollRef)
        assertEquals('some text', a.tutor.resume)
        assertEquals(1, a.tutor.wwChildrenStatus.databaseValue)

    }
}
