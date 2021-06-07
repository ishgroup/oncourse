/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.handler

import groovy.transform.CompileStatic
import ish.DatabaseSetup
import ish.TestWithDatabase
import ish.common.types.*
import ish.math.Money
import ish.oncourse.commercial.replication.builders.IAngelStubBuilder
import ish.oncourse.commercial.replication.cayenne.QueuedRecord
import ish.oncourse.commercial.replication.modules.ISoapPortLocator
import ish.oncourse.commercial.replication.services.IAngelQueueService
import ish.oncourse.generator.DataGenerator
import ish.oncourse.server.cayenne.*
import ish.oncourse.webservices.soap.v23.ReplicationPortType
import ish.oncourse.webservices.util.GenericReplicationStub
import ish.oncourse.webservices.v23.stubs.replication.*
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.junit.jupiter.api.Test

import java.time.LocalDate
import static org.junit.jupiter.api.Assertions.*


/**
 */
@CompileStatic
@DatabaseSetup(value = "ish/oncourse/server/replication/handler/courseClassTutorDataSet.xml")
class OutboundReplicationHandlerTest extends TestWithDatabase {


	@Test
	void testCourseClassTutor() throws Exception {

		ObjectContext ctx = this.cayenneService.getNewContext()

        Tutor tutor = SelectById.query(Tutor.class, 1).selectOne(ctx)
		SelectById.query(Tutor.class, 2).selectOne(ctx)

        CourseClass courseClass = SelectById.query(CourseClass.class, 1).selectOne(ctx)
        CourseClassTutor cct = createCourseClassTutor(tutor, courseClass)

        Session session = createSession(ctx)
		session.setCourseClass(courseClass)

        TutorAttendance tutorAttendance = ctx.newObject(TutorAttendance.class)
		tutorAttendance.setSession(session)
		tutorAttendance.setCourseClassTutor(cct)
		tutorAttendance.setAttendanceType(AttendanceType.UNMARKED)

		session.addToSessionTutors(tutorAttendance)

		ctx.commitChanges()

        Queueable[] queuableArray = [cct, session, tutor, courseClass]

		ISoapPortLocator soapLocator = new AbstractSoapPortLocator() {

			@Override
			ReplicationPortType replicationPort() {

				return new AbstractReplicationPortType() {
					@Override
					ReplicationResult sendRecords(ReplicationRecords records) {
						assertNotNull(records)

						ReplicationResult result = new ReplicationResult()

						boolean isSesionStub = false
						boolean isTutorStub = false
						boolean isCourseClassStub = false
						boolean isCourseClassTutorStub = false
						boolean isTutorAttendanceStub = false

						for (TransactionGroup group : records.getGroups()) {
							for (GenericReplicationStub stub : group.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
								ReplicatedRecord r = new ReplicatedRecord()
								r.setStatus(Status.SUCCESS)
								r.setStub(toHollow(stub))
								r.getStub().setWillowId(1L)
								result.getReplicatedRecord().add(r)
								if (stub instanceof CourseClassStub) {
									isCourseClassStub = true
								} else if (stub instanceof CourseClassTutorStub) {
									isCourseClassTutorStub = true
									r.getStub().setEntityIdentifier("CourseClassTutor")
								} else if (stub instanceof TutorStub) {
									isTutorStub = true
								} else if (stub instanceof SessionStub) {
									isSesionStub = true
								} else if (stub instanceof TutorAttendanceStub) {
									isTutorAttendanceStub = true
									r.getStub().setEntityIdentifier("TutorAttendance")
									assertNotNull(stub.getAngelId())
								}
							}
						}

                        assertTrue(isCourseClassStub,"Check CourseClassStub")
						assertTrue(isSesionStub,"Check SesionStub")
						assertTrue(isTutorStub,"Check TutorStub")
						assertTrue(isCourseClassTutorStub,"Check CourseClassTutorStub")
						assertTrue(isTutorAttendanceStub,"Check TutorAttendanceStub")

						return result
					}
				}
			}
		}

		OutboundReplicationHandler handler = new OutboundReplicationHandler(
						injector.getInstance(IAngelQueueService.class),
						this.cayenneService,
						soapLocator,
						injector.getInstance(IAngelStubBuilder.class))
		handler.replicate()

		ObjectContext ctx1 = this.cayenneService.getNewContext()
		// checking that willowId is set
		for (Queueable q : queuableArray) {
			Queueable local = SelectById.query(Queueable.class, q.getObjectId()).selectOne(ctx1)
			long willowId = local.getWillowId()
			assertEquals(willowId, 1L,"Expecting willowId=1.")

			long count = ObjectSelect.query(QueuedRecord)
					.where(QueuedRecord.TABLE_NAME.eq(q.getObjectId().getEntityName()))
					.and(QueuedRecord.FOREIGN_RECORD_ID.eq(q.getId()))
					.selectCount(ctx1)

			assertEquals( 0, count,"Expecting zero queued records for entity.")
		}
	}

