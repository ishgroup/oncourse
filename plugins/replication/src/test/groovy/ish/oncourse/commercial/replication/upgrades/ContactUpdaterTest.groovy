/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.upgrades

import groovy.transform.CompileStatic
import ish.common.types.Gender
import ish.oncourse.commercial.replication.updaters.ContactUpdater
import ish.oncourse.commercial.replication.updaters.RelationShipCallback
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.Queueable
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.cayenne.Tutor
import ish.oncourse.webservices.v23.stubs.replication.ContactStub
import org.apache.cayenne.ObjectContext
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull

/**
 */
@CompileStatic
class ContactUpdaterTest {

	// @Mock
	private ObjectContext objectContext

	private Tutor tutor

	private Student student

	@Before
	void initMethod() {
		objectContext = org.mockito.Mockito.mock(ObjectContext.class)
		tutor = org.mockito.Mockito.mock(Tutor.class)
		student = org.mockito.Mockito.mock(Student.class)
		org.mockito.Mockito.when(tutor.getObjectContext()).thenReturn(objectContext)
		org.mockito.Mockito.when(student.getObjectContext()).thenReturn(objectContext)
		org.mockito.Mockito.when(tutor.getWillowId()).thenReturn(1234L)
		org.mockito.Mockito.when(student.getWillowId()).thenReturn(4321L)
		final Contact preparedContact = new Contact()
		org.mockito.Mockito.when(objectContext.newObject(Contact.class)).thenReturn(preparedContact)
	}

	@Test
	void testCorrectUpdate() {
		final ContactStub stub = new ContactStub()
		stub.setAngelId(123L)
		stub.setWillowId(321L)
		stub.setBusinessPhoneNumber("bpn1")
		stub.setCompany(false)
		stub.setCountryId(null)
		stub.setCreated(new Date())
		stub.setEmailAddress("1234567890@test.au")
		stub.setFamilyName("fn1")
		stub.setGivenName("gn1")
		stub.setFaxNumber("faxn1")
		stub.setHomePhoneNumber("hpn1")
		stub.setGender(Gender.MALE.getDatabaseValue())
		stub.setUniqueCode("uc1")
		stub.setSuburb("suburb1")
		stub.setStreet("s1")
		stub.setPostcode("p1")
		stub.setState("state1")
		stub.setMobilePhoneNumber("mpn1")
		stub.setTutorId(null)
		stub.setStudentId(null)
        Contact contact = objectContext.newObject(Contact.class)
		new ContactUpdater().updateEntityFromStub(stub, contact, new RelationShipCallback() {
			def <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
				if ("Tutor".equals(clazz.getSimpleName()) && 1234L == entityId) {
					return (M) tutor
				} else if ("Student".equals(clazz.getSimpleName()) && 4321L == entityId) {
					return (M) student
				}
				return null
			}
		})
		assertNull("Check angel id.", contact.getId())
		assertEquals("Check work phone.", stub.getBusinessPhoneNumber(), contact.getWorkPhone())
		assertEquals("Check is company.", stub.isCompany(), contact.getIsCompany())
		assertEquals("Check created on.", stub.getCreated(), contact.getCreatedOn())
		assertNull("Check country.", contact.getCountry())
		assertEquals("Check email.", stub.getEmailAddress(), contact.getEmail())
		assertEquals("Check last name.", stub.getFamilyName(), contact.getLastName())
		assertEquals("Check first name.", stub.getGivenName(), contact.getFirstName())
		assertEquals("Check fax.", stub.getFaxNumber(), contact.getFax())
		assertEquals("Check home phone.", stub.getHomePhoneNumber(), contact.getHomePhone())
		assertEquals("Check is male.", stub.getGender(), contact.getGender().getDatabaseValue())
		assertEquals("Check unique code.", stub.getUniqueCode(), contact.getUniqueCode())
		assertEquals("Check suburb.", stub.getSuburb(), contact.getSuburb())
		assertEquals("Check street.", stub.getStreet(), contact.getStreet())
		assertEquals("Check postcode.", stub.getPostcode(), contact.getPostcode())
		assertEquals("Check work phone.", stub.getBusinessPhoneNumber(), contact.getWorkPhone())
		assertEquals("Check state.", stub.getState(), contact.getState())
		assertEquals("Check mobile phone.", stub.getMobilePhoneNumber(), contact.getMobilePhone())
		assertNull("Check tutor.", contact.getTutor())
		assertNull("Check student.", contact.getStudent())
	}
}
