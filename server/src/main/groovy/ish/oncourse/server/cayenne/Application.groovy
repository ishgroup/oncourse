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

import ish.common.types.ApplicationStatus
import ish.common.types.ConfirmationStatus
import ish.common.types.NodeType
import ish.common.types.PaymentSource
import ish.math.Money
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._Application

import javax.annotation.Nonnull
/**
 * Applications are a way for students to express interest in a course and process through the steps of sending in
 * supporting materials, arranging interviews or getting custom pricing from the college. The college
 * can then track the status changes of the application through: <br>
 *
 *	* New<br>
 *  * Offered<br>
 *  * Accepted<br>
 *  * Rejected<br>
 *  * Withdrawn<br><br>
 *
 *  Once an application is "Offered" then the student is able to enrol online into any class for that course, at the
 *  special price (if any).
 *
 *  Tags can be used for more detailed workflow and tracking of progress.
 */
@API
@QueueableEntity
class Application extends _Application implements Queueable, NotableTrait, ExpandableTrait, AttachableTrait, ContactActivityTrait {


	@Override
	void addToAttachmentRelations(AttachmentRelation relation) {
		addToAttachmentRelations((ApplicationAttachmentRelation) relation)
	}

	@Override
	void removeFromAttachmentRelations(AttachmentRelation relation) {
		removeFromAttachmentRelations((ApplicationAttachmentRelation) relation)
	}

	@Override
	Class<? extends AttachmentRelation> getRelationClass() {
		return ApplicationAttachmentRelation.class
	}

	@Override
	void postAdd() {
		super.postAdd()
		if (getConfirmationStatus() == null) {
			setConfirmationStatus(ConfirmationStatus.DO_NOT_SEND)
		}
	}

	/**
	 * We track whether the confirmation email has been sent for this particular record in order to avoid sending it twice.
	 *
	 * @return status of confirmation email sending
	 */
	@Nonnull
	@API
	@Override
	ConfirmationStatus getConfirmationStatus() {
		return super.getConfirmationStatus()
	}

	/**
	 * @return the date and time this record was created
	 */
	@Nonnull
	@API
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	@Override
	String getInteractionName() {
		return course.name
	}
/**
	 * The Application can sometimes be valid for a specific length of time.
	 *
	 * @return date until when students can apply
	 */
	@API
	@Override
	Date getEnrolBy() {
		return super.getEnrolBy()
	}

	/**
	 * Class fee can be overridden for enrolments using applications.
	 *
	 * @return fee which student will pay when enrolling with this application if fee is overridden per application, null otherwise
	 */
	@API
	@Override
	Money getFeeOverride() {
		return super.getFeeOverride()
	}


	/**
	 * @return the date and time this record was modified
	 */
	@Nonnull
	@API
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	/**
	 * @return reason for decision (accept or reject) on this application
	 */
	@API
	@Override
	String getReason() {
		return super.getReason()
	}

	/**
	 * @return source where application was created: website or onCourse
	 */
	@Nonnull
	@API
	@Override
	PaymentSource getSource() {
		return super.getSource()
	}

	/**
	 * @return current status of the application
	 */
	@Nonnull
	@API
	@Override
	ApplicationStatus getStatus() {
		return super.getStatus()
	}

	/**
	 * Applications are always linked to a course rather than to a specific class.
	 *
	 * @return course to which this application is linked
	 */
	@Nonnull
	@API
	@Override
	Course getCourse() {
		return super.getCourse()
	}

	/**
	 * @return the student who lodged the application
	 */
	@Nonnull
	@API
	@Override
	Student getStudent() {
		return super.getStudent()
	}


	/**
	 * @return The list of tags assigned to application
	 */
	@Nonnull
	@API
	List<Tag> getTags() {
		List<Tag> tagList = new ArrayList<>(getTaggingRelations().size())
		for (ApplicationTagRelation relation : getTaggingRelations()) {
			if(relation.tag?.nodeType?.equals(NodeType.TAG))
				tagList.add(relation.getTag())
		}
		return tagList
	}

	@Override
	Class<? extends TagRelation> getTagRelationClass() {
		return ApplicationTagRelation.class
	}

	@Override
	String getSummaryDescription() {
		if(getStudent() == null || getStudent().getContact() == null) {
			return super.getSummaryDescription()
		} else {
			return getStudent().getContact().getName(true)
		}
	}

	@Override
	Class<? extends CustomField> getCustomFieldClass() {
		return ApplicationCustomField
	}
}



