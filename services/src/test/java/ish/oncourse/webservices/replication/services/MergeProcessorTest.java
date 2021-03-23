/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.services;

import ish.common.types.ContactDuplicateStatus;
import ish.common.types.UsiStatus;
import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.tapestry.ServiceTest;
import ish.oncourse.webservices.ITransactionGroupProcessor;
import ish.oncourse.webservices.replication.builders.WillowStubBuilderTest;
import ish.oncourse.webservices.util.GenericReplicatedRecord;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.util.PortHelper;
import ish.oncourse.webservices.v21.stubs.replication.*;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import static ish.oncourse.webservices.util.SupportedVersions.V21;
import static ish.oncourse.webservices.v21.stubs.replication.Status.SUCCESS;
import static org.junit.Assert.*;

public class MergeProcessorTest extends ServiceTest {


	ITransactionGroupProcessor transactionGroupProcessor;
	ICayenneService cayenneService;


	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.webservices.services", "", ReplicationTestModule.class);

		InputStream st = WillowStubBuilderTest.class.getClassLoader().getResourceAsStream("ish/oncourse/webservices/replication/services/MergeProcessorTestDataSet.xml");
		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);

		DataSource onDataSource = getDataSource();
		DatabaseConnection dbConnection = new DatabaseConnection(onDataSource.getConnection(), null);
		dbConnection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);

		DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);

		transactionGroupProcessor = getService(ITransactionGroupProcessor.class);
		cayenneService = getService(ICayenneService.class);

	}

	@Test
	public void processMergeTransaction() {

		GenericTransactionGroup transactionGroup = PortHelper.createTransactionGroup(V21);

		ContactDuplicateStub duplicateStub = new ContactDuplicateStub();
		duplicateStub.setAngelId(1l);
		duplicateStub.setStatus(ContactDuplicateStatus.IN_TRANSACTION.getDatabaseValue());
		duplicateStub.setDescription("Description");
		duplicateStub.setContactToUpdateId(1l);
		duplicateStub.setContactToDeleteWillowId(2l);
		duplicateStub.setContactToDeleteAngelId(200l);
		duplicateStub.setEntityIdentifier("ContactDuplicate");
		duplicateStub.setCreated(new Date());
		duplicateStub.setModified(new Date());
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(duplicateStub);

		ContactStub contactStub = new ContactStub();
		contactStub.setAngelId(1l);
		contactStub.setWillowId(1l);
		contactStub.setEntityIdentifier("Contact");
		contactStub.setCreated(new Date());
		contactStub.setModified(new Date());
		contactStub.setMarketingViaEmailAllowed(true);
		contactStub.setMarketingViaSMSAllowed(true);
		contactStub.setMarketingViaPostAllowed(true);
		contactStub.setFamilyName("familiName");
		contactStub.setGivenName("givenName");
		contactStub.setUniqueCode("code");
		contactStub.setStudentId(1l);
		contactStub.setTutorId(1l);
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(contactStub);


		StudentStub studentStub = new StudentStub();
		studentStub.setAngelId(1l);
		studentStub.setWillowId(1l);
		studentStub.setEntityIdentifier("Student");
		studentStub.setCreated(new Date());
		studentStub.setModified(new Date());
		studentStub.setContactId(1l);
		studentStub.setUsiStatus(UsiStatus.VERIFIED.getDatabaseValue());
		studentStub.setUsi("2222222222");
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(studentStub);

		TutorStub tutorStub = new TutorStub();
		tutorStub.setAngelId(1l);
		tutorStub.setWillowId(1l);
		tutorStub.setEntityIdentifier("Tutor");
		tutorStub.setCreated(new Date());
		tutorStub.setModified(new Date());
		tutorStub.setContactId(1l);
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(tutorStub);

		DeletedStub deletedContact = new DeletedStub();
		deletedContact.setEntityIdentifier("Contact");
		deletedContact.setWillowId(2l);
		deletedContact.setAngelId(2l);
		deletedContact.setCreated(new Date());
		deletedContact.setModified(new Date());
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(deletedContact);

		DeletedStub deletedStudent = new DeletedStub();
		deletedStudent.setEntityIdentifier("Student");
		deletedStudent.setWillowId(2l);
		deletedStudent.setAngelId(2l);
		deletedStudent.setCreated(new Date());
		deletedStudent.setModified(new Date());
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(deletedStudent);

		DeletedStub deletedTutor = new DeletedStub();
		deletedTutor.setEntityIdentifier("Tutor");
		deletedTutor.setWillowId(2l);
		deletedTutor.setAngelId(2l);
		deletedTutor.setCreated(new Date());
		deletedTutor.setModified(new Date());
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(deletedTutor);


		transactionGroup.getTransactionKeys().add(TransactionGroupProcessorImpl.MERGE_KEY);

		List<GenericReplicatedRecord> records = transactionGroupProcessor.processGroup(transactionGroup);
		assertEquals(1, records.size());
		assertEquals(SUCCESS,((ReplicatedRecord)records.get(0)).getStatus());


		ObjectContext context = cayenneService.newContext();
		ContactDuplicate duplicate = ObjectSelect.query(ContactDuplicate.class).where(ContactDuplicate.ANGEL_ID.eq(1l).andExp(ContactDuplicate.STATUS.eq(ContactDuplicateStatus.PROCESSED))).selectOne(context);
		assertNotNull(duplicate);
		Contact contactToDelete = SelectById.query(Contact.class,duplicate.getContactToDeleteId()).selectOne(context);
		assertNull(contactToDelete);
		Contact contactToUpdate = duplicate.getContactToUpdate();

		assertEquals(1, contactToUpdate.getToContacts().size());
		assertEquals(1l, contactToUpdate.getToContacts().get(0).getId().longValue());
		assertEquals(1, contactToUpdate.getFromContacts().size());
		assertEquals(3l, contactToUpdate.getFromContacts().get(0).getId().longValue());

		BinaryInfoRelation docRelation1 =  ObjectSelect.query(BinaryInfoRelation.class)
				.where(BinaryInfoRelation.ENTITY_IDENTIFIER.eq("Contact"))
				.and(BinaryInfoRelation.ENTITY_WILLOW_ID.eq(contactToUpdate.getId()))
				.and(BinaryInfoRelation.DOCUMENT.dot(Document.ANGEL_ID).eq(1l)).selectOne(context);

		assertNotNull(docRelation1);
		assertEquals(1l ,docRelation1.getId().longValue());

		BinaryInfoRelation docRelation2 =  ObjectSelect.query(BinaryInfoRelation.class)
				.where(BinaryInfoRelation.ENTITY_IDENTIFIER.eq("Contact"))
				.and(BinaryInfoRelation.ENTITY_WILLOW_ID.eq(contactToUpdate.getId()))
				.and(BinaryInfoRelation.DOCUMENT.dot(Document.ANGEL_ID).eq(2l)).selectOne(context);

		assertNotNull(docRelation2);
		assertEquals(3l, docRelation2.getId().longValue());


		List<TaggableTag> tagRelation1 =  ObjectSelect.query(TaggableTag.class)
				.where(TaggableTag.TAGGABLE.dot(Taggable.ENTITY_IDENTIFIER).eq("Contact"))
				.where(TaggableTag.TAGGABLE.dot(Taggable.ENTITY_WILLOW_ID).eq(contactToUpdate.getId()))
				.and(TaggableTag.TAG.dot(Tag.ANGEL_ID).eq(1l)).select(context);

		assertTrue(!tagRelation1.isEmpty());
		assertEquals(1l, tagRelation1.get(0).getId().longValue());
		assertEquals(1l ,tagRelation1.get(0).getTaggable().getId().longValue());


		List<TaggableTag> tagRelation2 =  ObjectSelect.query(TaggableTag.class)
				.where(TaggableTag.TAGGABLE.dot(Taggable.ENTITY_IDENTIFIER).eq("Contact"))
				.where(TaggableTag.TAGGABLE.dot(Taggable.ENTITY_WILLOW_ID).eq(contactToUpdate.getId()))
				.and(TaggableTag.TAG.dot(Tag.ANGEL_ID).eq(2l)).select(context);

		assertTrue(!tagRelation2.isEmpty());
		assertEquals(2l ,tagRelation2.get(0).getId().longValue());
		assertEquals(1l, tagRelation2.get(0).getTaggable().getId().longValue());

		List<AssessmentClassTutor> assessmentClassTutor = ObjectSelect.query(AssessmentClassTutor.class).select(context);
		assertEquals(2, assessmentClassTutor.size());

		assertEquals(Long.valueOf(1), assessmentClassTutor.get(0).getTutor().getId());
		assertEquals(Long.valueOf(1), assessmentClassTutor.get(1).getTutor().getId());


		List<AssessmentSubmission> assessmentSubmissions = ObjectSelect.query(AssessmentSubmission.class).select(context);
		assertEquals(2, assessmentClassTutor.size());

		assertEquals(Long.valueOf(1), assessmentSubmissions.get(0).getSubmittedBy().getId());
		assertEquals(Long.valueOf(1), assessmentSubmissions.get(1).getSubmittedBy().getId());


	}


	/**
	 * contact1 has customField of type1, contact2 has customField of type1 and type2. After merge
	 * contact1 must have customFields of type1 and type2. contact2 must be deleted.
	 */
	@Test
	public void customFieldMerge1() {
		GenericTransactionGroup transactionGroup = PortHelper.createTransactionGroup(V21);

		ContactDuplicateStub duplicateStub = new ContactDuplicateStub();
		duplicateStub.setAngelId(1L);
		duplicateStub.setStatus(ContactDuplicateStatus.IN_TRANSACTION.getDatabaseValue());
		duplicateStub.setDescription("Description");
		duplicateStub.setContactToUpdateId(1L);
		duplicateStub.setContactToDeleteWillowId(2L);
		duplicateStub.setContactToDeleteAngelId(2L);
		duplicateStub.setEntityIdentifier("ContactDuplicate");
		duplicateStub.setCreated(new Date());
		duplicateStub.setModified(new Date());
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(duplicateStub);

		ContactStub contactStub = new ContactStub();
		contactStub.setAngelId(1L);
		contactStub.setWillowId(1L);
		contactStub.setEntityIdentifier("Contact");
		contactStub.setCreated(new Date());
		contactStub.setModified(new Date());
		contactStub.setMarketingViaEmailAllowed(true);
		contactStub.setMarketingViaSMSAllowed(true);
		contactStub.setMarketingViaPostAllowed(true);
		contactStub.setFamilyName("familyName");
		contactStub.setGivenName("givenName");
		contactStub.setUniqueCode("code");
		contactStub.setStudentId(1L);
		contactStub.setTutorId(1L);
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(contactStub);

		DeletedStub deletedContact = new DeletedStub();
		deletedContact.setEntityIdentifier("Contact");
		deletedContact.setWillowId(2L);
		deletedContact.setAngelId(2L);
		deletedContact.setCreated(new Date());
		deletedContact.setModified(new Date());
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(deletedContact);

		CustomFieldTypeStub customFieldTypeStub1 = new CustomFieldTypeStub();
		customFieldTypeStub1.setEntityIdentifier("CustomFieldType");
		customFieldTypeStub1.setAngelId(1L);
		customFieldTypeStub1.setWillowId(1L);
		customFieldTypeStub1.setMandatory(false);
		customFieldTypeStub1.setCreated(new Date());
		customFieldTypeStub1.setModified(new Date());
		customFieldTypeStub1.setName("test");
		customFieldTypeStub1.setMandatory(false);
		customFieldTypeStub1.setEntityName("Contact");
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(customFieldTypeStub1);

		CustomFieldTypeStub customFieldTypeStub2 = new CustomFieldTypeStub();
		customFieldTypeStub2.setEntityIdentifier("CustomFieldType");
		customFieldTypeStub2.setAngelId(2L);
		customFieldTypeStub2.setWillowId(2L);
		customFieldTypeStub2.setCreated(new Date());
		customFieldTypeStub2.setModified(new Date());
		customFieldTypeStub2.setName("test");
		customFieldTypeStub2.setMandatory(false);
		customFieldTypeStub2.setEntityName("Contact");
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(customFieldTypeStub2);

		CustomFieldStub customFieldStub1 = new CustomFieldStub();
		customFieldStub1.setEntityIdentifier("CustomField");
		customFieldStub1.setCustomFieldTypeId(1L);
		customFieldStub1.setAngelId(1L);
		customFieldStub1.setWillowId(1L);
		customFieldStub1.setForeignId(1L);
		customFieldStub1.setCreated(new Date());
		customFieldStub1.setModified(new Date());
		customFieldStub1.setEntityName("Contact");
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(customFieldStub1);

		DeletedStub customFieldStub2 = new DeletedStub();
		customFieldStub2.setEntityIdentifier("CustomField");
		customFieldStub2.setAngelId(2L);
		customFieldStub2.setWillowId(2L);
		customFieldStub2.setCreated(new Date());
		customFieldStub2.setModified(new Date());
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(customFieldStub2);

		CustomFieldStub customFieldStub3 = new CustomFieldStub();
		customFieldStub3.setEntityIdentifier("CustomField");
		customFieldStub3.setCustomFieldTypeId(2L);
		customFieldStub3.setAngelId(3L);
		customFieldStub3.setWillowId(3L);
		customFieldStub3.setForeignId(1L);
		customFieldStub3.setCreated(new Date());
		customFieldStub3.setModified(new Date());
		customFieldStub3.setEntityName("Contact");
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(customFieldStub3);

		transactionGroup.getTransactionKeys().add(TransactionGroupProcessorImpl.MERGE_KEY);

		List<GenericReplicatedRecord> records = transactionGroupProcessor.processGroup(transactionGroup);
		assertEquals(1, records.size());
		assertEquals(SUCCESS,((ReplicatedRecord)records.get(0)).getStatus());

		ObjectContext context = cayenneService.newContext();

		Contact contact1 = ObjectSelect.query(Contact.class)
				.where(Contact.ANGEL_ID.eq(1L))
				.selectOne(context);

		CustomFieldType customFieldType1 = ObjectSelect.query(CustomFieldType.class)
				.where(CustomFieldType.ANGEL_ID.eq(1L))
				.selectOne(context);
		CustomFieldType customFieldType2 = ObjectSelect.query(CustomFieldType.class)
				.where(CustomFieldType.ANGEL_ID.eq(2L))
				.selectOne(context);


		List<ContactCustomField> contact1CustomFields = ObjectSelect.query(ContactCustomField.class)
				.where(ContactCustomField.RELATED_OBJECT.eq(contact1))
				.select(context);

		assertEquals(2, contact1CustomFields.size());
		assertEquals(customFieldType1, contact1CustomFields.get(0).getCustomFieldType());
		assertEquals(customFieldType2, contact1CustomFields.get(1).getCustomFieldType());

		Contact contact2 = ObjectSelect.query(Contact.class)
				.where(Contact.ANGEL_ID.eq(2L))
				.selectOne(context);

		assertNull(contact2);
	}

	/**
	 * contact1 has customField of type1 and type2, contact2 also  has customField of type1 and type2. After merge
	 * contact1 must have customFields of type1 and type2. contact2 must be deleted.
	 */
	@Test
	public void customFieldMerge2() {
		GenericTransactionGroup transactionGroup = PortHelper.createTransactionGroup(V21);

		ContactDuplicateStub duplicateStub = new ContactDuplicateStub();
		duplicateStub.setAngelId(1L);
		duplicateStub.setStatus(ContactDuplicateStatus.IN_TRANSACTION.getDatabaseValue());
		duplicateStub.setDescription("Description");
		duplicateStub.setContactToUpdateId(2L);
		duplicateStub.setContactToDeleteWillowId(3L);
		duplicateStub.setContactToDeleteAngelId(3L);
		duplicateStub.setEntityIdentifier("ContactDuplicate");
		duplicateStub.setCreated(new Date());
		duplicateStub.setModified(new Date());
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(duplicateStub);

		ContactStub contactStub = new ContactStub();
		contactStub.setAngelId(2L);
		contactStub.setWillowId(2L);
		contactStub.setEntityIdentifier("Contact");
		contactStub.setCreated(new Date());
		contactStub.setModified(new Date());
		contactStub.setMarketingViaEmailAllowed(true);
		contactStub.setMarketingViaSMSAllowed(true);
		contactStub.setMarketingViaPostAllowed(true);
		contactStub.setFamilyName("familyName");
		contactStub.setGivenName("givenName");
		contactStub.setUniqueCode("code");
		contactStub.setStudentId(1L);
		contactStub.setTutorId(1L);
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(contactStub);

		DeletedStub deletedContact = new DeletedStub();
		deletedContact.setEntityIdentifier("Contact");
		deletedContact.setWillowId(3L);
		deletedContact.setAngelId(3L);
		deletedContact.setCreated(new Date());
		deletedContact.setModified(new Date());
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(deletedContact);

		CustomFieldTypeStub customFieldTypeStub1 = new CustomFieldTypeStub();
		customFieldTypeStub1.setEntityIdentifier("CustomFieldType");
		customFieldTypeStub1.setAngelId(1L);
		customFieldTypeStub1.setWillowId(1L);
		customFieldTypeStub1.setMandatory(false);
		customFieldTypeStub1.setCreated(new Date());
		customFieldTypeStub1.setModified(new Date());
		customFieldTypeStub1.setName("test");
		customFieldTypeStub1.setMandatory(false);
		customFieldTypeStub1.setEntityName("Contact");
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(customFieldTypeStub1);

		CustomFieldTypeStub customFieldTypeStub2 = new CustomFieldTypeStub();
		customFieldTypeStub2.setEntityIdentifier("CustomFieldType");
		customFieldTypeStub2.setAngelId(2L);
		customFieldTypeStub2.setWillowId(2L);
		customFieldTypeStub2.setCreated(new Date());
		customFieldTypeStub2.setModified(new Date());
		customFieldTypeStub2.setName("test");
		customFieldTypeStub2.setMandatory(false);
		customFieldTypeStub2.setEntityName("Contact");
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(customFieldTypeStub2);

		DeletedStub customFieldStub3 = new DeletedStub();
		customFieldStub3.setEntityIdentifier("CustomField");
		customFieldStub3.setAngelId(4L);
		customFieldStub3.setWillowId(4L);
		customFieldStub3.setCreated(new Date());
		customFieldStub3.setModified(new Date());
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(customFieldStub3);

		DeletedStub customFieldStub4 = new DeletedStub();
		customFieldStub4.setEntityIdentifier("CustomField");
		customFieldStub4.setAngelId(5L);
		customFieldStub4.setWillowId(5L);
		customFieldStub4.setCreated(new Date());
		customFieldStub4.setModified(new Date());
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(customFieldStub4);


		transactionGroup.getTransactionKeys().add(TransactionGroupProcessorImpl.MERGE_KEY);

		List<GenericReplicatedRecord> records = transactionGroupProcessor.processGroup(transactionGroup);
		assertEquals(1, records.size());
		assertEquals(SUCCESS,((ReplicatedRecord)records.get(0)).getStatus());

		ObjectContext context = cayenneService.newContext();

		Contact contact1 = ObjectSelect.query(Contact.class)
				.where(Contact.ANGEL_ID.eq(2L))
				.selectOne(context);

		CustomFieldType customFieldType1 = ObjectSelect.query(CustomFieldType.class)
				.where(CustomFieldType.ANGEL_ID.eq(1L))
				.selectOne(context);
		CustomFieldType customFieldType2 = ObjectSelect.query(CustomFieldType.class)
				.where(CustomFieldType.ANGEL_ID.eq(2L))
				.selectOne(context);


		List<ContactCustomField> contact1CustomFields = ObjectSelect.query(ContactCustomField.class)
				.where(ContactCustomField.RELATED_OBJECT.eq(contact1))
				.select(context);

		assertEquals(2, contact1CustomFields.size());
		assertEquals(customFieldType1, contact1CustomFields.get(0).getCustomFieldType());
		assertEquals("test2", contact1CustomFields.get(0).getValue());
		assertEquals(customFieldType2, contact1CustomFields.get(1).getCustomFieldType());
		assertEquals("test3", contact1CustomFields.get(1).getValue());

		Contact contact2 = ObjectSelect.query(Contact.class)
				.where(Contact.ANGEL_ID.eq(3L))
				.selectOne(context);

		assertNull(contact2);
	}

	/**
	 * contact1 has customField of type1, contact2 has customField of type1 and type2. Angel don't create stud to delete customField of type1 from contact2
	 * After merge contact1 must have customFields of type1 and type2. contact2 must be deleted. customField of type1 from contact2 also must be deleted.
	 */
	@Test
	public void customFieldMerge3() {
		GenericTransactionGroup transactionGroup = PortHelper.createTransactionGroup(V21);

		ContactDuplicateStub duplicateStub = new ContactDuplicateStub();
		duplicateStub.setAngelId(1L);
		duplicateStub.setStatus(ContactDuplicateStatus.IN_TRANSACTION.getDatabaseValue());
		duplicateStub.setDescription("Description");
		duplicateStub.setContactToUpdateId(1L);
		duplicateStub.setContactToDeleteWillowId(2L);
		duplicateStub.setContactToDeleteAngelId(2L);
		duplicateStub.setEntityIdentifier("ContactDuplicate");
		duplicateStub.setCreated(new Date());
		duplicateStub.setModified(new Date());
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(duplicateStub);

		ContactStub contactStub = new ContactStub();
		contactStub.setAngelId(1L);
		contactStub.setWillowId(1L);
		contactStub.setEntityIdentifier("Contact");
		contactStub.setCreated(new Date());
		contactStub.setModified(new Date());
		contactStub.setMarketingViaEmailAllowed(true);
		contactStub.setMarketingViaSMSAllowed(true);
		contactStub.setMarketingViaPostAllowed(true);
		contactStub.setFamilyName("familyName");
		contactStub.setGivenName("givenName");
		contactStub.setUniqueCode("code");
		contactStub.setStudentId(1L);
		contactStub.setTutorId(1L);
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(contactStub);

		DeletedStub deletedContact = new DeletedStub();
		deletedContact.setEntityIdentifier("Contact");
		deletedContact.setWillowId(2L);
		deletedContact.setAngelId(2L);
		deletedContact.setCreated(new Date());
		deletedContact.setModified(new Date());
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(deletedContact);

		CustomFieldTypeStub customFieldTypeStub1 = new CustomFieldTypeStub();
		customFieldTypeStub1.setEntityIdentifier("CustomFieldType");
		customFieldTypeStub1.setAngelId(1L);
		customFieldTypeStub1.setWillowId(1L);
		customFieldTypeStub1.setMandatory(false);
		customFieldTypeStub1.setCreated(new Date());
		customFieldTypeStub1.setModified(new Date());
		customFieldTypeStub1.setName("test");
		customFieldTypeStub1.setMandatory(false);
		customFieldTypeStub1.setEntityName("Contact");
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(customFieldTypeStub1);

		CustomFieldTypeStub customFieldTypeStub2 = new CustomFieldTypeStub();
		customFieldTypeStub2.setEntityIdentifier("CustomFieldType");
		customFieldTypeStub2.setAngelId(2L);
		customFieldTypeStub2.setWillowId(2L);
		customFieldTypeStub2.setCreated(new Date());
		customFieldTypeStub2.setModified(new Date());
		customFieldTypeStub2.setName("test");
		customFieldTypeStub2.setMandatory(false);
		customFieldTypeStub2.setEntityName("Contact");
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(customFieldTypeStub2);

		CustomFieldStub customFieldStub1 = new CustomFieldStub();
		customFieldStub1.setEntityIdentifier("CustomField");
		customFieldStub1.setCustomFieldTypeId(1L);
		customFieldStub1.setAngelId(1L);
		customFieldStub1.setWillowId(1L);
		customFieldStub1.setForeignId(1L);
		customFieldStub1.setCreated(new Date());
		customFieldStub1.setModified(new Date());
		customFieldStub1.setEntityName("Contact");
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(customFieldStub1);

		CustomFieldStub customFieldStub3 = new CustomFieldStub();
		customFieldStub3.setEntityIdentifier("CustomField");
		customFieldStub3.setCustomFieldTypeId(2L);
		customFieldStub3.setAngelId(3L);
		customFieldStub3.setWillowId(3L);
		customFieldStub3.setForeignId(1L);
		customFieldStub3.setCreated(new Date());
		customFieldStub3.setModified(new Date());
		customFieldStub3.setEntityName("Contact");
		transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(customFieldStub3);

		transactionGroup.getTransactionKeys().add(TransactionGroupProcessorImpl.MERGE_KEY);

		List<GenericReplicatedRecord> records = transactionGroupProcessor.processGroup(transactionGroup);
		assertEquals(1, records.size());
		assertEquals(SUCCESS,((ReplicatedRecord)records.get(0)).getStatus());

		ObjectContext context = cayenneService.newContext();

		Contact contact1 = ObjectSelect.query(Contact.class)
				.where(Contact.ANGEL_ID.eq(1L))
				.selectOne(context);

		CustomFieldType customFieldType1 = ObjectSelect.query(CustomFieldType.class)
				.where(CustomFieldType.ANGEL_ID.eq(1L))
				.selectOne(context);
		CustomFieldType customFieldType2 = ObjectSelect.query(CustomFieldType.class)
				.where(CustomFieldType.ANGEL_ID.eq(2L))
				.selectOne(context);


		List<ContactCustomField> contact1CustomFields = ObjectSelect.query(ContactCustomField.class)
				.where(ContactCustomField.RELATED_OBJECT.eq(contact1))
				.select(context);

		assertEquals(2, contact1CustomFields.size());
		assertEquals(customFieldType1, contact1CustomFields.get(0).getCustomFieldType());
		assertEquals(customFieldType2, contact1CustomFields.get(1).getCustomFieldType());

		Contact contact2 = ObjectSelect.query(Contact.class)
				.where(Contact.ANGEL_ID.eq(2L))
				.selectOne(context);

		assertNull(contact2);

		CustomField customField2 = ObjectSelect.query(CustomField.class)
				.where(CustomField.ANGEL_ID.eq(2L))
				.selectOne(context);
		assertNull(customField2);
	}
}
