/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.services;


import ish.common.types.ContactDuplicateStatus;
import ish.oncourse.model.*;
import ish.oncourse.webservices.util.GenericDeletedStub;
import ish.oncourse.webservices.util.GenericReplicatedRecord;
import ish.oncourse.webservices.util.GenericReplicationStub;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MergeProcessor {

	private List<GenericReplicationStub> stubs;
	private ObjectContext context;
	private GenericReplicationStub contactDuplicateStub;

	private Contact contactToUpdate;
	private Student studentToUpdate;
	private Tutor tutorToUpdate;

	private Contact contactToDelete;
	private Student studentToDelete;
	private Tutor tutorToDelete;
	
	private static final String CONTACT_IDENTIFIER = "Contact";
	private static final String STUDENT_IDENTIFIER = "Student";



	private MergeProcessor() {};
	
	public static MergeProcessor valueOf(ObjectContext context, List<GenericReplicationStub> stubs) {
		MergeProcessor processor = new MergeProcessor();
		processor.context = context;
		processor.stubs = new ArrayList<>(stubs);
		return processor;
	}

	public void processMerge(GenericReplicatedRecord contactDuplicateRec, GenericReplicatedRecord studentToUpdateRec, GenericReplicatedRecord tutorToUpdateRec) {
		
		ContactDuplicate contactDuplicate = SelectById.query(ContactDuplicate.class, contactDuplicateRec.getStub().getWillowId()).selectOne(context);
		
		contactToUpdate = contactDuplicate.getContactToUpdate();
		studentToUpdate = contactToUpdate.getStudent();
		tutorToUpdate = contactToUpdate.getTutor();
		
		if (contactDuplicate.getContactToDeleteId() != null) {
			contactToDelete = SelectById.query(Contact.class, contactDuplicate.getContactToDeleteId()).selectOne(context);
		} else if (contactDuplicate.getAngelId() != null) {
			contactToDelete = ObjectSelect.query(Contact.class).where(Contact.ANGEL_ID.eq(contactDuplicate.getAngelId())).and(Contact.COLLEGE.eq(contactDuplicate.getCollege())).selectOne(context);
			if (contactToDelete != null) {
				contactDuplicate.setContactToDeleteId(contactToDelete.getId());
			}
		} else {
			throw new IllegalStateException("ContactDuplicate has no reference to contact to delete");
		}
		
		if (contactToDelete == null) {
			return;
		} else if (!toDelete(CONTACT_IDENTIFIER, contactToDelete.getId(), contactToDelete.getAngelId())) {
			throw new IllegalStateException("Contact to delete  does not present in MERGE transactionGroup");
		}
		
		studentToDelete = contactToDelete.getStudent();
		tutorToDelete = contactToDelete.getTutor();
		

		for (CorporatePass corporatePass : new ArrayList<>(contactToDelete.getCorporatePasses())) {
			corporatePass.setContact(contactToUpdate);
		}
		for (CustomField customField : new ArrayList<>(contactToDelete.getCustomFields())) {
			customField.setRelatedObject(contactToUpdate);
		}
		for (Invoice invoice : new ArrayList<>(contactToDelete.getInvoices())) {
			invoice.setContact(contactToUpdate);
		}
		for (MessagePerson person : new ArrayList<>(contactToDelete.getMessagePeople())) {
			person.setContact(contactToUpdate);
		}
		for (PaymentIn paymentIn : new ArrayList<>(contactToDelete.getPaymentsIn())) {
			paymentIn.setContact(contactToUpdate);
		}
		for (PaymentOut paymentOut : new ArrayList<>(contactToDelete.getPaymentsOut())) {
			paymentOut.setContact(contactToUpdate);
		}
		for (ProductItem productItem : new ArrayList<>(contactToDelete.getProducts())) {
			productItem.setContact(contactToUpdate);
		}

		List<ContactDuplicate> duplicates = ObjectSelect.query(ContactDuplicate.class).where(ContactDuplicate.CONTACT_TO_UPDATE.eq(contactToDelete)).select(context);
		
		for (ContactDuplicate duplicate : new ArrayList<>(duplicates)) {
			duplicate.setContactToUpdate(contactToUpdate);
		}
		
		mergeDocumentRelation(CONTACT_IDENTIFIER, contactToDelete.getId(), contactToUpdate.getId(), contactToUpdate.getAngelId());
		mergeContactRelations();
		mergeTagRelations();

		if (studentToUpdateRec != null) {
			if (studentToUpdate != null && studentToUpdate.getId().equals(studentToUpdateRec.getStub().getWillowId())) {
				mergeStudents(studentToUpdate, studentToDelete);
			} else if (studentToDelete != null && studentToDelete.getId().equals(studentToUpdateRec.getStub().getWillowId())) {
				mergeStudents(studentToDelete, studentToUpdate);
			} else {
				throw new IllegalStateException("Unknown student to update");
			}
			
		} else if ((studentToUpdate != null && studentToDelete == null) || (studentToUpdate == null && studentToDelete == null))  {
			//nothing to relink
		} else if (studentToUpdate == null && studentToDelete != null) {
			mergeStudents(studentToDelete, studentToUpdate);
		} else {
			mergeStudents(studentToUpdate, studentToDelete);
		}


		if (tutorToUpdateRec != null) {
			if (tutorToUpdate != null && tutorToUpdate.getId().equals(tutorToUpdateRec.getStub().getWillowId())) {
				mergeTutor(tutorToUpdate, tutorToDelete);
			} else if (tutorToDelete != null && tutorToDelete.getId().equals(tutorToUpdateRec.getStub().getWillowId())) {
				mergeTutor(tutorToDelete, tutorToUpdate);
			} else {
				throw new IllegalStateException("Unknown tutor to update");
			}

		} else if ((tutorToUpdate != null && tutorToDelete == null) || (tutorToUpdate == null && tutorToDelete == null))  {
			//nothing to relink
		} else if (tutorToUpdate == null && tutorToDelete != null) {
			mergeTutor(tutorToDelete, tutorToUpdate);
		} else {
			mergeTutor(tutorToUpdate, tutorToDelete);
		}

		context.deleteObject(contactToDelete);
		contactDuplicate.setStatus(ContactDuplicateStatus.PROCESSED);
		StringBuilder builder = new StringBuilder();
		builder.append(contactDuplicate.getDescription());
		builder.append("\nContacts was successfully merged by willow side at: ");
		builder.append(new Date());
		contactDuplicate.setDescription(builder.toString());
		context.commitChanges();
	}

	private void mergeTutor(Tutor tutorToUpdate, Tutor tutorToDelete) {
		tutorToUpdate.setContact(contactToUpdate);
		if (tutorToDelete != null) {
			for (TutorRole tutorRole : new ArrayList<>(tutorToDelete.getTutorRoles())) {
				tutorRole.setTutor(tutorToUpdate);
			}
			for (SessionTutor sessionTutor : new ArrayList<>(tutorToDelete.getSessionTutors())) {
				sessionTutor.setTutor(tutorToUpdate);
			}

			for (Attendance attendance : new ArrayList<>(tutorToDelete.getAttendances())) {
				attendance.setMarkedByTutor(tutorToUpdate);
			}
			
			List<Outcome> outcomes = ObjectSelect.query(Outcome.class).where(Outcome.MARKED_BY_TUTOR.eq(tutorToDelete)).select(context);
			for (Outcome outcome : new ArrayList<>(outcomes)) {
				outcome.setMarkedByTutor(tutorToUpdate);
			}
			context.deleteObject(tutorToDelete);
		}
	}

	private void mergeStudents(Student studentToUpdate, Student studentToDelet) {
		studentToUpdate.setContact(contactToUpdate);
		
		if (studentToDelet != null) {
			for (Application application : new ArrayList<>(studentToDelet.getApplications())) {
				application.setStudent(studentToUpdate);
			}
			for (Attendance attendance : new ArrayList<>( studentToDelet.getAttendances())) {
				attendance.setStudent(studentToUpdate);
			}
			for (Certificate certificate : new ArrayList<>(studentToDelet.getCertificates())) {
				certificate.setStudent(studentToUpdate);
			}
			for (StudentConcession concession : new ArrayList<>(studentToDelet.getStudentConcessions())) {
				concession.setStudent(studentToUpdate);
			}
			for (Enrolment enrolment : new ArrayList<>(studentToDelet.getEnrolments())) {
				enrolment.setStudent(studentToUpdate);
			}
			for (PriorLearning priorLearning : new ArrayList<>(studentToDelet.getPriorLearnings())) {
				priorLearning.setStudent(studentToUpdate);
			}
			for (WaitingList waitingList : new ArrayList<>(studentToDelet.getWaitingLists())) {
				waitingList.setStudent(studentToUpdate);
			}
			mergeDocumentRelation(STUDENT_IDENTIFIER, contactToDelete.getId(), contactToUpdate.getId(), contactToUpdate.getAngelId());
			context.deleteObject(studentToDelet);
		}
	}

	public GenericReplicationStub getContactDuplicateStub() {
		if (contactDuplicateStub == null) {
			List<GenericReplicationStub> contactDuplicateStubs = getStubBy(ContactDuplicate.class.getSimpleName(), false);

			if (contactDuplicateStubs.size() == 1) {
				contactDuplicateStub = contactDuplicateStubs.get(0);
				return contactDuplicateStub;
			} else {
				throw new IllegalStateException("Merge transaction does not contains/contains more than one contactDuplicate stub"); 
			} 
		}
		return contactDuplicateStub;
	}

	public List<GenericReplicationStub> getStubBy(String entityIdentifier, boolean isDeleteStub) {
		List<GenericReplicationStub> stubList = new LinkedList<>();
		for (GenericReplicationStub stub : stubs) {
			if (entityIdentifier.equals(stub.getEntityIdentifier()) &&
					((isDeleteStub && stub instanceof GenericDeletedStub) || (!isDeleteStub && !(stub instanceof GenericDeletedStub)))) {
				stubList.add(stub);
			}
		}
		return stubList;
	}
	
	private boolean toDelete (String entityIdentifier, Long willowId, Long angelId) {
		for (GenericReplicationStub stub :  getStubBy(entityIdentifier, true)) {
			if (willowId.equals(stub.getWillowId()) || (angelId != null && angelId.equals(stub.getAngelId()))) {
				return true;
			}
		}
		return false;
	}

	private void mergeDocumentRelation(String entityIdentifier, Long toDeleteWillowId, Long toUpdateWillowId, Long toUpdateAngelId  ) {

		List<BinaryInfoRelation> documentRelations = ObjectSelect.query(BinaryInfoRelation.class)
				.where(BinaryInfoRelation.ENTITY_IDENTIFIER.eq(entityIdentifier)).and(BinaryInfoRelation.ENTITY_WILLOW_ID.eq(toDeleteWillowId)).select(context);

		for (BinaryInfoRelation documentRelation : new ArrayList<>(documentRelations)) {
			if (toDelete(entityIdentifier + "AttachmentRelation", documentRelation.getId(), documentRelation.getAngelId())) {
				context.deleteObject(documentRelation);
			} else {
				List<BinaryInfoRelation> existingRelations = ObjectSelect.query(BinaryInfoRelation.class)
						.where(BinaryInfoRelation.ENTITY_IDENTIFIER.eq(entityIdentifier))
						.and(BinaryInfoRelation.ENTITY_WILLOW_ID.eq(toUpdateWillowId))
						.and(BinaryInfoRelation.DOCUMENT.eq(documentRelation.getDocument())).select(context);
				if (existingRelations.size() > 0) {
					context.deleteObject(documentRelation);
				} else {
					documentRelation.setEntityWillowId(toUpdateWillowId);
					documentRelation.setEntityAngelId(toUpdateAngelId);
				}
			}
		}
	}

	private void mergeContactRelations() {
		List<ContactRelation> toContactRelations = contactToDelete.getToContacts();
		
		for (ContactRelation toContactRelation : new ArrayList<>(toContactRelations)) {
			if (toDelete("ContactRelation", toContactRelation.getId(), toContactRelation.getAngelId())) {
				context.deleteObject(toContactRelation);
			} else {

				List<ContactRelation> existingRelations = ContactRelation.TO_CONTACT.eq(toContactRelation.getToContact())
						.andExp(ContactRelation.RELATION_TYPE.eq(toContactRelation.getRelationType()))
						.filterObjects(contactToUpdate.getToContacts());
				if (existingRelations.size() > 0) {
					context.deleteObject(toContactRelation);
				} else {
					toContactRelation.setFromContact(contactToUpdate);
				}
			}
		}

		List<ContactRelation> fromContactRelations = contactToDelete.getFromContacts();

		for (ContactRelation fromContactRelation : new ArrayList<>(fromContactRelations)) {
			if (toDelete("ContactRelation", fromContactRelation.getId(), fromContactRelation.getAngelId())) {
				context.deleteObject(fromContactRelation);
			} else {
				List<ContactRelation> existingRelations = ContactRelation.FROM_CONTACT.eq(fromContactRelation.getFromContact())
						.andExp(ContactRelation.RELATION_TYPE.eq(fromContactRelation.getRelationType()))
						.filterObjects(contactToUpdate.getFromContacts());
				if (existingRelations.size() > 0) {
					context.deleteObject(fromContactRelation);
				} else {
					fromContactRelation.setToContact(contactToUpdate);
				}
			}
		}
	}

	private void mergeTagRelations() {
		List<TaggableTag> taggables = ObjectSelect.query(TaggableTag.class)
				.where(TaggableTag.TAGGABLE.dot(Taggable.ENTITY_IDENTIFIER).eq(CONTACT_IDENTIFIER)).and(TaggableTag.TAGGABLE.dot(Taggable.ENTITY_WILLOW_ID).eq(contactToDelete.getId())).select(context);
		for (TaggableTag taggableTag : new ArrayList<>(taggables)) {
			if (toDelete("ContactTagRelation", taggableTag.getTaggable().getId(), taggableTag.getTaggable().getAngelId())) {
				context.deleteObject(taggableTag.getTaggable());
				context.deleteObject(taggableTag);
			} else {
				List<TaggableTag> existingTaggables = ObjectSelect.query(TaggableTag.class)
						.where(TaggableTag.TAGGABLE.dot(Taggable.ENTITY_IDENTIFIER).eq(CONTACT_IDENTIFIER))
						.and(TaggableTag.TAGGABLE.dot(Taggable.ENTITY_WILLOW_ID).eq(contactToUpdate.getId()))
						.and(TaggableTag.TAG.eq(taggableTag.getTag())).select(context);
				if (existingTaggables.size() > 0) {
					context.deleteObject(taggableTag.getTaggable());
					context.deleteObject(taggableTag);
				} else {
					taggableTag.getTaggable().setEntityWillowId(contactToUpdate.getId());
					taggableTag.getTaggable().setEntityAngelId(contactToUpdate.getAngelId());
				}
			}
		}
	}
}
