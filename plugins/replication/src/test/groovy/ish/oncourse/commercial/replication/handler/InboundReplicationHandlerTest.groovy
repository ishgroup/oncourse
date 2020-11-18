/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.handler

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.common.types.*
import ish.oncourse.commercial.replication.modules.ISoapPortLocator
import ish.oncourse.server.PreferenceController
import ish.oncourse.webservices.ITransactionGroupProcessor
import ish.oncourse.webservices.soap.v22.ReplicationPortType
import ish.oncourse.webservices.util.GenericTransactionGroup
import ish.oncourse.webservices.v22.stubs.replication.*
import ish.util.SecurityUtil
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.*

/**
 */
@CompileStatic
class InboundReplicationHandlerTest extends CayenneIshTestCase {

	private ITransactionGroupProcessor transactionGroupAtomicUpdater

	@Before
	void setup() throws Exception {
		wipeTables()
        PreferenceController pref = injector.getInstance(PreferenceController.class)
		this.transactionGroupAtomicUpdater = injector.getInstance(ITransactionGroupProcessor.class)
	}

	@Test
	void testMainFlow() throws Exception {

		ISoapPortLocator soapPortLocator = new AbstractSoapPortLocator() {

			@Override
			ReplicationPortType replicationPort() {

				return new AbstractReplicationPortType() {
					@Override
					ReplicationRecords getRecords() {
						ReplicationRecords records = new ReplicationRecords()
						GenericTransactionGroup group = new TransactionGroup()

						WaitingListStub wList = new WaitingListStub()
						wList.setEntityIdentifier("WaitingList")
						wList.setWillowId(2L)
						wList.setAngelId(null)
						wList.setCourseId(35L)
						wList.setStudentId(100L)
						wList.setStudentCount(5)

						CourseStub course = new CourseStub()
						course.setEntityIdentifier("Course")
						course.setWillowId(35L)
						course.setAngelId(null)
						course.setCode("123")
						course.setVETCourse(false)
						course.setName("Math")
						course.setAllowWaitingList(true)
						course.setSufficientForQualification(false)
						course.setEnrolmentType(CourseEnrolmentType.OPEN_FOR_ENROLMENT.getDatabaseValue())
						course.setFieldConfigurationSchemeId(66L)

						Date date = new Date()

						FieldConfigurationSchemeStub schemeStub = new FieldConfigurationSchemeStub()
						schemeStub.setEntityIdentifier("FieldConfigurationScheme")
						schemeStub.setWillowId(66L)
						schemeStub.setAngelId(null)
						schemeStub.setName("scheme")
						schemeStub.setCreated(date)
						schemeStub.setModified(date)

						FieldConfigurationStub configurationStubA = new FieldConfigurationStub()
						configurationStubA.setEntityIdentifier("ApplicationFieldConfiguration")
						configurationStubA.setWillowId(55L)
						configurationStubA.setAngelId(null)
						configurationStubA.setName("configuration")
						configurationStubA.setCreated(date)
						configurationStubA.setModified(date)
						configurationStubA.setType(2)

						FieldConfigurationLinkStub linkStubA = new FieldConfigurationLinkStub()
						linkStubA.setCreated(date)
						linkStubA.setModified(date)
						linkStubA.setWillowId(19L)
						linkStubA.setSchemeId(66L)
						linkStubA.setConfigurationId(55L)

						FieldConfigurationStub configurationStubE = new FieldConfigurationStub()
						configurationStubE.setEntityIdentifier("EnrolmentFieldConfiguration")
						configurationStubE.setWillowId(56L)
						configurationStubE.setAngelId(null)
						configurationStubE.setName("configuration")
						configurationStubE.setCreated(date)
						configurationStubE.setModified(date)
						configurationStubE.setType(1)

						FieldConfigurationLinkStub linkStubE = new FieldConfigurationLinkStub()
						linkStubE.setCreated(date)
						linkStubE.setModified(date)
						linkStubE.setWillowId(20L)
						linkStubE.setSchemeId(66L)
						linkStubE.setConfigurationId(56L)

						FieldConfigurationStub configurationStubW = new FieldConfigurationStub()
						configurationStubW.setEntityIdentifier("WaitingListFieldConfiguration")
						configurationStubW.setWillowId(57L)
						configurationStubW.setAngelId(null)
						configurationStubW.setName("configuration")
						configurationStubW.setCreated(date)
						configurationStubW.setModified(date)
						configurationStubW.setType(3)

						FieldConfigurationLinkStub linkStubW = new FieldConfigurationLinkStub()
						linkStubW.setCreated(date)
						linkStubW.setModified(date)
						linkStubW.setWillowId(21L)
						linkStubW.setSchemeId(66L)
						linkStubW.setConfigurationId(57L)

						StudentStub st = new StudentStub()
						st.setEntityIdentifier("Student")
						st.setWillowId(100L)
						st.setAngelId(null)
						st.setContactId(200L)
						st.setEnglishProficiency(1)
						st.setIndigenousStatus(1)
						st.setHighestSchoolLevel(1)
						st.setPriorEducationCode(1)
						st.setOverseasClient(true)
						st.setDisabilityType(1)
						st.setCitizenship(StudentCitizenship.NO_INFORMATION.getDatabaseValue())
						st.setFeeHelpEligible(false)

						ContactStub con = new ContactStub()
						con.setFamilyName("Test")
						con.setGivenName("Test1")
						con.setCompany(false)
						con.setMarketingViaEmailAllowed(true)
						con.setMarketingViaPostAllowed(true)
						con.setMarketingViaSMSAllowed(true)
						con.setEntityIdentifier("Contact")
						con.setStudentId(100L)
						con.setWillowId(200L)
						con.setAngelId(null)
						con.setUniqueCode(SecurityUtil.generateRandomPassword(16))

						group.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(course)
						group.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(st)
						group.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(con)
						group.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(wList)
						group.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(schemeStub)
						group.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(configurationStubW)

						records.getGenericGroups().add(group)

						return records
					}

					@Override
					int sendResults(ReplicationResult replResult) {

						for (ReplicatedRecord r : replResult.getReplicatedRecord()) {
							assertNotNull(String.format("Check angel id for %s", r.getStub().getEntityIdentifier()), r.getStub().getAngelId())
							assertEquals(String.format("Check status SUCESS %s", r.getStub().getEntityIdentifier()), Status.SUCCESS, r.getStatus())
						}

						return replResult.getReplicatedRecord().size()
					}
				}
			}

		}

		InboundReplicationHandler handler = new InboundReplicationHandler(soapPortLocator, this.transactionGroupAtomicUpdater)
		handler.replicate()
	}