	@Test
	void testCourseClassTutorRemoveAndAdd() throws Exception {
		ObjectContext ctx = this.cayenneService.getNewContext()

		SelectById.query(Tutor.class, 1).selectOne(ctx)
        CourseClass courseClass = SelectById.query(CourseClass.class, 1).selectOne(ctx)
        CourseClassTutor cct = SelectById.query(CourseClassTutor.class, 100).selectOne(ctx)
        Session session = SelectById.query(Session.class, 200).selectOne(ctx)

        Tutor tutor2 = SelectById.query(Tutor.class, 2).selectOne(ctx)
		cct.setTutor(tutor2)

		ctx.commitChanges()

        Queueable[] queuableArray = [cct, session, tutor2, courseClass]

		ISoapPortLocator soapLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {

				return new AbstractReplicationPortType() {
					@Override
					ReplicationResult sendRecords(ReplicationRecords records) {
						assertNotNull(records)

						ReplicationResult result = new ReplicationResult()

						boolean isCourseClassTutorStub = false
						int tutorStubCount = 0

						for (TransactionGroup group : records.getGroups()) {
							for (GenericReplicationStub stub : group.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
								ReplicatedRecord r = new ReplicatedRecord()
								r.setStatus(Status.SUCCESS)
								r.setStub(toHollow(stub))
								r.getStub().setWillowId(2L)
								result.getReplicatedRecord().add(r)
								if (stub instanceof CourseClassTutorStub) {
									isCourseClassTutorStub = true
									r.getStub().setEntityIdentifier("CourseClassTutor")
								} else if (stub instanceof TutorStub) {
									tutorStubCount++
								}
							}
						}

                        assertTrue( isCourseClassTutorStub, "Check CourseClassTutorStub ")
						assertEquals(2, tutorStubCount,"Check TutorStub ")

						return result
					}
				}
			}
		}

		OutboundReplicationHandler handler = new OutboundReplicationHandler(
						injector.getInstance(IAngelQueueService.class),
						this.cayenneService,
						soapLocator,
						injector.getInstance(IAngelStubBuilder.class))
		handler.replicate()

		ObjectContext ctx1 = this.cayenneService.getNewContext()

