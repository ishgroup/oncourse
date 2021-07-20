/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.cayenne

import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._AttachmentRelation

import javax.annotation.Nonnull

/**
 * Object representing relation between a record and document version.
 */
@QueueableEntity
abstract class AttachmentRelation extends _AttachmentRelation implements Queueable {



	/**
	 * @return attached record
	 */
	@API
	abstract AttachableTrait getAttachedRelation()

	/**
	 * @return entity identifier of the attached record
	 */
	@Nonnull
	@API
	@Override
	abstract String getEntityIdentifier();

	/**
	 * @see ish.oncourse.server.cayenne.glue.CayenneDataObject#postAdd()
	 */
	@Override
	protected void postAdd() {
		super.postAdd()
		if (super.getEntityIdentifier() == null) {
			setEntityIdentifier(getEntityIdentifier())
		}
	}

	abstract void setAttachedRelation(AttachableTrait attachable)

	@Override
	String getSummaryDescription() {
		if(getDocument() == null) {
			return super.getSummaryDescription()
		}
		return getDocument().getName()
	}
	
	Contact getContact() {
		switch (getClass()) {
			case ContactAttachmentRelation:
				return (this as ContactAttachmentRelation).attachedContact
			case StudentAttachmentRelation:
				return (this as StudentAttachmentRelation).attachedStudent.contact
			case TutorAttachmentRelation:
				return (this as TutorAttachmentRelation).attachedTutor.contact
			case InvoiceAttachmentRelation:
				return (this as InvoiceAttachmentRelation).attachedInvoice.contact
			case ApplicationAttachmentRelation:
				return (this as ApplicationAttachmentRelation).attachedApplication.student.contact
			case PriorLearningAttachmentRelation:
				return (this as PriorLearningAttachmentRelation).attachedPriorLearning.student.contact
			case CertificateAttachmentRelation:
				return (this as CertificateAttachmentRelation).attachedCertificate.student.contact
			case EnrolmentAttachmentRelation:
				return (this as EnrolmentAttachmentRelation).attachedEnrolment.student.contact
			case LeadAttachmentRelation:
				return (this as LeadAttachmentRelation).attachedLead.customer
			default: 
				return null
		}
	}
}