	// @Test
	void testPartialSucess() throws Exception {

		ISoapPortLocator soapPortLocator = new AbstractSoapPortLocator() {
			@Override
			ReplicationPortType replicationPort() {

				return new AbstractReplicationPortType() {

					@Override
					ReplicationRecords getRecords() {
						ReplicationRecords records = new ReplicationRecords()
						TransactionGroup group = new TransactionGroup()

						WaitingListStub wList = new WaitingListStub()
						wList.setEntityIdentifier("WaitingList")
						wList.setWillowId(3L)
						wList.setCourseId(37L)
						wList.setStudentId(101L)
						wList.setStudentCount(5)

						CourseStub course = new CourseStub()
						course.setEntityIdentifier("Course")
						course.setWillowId(36L)
						course.setCode("123")
						course.setVETCourse(false)
						course.setName("Math")
						course.setAllowWaitingList(true)
						course.setSufficientForQualification(false)
						// course.setAngelId(15l);

						StudentStub st = new StudentStub()
						st.setEntityIdentifier("Student")
						st.setWillowId(101L)
						st.setContactId(251L)
						st.setAngelId(null)
						st.setOverseasClient(false)
						st.setIndigenousStatus(AvetmissStudentIndigenousStatus.NEITHER.getDatabaseValue())
						st.setDisabilityType(AvetmissStudentDisabilityType.NONE.getDatabaseValue())
						st.setPriorEducationCode(AvetmissStudentPriorEducation.NONE.getDatabaseValue())
						st.setEnglishProficiency(AvetmissStudentEnglishProficiency.VERY_WELL.getDatabaseValue())
						st.setHighestSchoolLevel(AvetmissStudentSchoolLevel.COMPLETED_YEAR_12.getDatabaseValue())

						ContactStub con = new ContactStub()
						con.setFamilyName("Test")
						con.setGivenName("Test1")
						con.setCompany(false)
						con.setMarketingViaEmailAllowed(true)
						con.setMarketingViaPostAllowed(true)
						con.setMarketingViaSMSAllowed(true)
						con.setEntityIdentifier("Contact")
						con.setWillowId(201L)

						group.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(course)
						group.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(st)
						group.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(con)
						group.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(wList)

						records.getGroups().add(group)

						return records
					}

					@Override
					int sendResults(ReplicationResult replResult) {

						for (ReplicatedRecord r : replResult.getReplicatedRecord()) {
							if (r.getStub().getEntityIdentifier().equals("Student")) {
								assertNull("Check angel id", r.getStub().getAngelId())
								assertEquals("Check status FAILED on Student.", Status.FAILED, r.getStatus())
							} else if (r.getStub().getEntityIdentifier().equals("WaitingList")) {
								assertNull("Check angel id", r.getStub().getAngelId())
								assertEquals("Check status FAILED on WaitingList.", Status.FAILED, r.getStatus())
							} else {
								assertNotNull("Check angel id", r.getStub().getAngelId())
								assertEquals("Check status SUCESS", Status.SUCCESS, r.getStatus())
							}
						}

						return replResult.getReplicatedRecord().size()
					}
				}
			}

		}

		InboundReplicationHandler handler = new InboundReplicationHandler(soapPortLocator, this.transactionGroupAtomicUpdater)
		handler.replicate()
	}
}