		// checking that willowId is set
		for (Queueable q : queuableArray) {
			Queueable local = SelectById.query(Queueable.class, q.getObjectId()).selectOne(ctx1)

			if (local != null) {
				Long willowId = local.getWillowId()

				if (willowId != null) {
					long count = ObjectSelect.query(QueuedRecord)
							.where(QueuedRecord.TABLE_NAME.eq(q.getObjectId().getEntityName()))
							.and(QueuedRecord.FOREIGN_RECORD_ID.eq(q.getId()))
							.selectCount(ctx1)
					assertEquals( 0, count,"Expecting zero queued records for entity.")
				}
			}
		}
	}

	static CourseClassTutor createCourseClassTutor(Tutor tutor, CourseClass cc) {
		ObjectContext ctx = cc.getObjectContext()
        CourseClassTutor cct = ctx.newObject(CourseClassTutor.class)
		cct.setTutor(tutor)
		cct.setCourseClass(cc)
		cct.setInPublicity(true)

        DefinedTutorRole definedTutorRole = ctx.newObject(DefinedTutorRole.class)
		definedTutorRole.setName("definedTutorRoleName")

		cct.setDefinedTutorRole(definedTutorRole)
		return cct
	}

	static Session createSession(ObjectContext ctx) {
		Session session = ctx.newObject(Session.class)
		session.setCourseClass(SelectById.query(CourseClass.class, 1).selectOne(ctx))
		session.setPayAdjustment(4)
		return session
	}

	static Enrolment createEnrolment(InvoiceLine invoiceLine, Student student, CourseClass courseClass) {
		ObjectContext ctx = invoiceLine.getObjectContext()

        Enrolment enrl = ctx.newObject(Enrolment.class)
		enrl.addToInvoiceLines(invoiceLine)
		enrl.setSource(PaymentSource.SOURCE_WEB)
		enrl.setStatus(EnrolmentStatus.SUCCESS)
		enrl.setStudent(student)
		enrl.setCourseClass(courseClass)

		return enrl
	}

	static CourseClass createCourseClass(Course course) {
		ObjectContext ctx = course.getObjectContext()

        CourseClass c = ctx.newObject(CourseClass.class)

		c.setIsCancelled(false)
		c.setIsActive(true)
		c.setCode("12345")
		c.setCourse(course)
		c.setFeeExGst(new Money(123, 25))
		c.setMaterials("Test materials")
		c.setMaximumPlaces(18)
		c.setIsClassFeeApplicationOnly(true)
		c.setSessionsCount(10)
		c.setSuppressAvetmissExport(false)
		c.setBudgetedPlaces(10)

		c.setDeliveryMode(DeliveryMode.ONLINE)
		c.setMinimumPlaces(7)
		c.setTaxAdjustment(new Money(1, 1))
		c.setTax(SelectById.query(Tax.class, 1).selectOne(ctx))
		c.setIncomeAccount(SelectById.query(Account.class, 200).selectOne(ctx))
		c.setAttendanceType(CourseClassAttendanceType.NO_INFORMATION)

		return c
	}

	static Course createCourse(ObjectContext ctx) {

		Course c = ctx.newObject(Course.class)

		c.setCode("123")

		c.setFieldOfEducation("BBBNNN")
		c.setName("computer literance")

        Module m1 = SelectById.query(Module.class, 1).selectOne(ctx)
        Module m2 = SelectById.query(Module.class, 2).selectOne(ctx)

		c.addToModules(m1)
		c.addToModules(m2)

        Qualification qualification = SelectById.query(Qualification.class, 1).selectOne(ctx)
		c.setQualification(qualification)

		c.setFieldConfigurationSchema(DataGenerator.valueOf(ctx).getFieldConfigurationScheme())

		return c
	}

	static InvoiceLine createInvoiceLine(Invoice invoice) {
		ObjectContext ctx = invoice.getObjectContext()

        InvoiceLine invLine = ctx.newObject(InvoiceLine.class)

		invLine.setTitle("Test invoice line")
		invLine.setDescription("Test invoice line.")
		invLine.setInvoice(invoice)
		invLine.setQuantity(new BigDecimal(5))
		invLine.setTaxEach(new Money(10, 1))
		invLine.setUnit("unit")
		invLine.setPrepaidFeesAccount(SelectById.query(Account.class, 100).selectOne(ctx))

		invLine.setTax(SelectById.query(Tax.class, 1).selectOne(ctx))
		invLine.setPriceEachExTax(new Money(1, 1))
		invLine.setDiscountEachExTax(new Money(1, 1))
		invLine.setAccount(SelectById.query(Account.class, 1).selectOne(ctx))

		return invLine
	}

	static Invoice createInvoice(Contact contact) {
		ObjectContext ctx = contact.getObjectContext()

        Invoice inv = ctx.newObject(Invoice.class)

		inv.setContact(contact)
		inv.setCustomerReference("customer reference")
		inv.setDateDue(LocalDate.now())
		inv.setDescription("test description")
		inv.setInvoiceDate(LocalDate.now())
		inv.setPublicNotes("test public notes")
		inv.setShippingAddress("test shipping address")
		inv.setSource(PaymentSource.SOURCE_WEB)
		inv.setAmountOwing(new Money(10, 10))
		inv.setDebtorsAccount(SelectById.query(Account.class, 1).selectOne(ctx))

		return inv
	}

	static Student createStudent(Contact contact) {
		ObjectContext ctx = contact.getObjectContext()

        Student st = ctx.newObject(Student.class)

		st.setContact(contact)
        Country country = SelectById.query(Country.class, 1).selectOne(ctx)
		st.setCountryOfBirth(country)
		st.setDisabilityType(AvetmissStudentDisabilityType.HEARING)
		st.setEnglishProficiency(AvetmissStudentEnglishProficiency.VERY_WELL)
		st.setHighestSchoolLevel(AvetmissStudentSchoolLevel.COMPLETED_YEAR_11)
		st.setIndigenousStatus(AvetmissStudentIndigenousStatus.ABORIGINAL)
		st.setIsOverseasClient(true)
		st.setIsStillAtSchool(true)
		st.setCitizenship(StudentCitizenship.NO_INFORMATION)
		st.setFeeHelpEligible(false)

        Language lang = SelectById.query(Language.class, 2).selectOne(ctx)
		st.setLanguage(lang)

		st.setPriorEducationCode(AvetmissStudentPriorEducation.MISC)
		st.setYearSchoolCompleted(1999)

		return st
	}

	static Contact createContact(ObjectContext ctx) {
		Contact c = ctx.newObject(Contact.class)

        Country country = SelectById.query(Country.class, 1).selectOne(ctx)
		c.setCountry(country)

		c.setUniqueCode("AA123")
		c.setFirstName("Vladimir")
		c.setLastName("Putin")
		c.setIsCompany(false)
		c.setGender(Gender.MALE)
		c.setPostcode("777AB")
		c.setState("NSW")
		c.setStreet("30 Avoca st.")
		c.setSuburb("Randwick")

		return c
	}

	private static HollowStub toHollow(GenericReplicationStub stub) {

		HollowStub hollowStub = new HollowStub()
		hollowStub.setEntityIdentifier(stub.getEntityIdentifier())
		hollowStub.setAngelId(stub.getAngelId())

		Date today = new Date()

		hollowStub.setModified(today)
		hollowStub.setCreated(today)

		return hollowStub
	}
}
