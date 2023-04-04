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
import ish.oncourse.server.api.v1.function.CartFunctions
import ish.oncourse.server.api.validation.EntityValidator
import ish.oncourse.server.cayenne.glue._WaitingList
import org.apache.cayenne.query.ObjectSelect

import javax.annotation.Nonnull
/**
 * A waiting list represents a student's interest in a course.
 *
 * Typically students add themselves to a waiting list when they don't want any of the class
 * dates or locations currently on offer. Or just want to be reminded in the future for new availability.
 *
 */
@API
@QueueableEntity
class WaitingList extends _WaitingList implements Queueable, ExpandableTrait, ContactActivityTrait {
	@Override
	protected void postPersist() {
		super.postPersist()

		List<CheckoutContactRelation> checkoutRelations = CartFunctions.checkoutsByContactId(context, student.contact.id)

		context.deleteObjects(checkoutRelations
				.findAll { it instanceof CheckoutWaitingCourseRelation && it.relatedObjectId == course.id }
				.collect {it.checkout}.unique())
		context.commitChanges()
	}

	@Override
	protected void prePersist() {
		super.prePersist()
		validateDuplicate()
	}

	@Override
	protected void preUpdate() {
		super.preUpdate()
		validateDuplicate()
	}

	private void validateDuplicate(){
		def sameWaitingList = ObjectSelect.query(WaitingList)
				.where(WaitingList.ID.ne(id).andExp(WaitingList.STUDENT.eq(student)).andExp(WaitingList.COURSE.eq(course)))
				.selectFirst(context)
		if(sameWaitingList != null){
			EntityValidator.throwClientErrorException("student","Waiting list for this student and course already exists!")
		}
	}

	/**
	 * @return the date and time this record was created
	 */
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
	 * @return the date and time this record was modified
	 */
	@API
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	/**
	 * @return internal notes created by the staff
	 */
	@API
	@Override
	String getNotes() {
		return super.getNotes()
	}

	/**
	 * @return the number of enrolments (including the primary student)
	 */
	@Nonnull
	@API
	@Override
	Integer getStudentCount() {
		return super.getStudentCount()
	}

	/**
	 * @return notes the student entered when creating the waiting list
	 */
	@API
	@Override
	String getStudentNotes() {
		return super.getStudentNotes()
	}

	/**
	 * @return the course the student is waiting for
	 */
	@Nonnull
	@API
	@Override
	Course getCourse() {
		return super.getCourse()
	}

	/**
	 * @return a list of preferred sites (can be empty)
	 */
	@Nonnull
	@API
	@Override
	List<Site> getSites() {
		return super.getSites()
	}

	/**
	 * @return the student who is waiting
	 */
	@Nonnull
	@API
	@Override
	Student getStudent() {
		return super.getStudent()
	}

	/**
	 * @return
	 */
	@Nonnull
	@Override
	List<WaitingListSite> getWaitingListSites() {
		return super.getWaitingListSites()
	}

	@Override
	Class<? extends CustomField> getCustomFieldClass() {
		return WaitingListCustomField
	}

	@Override
	Class<? extends TagRelation> getTagRelationClass() {
		return WaitingListTagRelation.class
	}
}
