package ish.oncourse.server.deduplication

import groovy.transform.CompileStatic
import ish.TestWithDatabase
import ish.DatabaseSetup
import ish.common.types.Gender
import ish.oncourse.server.api.v1.model.MergeLineDTO
import ish.oncourse.server.cayenne.*
import org.apache.cayenne.query.ObjectSelect
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
@DatabaseSetup(value = "ish/oncourse/server/deduplication/dedupeContactDataSet.xml")
class ContactMergeServiceTest extends TestWithDatabase {
    
    @Test
    void testGetDiffData() {
        Contact contactA = ObjectSelect.query(Contact).where(Contact.ID.eq(1L)).selectFirst(cayenneContext)
        Contact contactB = ObjectSelect.query(Contact).where(Contact.ID.eq(2L)).selectFirst(cayenneContext)
        List<MergeLineDTO> mergeLines = injector.getInstance(ContactMergeService).getDifferenceAttributes(contactA, contactB)
        Assertions.assertEquals("[class MergeLineDTO {\n" +
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
                "    a: Tue. 1 Jan. 2013\n" +
                "    b: Wed. 1 Jan. 2014\n" +
                "}, class MergeLineDTO {\n" +
                "    key: Tutor.dateStarted\n" +
                "    label: Date started\n" +
                "    a: Sun. 1 Jan. 2012\n" +
                "    b: Tue. 1 Jan. 2013\n" +
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
        Contact a = ObjectSelect.query(Contact).where(Contact.ID.eq(1L)).selectFirst(cayenneContext)
        Contact b = ObjectSelect.query(Contact).where(Contact.ID.eq(2L)).selectFirst(cayenneContext)

        Map<String, String> diffMap = ['Contact.birthDate'         : 'A',
                                       'Contact.email'             : 'B',
                                       'Contact.firstName'         : 'A',
                                       'Contact.homePhone'         : 'A',
                                       'Contact.gender'            : 'B',
                                       'Contact.lastName'          : 'A',
                                       'Contact.middleName'        : 'B',
                                       'Contact.mobilePhone'       : 'A',
                                       'Contact.postcode'          : 'B',
                                       'Contact.state'             : 'A',
                                       'Contact.street'            : 'B',
                                       'Contact.suburb'            : 'A',
                                       'Contact.workPhone'         : 'B',

                                       'Student.studentNumber'     : 'B',
                                       'Student.citizenship'       : 'A',
                                       'Student.disabilityType'    : 'B',
                                       'Student.englishProficiency': 'A',
                                       'Student.feeHelpEligible'   : 'B',
                                       'Student.highestSchoolLevel': 'A',
                                       'Student.indigenousStatus'  : 'B',
                                       'Student.isOverseasClient'  : 'A',
                                       'Student.labourForceStatus' : 'B',
                                       'Student.priorEducationCode': 'A',

                                       'Tutor.dateFinished'        : 'B',
                                       'Tutor.dateStarted'         : 'A',
                                       'Tutor.payrollRef'          : 'B',
                                       'Tutor.resume'              : 'A',
                                       'Tutor.wwChildrenStatus'    : 'B',

                                       'customField.someField1'    : 'A',
                                       'customField.someField2'    : 'B',
                                       'tags'                      : 'A'
        ]

        injector.getInstance(ContactMergeService).merge(a, b, diffMap)

        Assertions.assertEquals(3, ObjectSelect.query(Contact).selectCount(cayenneContext))
        Assertions.assertEquals(1, ObjectSelect.query(Student).selectCount(cayenneContext))
        Assertions.assertEquals(1, ObjectSelect.query(Tutor).selectCount(cayenneContext))

        Assertions.assertEquals(2, ObjectSelect.query(ContactCustomField).where(ContactCustomField.RELATED_OBJECT.eq(a)).selectCount(cayenneContext))
        Assertions.assertEquals(2, ObjectSelect.query(Tag).where(Tag.TAG_RELATIONS.dot(TagRelation.ENTITY_IDENTIFIER).eq(8).andExp(Tag.TAG_RELATIONS.dot(TagRelation.ENTITY_ANGEL_ID).eq(1L))).selectCount(cayenneContext))
        Assertions.assertEquals(1, ObjectSelect.query(ContactRelation).where(ContactRelation.TO_CONTACT.eq(a)).selectCount(cayenneContext))
        Assertions.assertEquals(1, ObjectSelect.query(ContactRelation).where(ContactRelation.FROM_CONTACT.eq(a)).selectCount(cayenneContext))
        Assertions.assertEquals(2, ObjectSelect.query(ClassCost).where(ClassCost.CONTACT.eq(a)).selectCount(cayenneContext))
        Assertions.assertEquals(2, ObjectSelect.query(CorporatePass).where(CorporatePass.CONTACT.eq(a)).selectCount(cayenneContext))
        Assertions.assertEquals(2, ObjectSelect.query(Invoice).where(Invoice.CONTACT.eq(a)).selectCount(cayenneContext))
        Assertions.assertEquals(2, ObjectSelect.query(Message).where(Message.MESSAGE_PERSONS.dot(MessagePerson.CONTACT).eq(a)).selectCount(cayenneContext))
        Assertions.assertEquals(3, ObjectSelect.query(ContactNoteRelation).where(ContactNoteRelation.NOTED_CONTACT.eq(a)).selectCount(cayenneContext))
        Assertions.assertEquals(2, ObjectSelect.query(PaymentIn).where(PaymentIn.PAYER.eq(a)).selectCount(cayenneContext))
        Assertions.assertEquals(2, ObjectSelect.query(PaymentOut).where(PaymentOut.PAYEE.eq(a)).selectCount(cayenneContext))
        Assertions.assertEquals(2, ObjectSelect.query(Payslip).where(Payslip.CONTACT.eq(a)).selectCount(cayenneContext))
        Assertions.assertEquals(2, ObjectSelect.query(ProductItem).where(ProductItem.CONTACT.eq(a)).selectCount(cayenneContext))
        Assertions.assertEquals(2, ObjectSelect.query(ContactUnavailableRuleRelation).where(ContactUnavailableRuleRelation.CONTACT.eq(a)).selectCount(cayenneContext))
        Assertions.assertEquals(3, ObjectSelect.query(ContactDuplicate).where(ContactDuplicate.CONTACT_TO_UPDATE.eq(a)).selectCount(cayenneContext))

        Assertions.assertEquals(2, ObjectSelect.query(Attendance).where(Attendance.STUDENT.eq(a.student)).selectCount(cayenneContext))
        Assertions.assertEquals(2, ObjectSelect.query(Certificate).where(Certificate.STUDENT.eq(a.student)).selectCount(cayenneContext))
        Assertions.assertEquals(2, ObjectSelect.query(PriorLearning).where(PriorLearning.STUDENT.eq(a.student)).selectCount(cayenneContext))
        Assertions.assertEquals(2, ObjectSelect.query(ConcessionType).where(ConcessionType.STUDENT_CONCESSIONS.dot(StudentConcession.STUDENT).eq(a.student)).selectCount(cayenneContext))

        Assertions.assertEquals(2, ObjectSelect.query(CourseClassTutor).where(CourseClassTutor.TUTOR.eq(a.tutor)).selectCount(cayenneContext))

        Assertions.assertEquals('1991-01-01', a.birthDate.toString())
        Assertions.assertEquals('test2@test.te', a.email)
        Assertions.assertEquals('fname1', a.firstName)
        Assertions.assertEquals('331110000', a.homePhone)
        Assertions.assertEquals(Gender.MALE, a.gender)
        Assertions.assertEquals('lname1', a.lastName)
        Assertions.assertEquals('mname2', a.middleName)
        Assertions.assertEquals('331110000', a.mobilePhone)
        Assertions.assertEquals('0110', a.postcode)
        Assertions.assertEquals('NSW', a.state)
        Assertions.assertEquals('Down', a.street)
        Assertions.assertEquals('Sydney', a.suburb)
        Assertions.assertEquals('123456723', a.workPhone)

        Assertions.assertEquals(2, a.student.studentNumber)
        Assertions.assertEquals(9, a.student.citizenship.databaseValue)
        Assertions.assertEquals(1, a.student.disabilityType.databaseValue)
        Assertions.assertEquals(0, a.student.englishProficiency.databaseValue)
        Assertions.assertTrue(a.student.feeHelpEligible)
        Assertions.assertEquals(0, a.student.highestSchoolLevel.databaseValue)
        Assertions.assertEquals(1, a.student.indigenousStatus.databaseValue)
        Assertions.assertFalse(a.student.isOverseasClient)
        Assertions.assertEquals(1, a.student.labourForceStatus.databaseValue)
        Assertions.assertEquals(0, a.student.priorEducationCode.databaseValue)

        Assertions.assertEquals('2014-01-01 00:00:00', a.tutor.dateFinished.format('YYYY-MM-DD HH:mm:ss'))
        Assertions.assertEquals('2012-01-01 00:00:00', a.tutor.dateStarted.format('YYYY-MM-DD HH:mm:ss'))
        Assertions.assertEquals('0987654321', a.tutor.payrollRef)
        Assertions.assertEquals('some text', a.tutor.resume)
        Assertions.assertEquals(1, a.tutor.wwChildrenStatus.databaseValue)

    }
}
