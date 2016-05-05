/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.services;

import ish.common.types.ContactDuplicateStatus;
import ish.common.types.UsiStatus;
import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.*;
import ish.oncourse.webservices.replication.builders.WillowStubBuilderTest;
import ish.oncourse.webservices.soap.v4.ReplicationTestModule;
import ish.oncourse.webservices.util.GenericReplicatedRecord;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.util.PortHelper;
import ish.oncourse.webservices.v13.stubs.replication.ContactDuplicateStub;
import ish.oncourse.webservices.v13.stubs.replication.*;
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


import static ish.oncourse.webservices.util.SupportedVersions.*;
import static ish.oncourse.webservices.v13.stubs.replication.Status.SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class MergeProcessorTest extends ServiceTest {


	ITransactionGroupProcessor transactionGroupProcessor;
	ICayenneService cayenneService;

	
	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.webservices.services", "", ReplicationTestModule.class);

		InputStream st = WillowStubBuilderTest.class.getClassLoader().getResourceAsStream("ish/oncourse/webservices/replication/services/MergeProcessorTestDataSet.xml");
		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);

		DataSource onDataSource = getDataSource("jdbc/oncourse");
		DatabaseConnection dbConnection = new DatabaseConnection(onDataSource.getConnection(), null);
		dbConnection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);

		DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);

		transactionGroupProcessor = getService(ITransactionGroupProcessor.class);
		cayenneService = getService(ICayenneService.class);

	}
	
	@Test
	public void processMergeTransaction() {
		
		
		GenericTransactionGroup transactionGroup = PortHelper.createTransactionGroup(V13);

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


		TaggableTag tagRelation1 =  ObjectSelect.query(TaggableTag.class)
				.where(TaggableTag.TAGGABLE.dot(Taggable.ENTITY_IDENTIFIER).eq("Contact"))
				.where(TaggableTag.TAGGABLE.dot(Taggable.ENTITY_WILLOW_ID).eq(contactToUpdate.getId()))
				.and(TaggableTag.TAG.dot(Tag.ANGEL_ID).eq(1l)).selectOne(context);
		
		assertNotNull(tagRelation1);
		assertEquals(1l, tagRelation1.getId().longValue());
		assertEquals(1l ,tagRelation1.getTaggable().getId().longValue());


		TaggableTag tagRelation2 =  ObjectSelect.query(TaggableTag.class)
				.where(TaggableTag.TAGGABLE.dot(Taggable.ENTITY_IDENTIFIER).eq("Contact"))
				.where(TaggableTag.TAGGABLE.dot(Taggable.ENTITY_WILLOW_ID).eq(contactToUpdate.getId()))
				.and(TaggableTag.TAG.dot(Tag.ANGEL_ID).eq(2l)).selectOne(context);

		assertNotNull(tagRelation2);
		assertEquals(3l ,tagRelation2.getId().longValue());
		assertEquals(3l, tagRelation2.getTaggable().getId().longValue());
	}
}
